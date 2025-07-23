package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.subscriber;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

public interface ISubscriber extends IMqttMessageListener{
	
	public boolean isMessageReceived();
	
	public String getSubscriptionTopic();
}
