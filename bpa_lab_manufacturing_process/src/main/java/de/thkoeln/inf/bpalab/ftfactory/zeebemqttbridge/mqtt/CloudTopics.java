package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.FtfactoryMQTTClient;

@Component
public class CloudTopics {
			
// Topic for publishing order
	private String cloudPubOrderTopic;

	public String getCloudPubOrderTopic() {
		return cloudPubOrderTopic;
	}

	@Value("${ftfactorymqttclient.cloudPubOrderTopic}")
	public void setCloudPubOrderTopic(String cloudPubOrderTopic) {
		this.cloudPubOrderTopic = cloudPubOrderTopic;
	}
	
// Topic for publishing broadcast
	private String cloudBroadcastTopic;

	public String getCloudBroadcastTopic() {
		return cloudBroadcastTopic;
	}

	@Value("${ftfactorymqttclient.cloudBroadcastTopic}")
	public void setCloudBroadcastTopic(String cloudBroadcastTopic) {
		this.cloudBroadcastTopic = cloudBroadcastTopic;
	}
	
// Topic for subscribing order
	private String cloudSubOrderTopic;

	public String getCloudSubOrderTopic() {
		return cloudSubOrderTopic;
	}

	@Value("${ftfactorymqttclient.cloudSubOrderTopic}")
	public void setCloudSubOrderTopic(String cloudSubOrderTopic) {
		this.cloudSubOrderTopic = cloudSubOrderTopic;
	}
	
// Topic for subscribing HBWStorage
	private String cloudHBWTopic;

	public String getCloudHBWTopic() {
		return cloudHBWTopic;
	}

	@Value("${ftfactorymqttclient.cloudHBWTopic}")
	public void setCloudHBWTopic(String cloudHBWTopic) {
		this.cloudHBWTopic = cloudHBWTopic;
	}
	
// Topic for subscribing HBWStorage
	private String cloudBME680Topic;

	public String getCloudBME680Topic() {
		return cloudBME680Topic;
	}

	@Value("${ftfactorymqttclient.cloudBME680Topic}")
	public void setCloudBME680Topic(String cloudBME680Topic) {
		this.cloudBME680Topic = cloudBME680Topic;
	}

}
