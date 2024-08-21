package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
//import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.TopicPayload;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.publisher.*;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber.FtfactoryBME680;

import jakarta.annotation.PostConstruct;

@Component
public class FtfactoryMQTTClient {
	private static Logger log = LoggerFactory.getLogger(FtfactoryMQTTClient.class);

    private MemoryPersistence persistence = new MemoryPersistence();    	
	private MqttClient mqttClient;
	private MqttConnectOptions connOpts;

	@Autowired
	private FtfactoryBroadcast ftfactoryBroadcast;
	
	public FtfactoryMQTTClient(@Value("${ftfactorymqttclient.broker}") final String broker, @Value("${ftfactorymqttclient.clientId}") final String clientId) {
		try {
			mqttClient = new MqttClient(broker, clientId, persistence);
			connOpts = new MqttConnectOptions();
			// if true, removes subscriptions after reconnect 
			connOpts.setCleanSession(false);
			connOpts.setAutomaticReconnect(true);
			mqttClient.connect(connOpts);
			log.info(">>>\nConnected to broker: {}", broker);
			
		} catch(MqttException me) {
    		log.info("\nreason{} msg{} loc{} cause{} exep", me.getReasonCode(), me.getMessage(), me.getLocalizedMessage(), me.getCause(), me);
	    }
		
	}

	@PostConstruct
	public void postConstruct() {
		publish(ftfactoryBroadcast.getTopicPayload());
	}
	
	public boolean isConnected() {
		return this.mqttClient.isConnected();
	}

    public void publish(TopicPayload topicPayload) { //throws MqttException {
    	try {
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
			this.mqttClient.subscribe(subTopic, listener);
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
