from kafka import KafkaProducer
import json
import requests
import time
import logging

# Logging-Konfiguration
logging.basicConfig(level=logging.INFO)

def get_auth_cookie():
    url = 'http://operate:8080/api/login'
    credentials = {'username': 'demo', 'password': 'demo'}
    try:
        response = requests.post(url, params=credentials)
        # logging.info(f"Login-Antwort Status: {response.status_code}, Antworttext: {response.text}")
        if response.status_code == 204:
            csrf_token = response.headers.get('operate-x-csrf-token')
            cookies = response.cookies
            return cookies, csrf_token
        else:
            raise Exception(f"Fehler beim Abrufen des Authentifizierungs-Cookies: {response.status_code}")
    except Exception as e:
        logging.error(f"Fehler beim Abrufen des Auth-Cookies: {e}")
        raise e

def fetch_camunda_events(cookies, csrf_token, last_event_key=None):
    url = 'http://operate:8080/v1/flownode-instances/search'
    headers = {
        'Content-Type': 'application/json',
        'operate-x-csrf-token': csrf_token
    }
    payload = {
        "size": 50
    }
    if last_event_key:
        payload["searchAfter"] = [last_event_key]  # Verwenden des letzten Event-Schlüssels
    try:
        response = requests.post(url, headers=headers, json=payload, cookies=cookies)
        if response.status_code == 200:
            events = response.json().get('items', [])
            sort_values = response.json().get('sortValues', [])
            logging.info(f"Erfolgreich {len(events)} Events von Camunda Operate abgerufen.")
            last_event_key = sort_values[-1] if sort_values else last_event_key  # Setzen des neuen letzten Event-Schlüssels
            return events, last_event_key
        else:
            logging.error(f"Fehler beim Abrufen von Camunda Events: {response.status_code}, {response.text}")
            return [], last_event_key
    except Exception as e:
        logging.error(f"Fehler beim Abrufen der Camunda Events: {e}")
        return [], last_event_key

def fetch_all_variables(cookies, csrf_token, last_variable_key=None):
    url = 'http://operate:8080/v1/variables/search'
    headers = {'Content-Type': 'application/json', 'operate-x-csrf-token': csrf_token}
    payload = {
        "size": 50,
        "filter": {
            "name": "orderID"  # Filtere nur die Variablen mit dem Namen "orderID"
        }
    }
    if last_variable_key:
        payload["searchAfter"] = [last_variable_key]  # Verwenden des letzten Variablen-Schlüssels
    try:
        response = requests.post(url, headers=headers, json=payload, cookies=cookies)
        if response.status_code == 200:
            variables = response.json().get('items', [])
            sort_values = response.json().get('sortValues', [])
            logging.info(f"Erfolgreich {len(variables)} Variablen abgerufen.")
            last_variable_key = sort_values[-1] if sort_values else last_variable_key  # Setzen des neuen letzten Variablen-Schlüssels
            return variables, last_variable_key
        else:
            logging.error(f"Fehler beim Abrufen der Variablen: {response.status_code}, {response.text}")
            return [], last_variable_key
    except Exception as e:
        logging.error(f"Fehler beim Abrufen der Variablen: {e}")
        return [], last_variable_key

# Set zum Speichern der bereits verarbeiteten Event-Keys
processed_event_keys = set()

def send_events_to_kafka(events, producer, topic):
    global processed_event_keys
    new_events_processed = False
    for event in events:
        event_key = str(event['key'])
        if event_key not in processed_event_keys:
            try:
                producer.send(topic, key=event_key.encode('utf-8'), value=event)
                logging.info(f"Event gesendet: {event_key}")
                processed_event_keys.add(event_key)  # Key zu verarbeiteten Events hinzufügen
                new_events_processed = True
            except Exception as e:
                logging.error(f"Fehler beim Senden an Kafka: {e}")
    
    if not new_events_processed:
        logging.info("Alle bisherigen Events wurden bereits verarbeitet.")

def main():
    # Kafka-Producer-Konfiguration
    producer = KafkaProducer(
        bootstrap_servers='kafka:9092',
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )

    last_event_key = None
    last_variable_key = None

    try:
        cookies, csrf_token = get_auth_cookie()
        while True:
            variables, last_variable_key = fetch_all_variables(cookies, csrf_token, last_variable_key)
            events, last_event_key = fetch_camunda_events(cookies, csrf_token, last_event_key)
            if variables:
                send_events_to_kafka(variables, producer, 'camunda-variables')
            if events:
                send_events_to_kafka(events, producer, 'camunda-events')
            if not events and not variables:
                logging.info("Keine neuen Events oder Variablen zum Senden.")
            # time.sleep(5) # Abfrage alle 5 Sekunden
    except KeyboardInterrupt:
        logging.info("Producer wurde vom Benutzer gestoppt.")
    except Exception as e:
        logging.error(f"Unbehandelter Fehler: {e}")
    finally:
        producer.flush()
        producer.close()
        logging.info("Producer wurde geschlossen.")

if __name__ == '__main__':
    main()