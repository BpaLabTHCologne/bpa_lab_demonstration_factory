package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FtwarehouseMQTTClient {
	private static Logger log = LoggerFactory.getLogger(FtwarehouseMQTTClient.class);

    private MemoryPersistence persistence = new MemoryPersistence();    	
	private MqttClient mqttClient;
	private MqttConnectOptions connOpts;

	public FtwarehouseMQTTClient(@Value("${ftwarehousemqttclient.broker:ws://10.0.0.21:8081}") final String broker,
                                 @Value("${ftwarehousemqttclient.clientId:FTWarehouseMQTTClient}") final String clientId) {
		try {
			mqttClient = new MqttClient(broker, clientId, persistence);
			connOpts = new MqttConnectOptions();
			// if true, removes subscriptions after reconnect 
			connOpts.setCleanSession(false);
			connOpts.setAutomaticReconnect(true);
			connOpts.setConnectionTimeout(5);
			mqttClient.connect(connOpts);
			log.info(">>>\nConnected to broker: {}", broker);
			
		} catch(MqttException me) {
			log.info(">>>\nNot Connected to broker: {} !!!!", broker);
	    }
		
	}

//	@PostConstruct
//	public void postConstruct() {
//		if (mqttClient.isConnected())
//	}
	
	public boolean isConnected() {
		if (mqttClient != null)
			return this.mqttClient.isConnected();
		return false;
	}

    public void publish(TopicPayload topicPayload) { //throws MqttException {
    	try {
			log.info("\npublish messsage{} payload{}", topicPayload.getTopic(), topicPayload.mqttMessage());
    		mqttClient.publish(topicPayload.getTopic(), topicPayload.mqttMessage());
		} catch(MqttException me) {
    		log.info("\nreason{} msg{} loc{} cause{} exep", me.getReasonCode(), me.getMessage(), me.getLocalizedMessage(), me.getCause(), me);
	    }
    }

    public void publish(TopicPayload topicPayload, int qos) {
    	try {
	    	// Send the message to the server, control is not returned until
	    	// it has been delivered to the server meeting the specified
	    	// quality of service.
    		mqttClient.publish(topicPayload.getTopic(), topicPayload.mqttMessage().getPayload(), qos, false);    	
		} catch(MqttException me) {
    		log.info("\nreason{} msg{} loc{} cause{} exep", me.getReasonCode(), me.getMessage(), me.getLocalizedMessage(), me.getCause(), me);
	    }
    }

	public void subscribe(String subTopic, IMqttMessageListener listener) {
		try {
			this.mqttClient.subscribe(subTopic, 0, listener);
            log.info("Subscribing to topic: {}", subTopic);
		} catch(MqttException me) {
    		log.info("\nreason{} msg{} loc{} cause{} exep", me.getReasonCode(), me.getMessage(), me.getLocalizedMessage(), me.getCause(), me);
	    }
	}

	public void unsubscribe(String subTopic) {
		try {
			this.mqttClient.unsubscribe(subTopic);
		} catch(MqttException me) {
    		log.info("\nreason{} msg{} loc{} cause{} exep", me.getReasonCode(), me.getMessage(), me.getLocalizedMessage(), me.getCause(), me);
	    }
	}    
}
