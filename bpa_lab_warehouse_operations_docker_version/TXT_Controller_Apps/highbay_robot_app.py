import paho.mqtt.client as mqtt
import json
from domain import HighbayDomain
import sys
import os

if (os.getenv('IS_PROD') == 'true'):
    highbay_domain = HighbayDomain()

    # Perform retrieval
    def fetch_bicycle_from_shelf(place_id):
        while not highbay_domain.get_product(place_id):
            continue

    def move_bicycle_to_dock():
        while not highbay_domain.place_product():
            continue

    # Perform storage
    def pick_bicycle():
        while not highbay_domain.pick_product():
            continue

    def store_bicycle_to_shelf(place_id):
        while not highbay_domain.put_product(place_id):
            continue


# Functions for MQTT
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
    client.subscribe("Warehouse_Operations/start")

def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.payload))

    # Load the received message
    message = json.loads(msg.payload)

    # Perform the task based on the function parameter (Test version: robot action are commented for testing without robot)
    if message['function'] == 'fetch_bicycle_from_shelf': 
        if (os.getenv('IS_PROD') == 'true'):
            fetch_bicycle_from_shelf(message['place_id'])
        print("Bicycle fetched from shelf!")

    elif message['function'] == 'move_bicycle_to_dock': 
        if (os.getenv('IS_PROD') == 'true'):
            move_bicycle_to_dock()
        print ("Bicycle moved to dock!")

    elif message['function'] == 'pick_bicycle':
        if (os.getenv('IS_PROD') == 'true'):
            pick_bicycle() 
        print("Bicycle picked up!")    

    elif message['function'] == 'store_bicycle_to_shelf':
        if (os.getenv('IS_PROD') == 'true'):
            highbay_domain.put_product(message['place_id']) 
        print("Bicycle stored to shelf!")

    # Response that is sent once the task is executed
    completed_message = {
    'function': message['function'],
    'place_id': message['place_id'],
    'task_type': 'completed'
    }

    # Send the response that the task is completed
    client.publish('Warehouse_Operations/ready', json.dumps(completed_message))


# MQTT client is started and waits for message from the workers
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

if (os.getenv('IS_PROD') == 'true'):
    if client.connect("10.0.0.21", 1883, 60) != 0:
        print("Could not establish connection with the MQTT broker!")
        sys.exit(-1)
else:
    if client.connect("mqtt-broker", 1883, 60) != 0:
        print("Could not establish connection with the MQTT broker!")
        sys.exit(-1)

# Block the network loop to keep the script running
client.loop_forever()
