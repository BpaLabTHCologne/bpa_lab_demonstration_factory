from kafka import KafkaConsumer

# Kafka-Consumer-Konfiguration
consumer = KafkaConsumer(
    'camunda-events', 'camunda-variables',
    bootstrap_servers=['kafka:9092'],
    auto_offset_reset='earliest',
    enable_auto_commit=True,
    group_id='camunda-event-reader',
    value_deserializer=lambda x: x.decode('utf-8')
)

# Nachrichten konsumieren und anzeigen
for message in consumer:
    print(f"Topic: {message.topic}, Key: {message.key}, Value: {message.value}")