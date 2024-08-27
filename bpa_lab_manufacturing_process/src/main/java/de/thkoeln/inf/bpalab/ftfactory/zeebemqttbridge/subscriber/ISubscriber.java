package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.FtfactoryMQTTClient;

public interface ISubscriber extends IMqttMessageListener{
	
	public boolean isMessageReceived();
	
	public String getSubscriptionTopic();
}
