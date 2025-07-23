package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CloudTopics {
			
// Topic for publishing order
	private String cloudPubOrderTopic;

	public String getCloudPubOrderTopic() {
		return cloudPubOrderTopic;
	}

	@Value("${ftfactorymqttclient.cloudPubOrderTopic:bpalab/ftfactory/f/o/order}")
	public void setCloudPubOrderTopic(String cloudPubOrderTopic) {
		this.cloudPubOrderTopic = cloudPubOrderTopic;
	}
	
// Topic for publishing broadcast
	private String cloudBroadcastTopic;

	public String getCloudBroadcastTopic() {
		return cloudBroadcastTopic;
	}

	@Value("${ftfactorymqttclient.cloudBroadcastTopic:bpalab/ftfactory/o/broadcast}")
	public void setCloudBroadcastTopic(String cloudBroadcastTopic) {
		this.cloudBroadcastTopic = cloudBroadcastTopic;
	}
	
// Topic for subscribing order
	private String cloudSubOrderTopic;

	public String getCloudSubOrderTopic() {
		return cloudSubOrderTopic;
	}

	@Value("${ftfactorymqttclient.cloudSubOrderTopic:bpalab/ftfactory/f/i/order}")
	public void setCloudSubOrderTopic(String cloudSubOrderTopic) {
		this.cloudSubOrderTopic = cloudSubOrderTopic;
	}
	
// Topic for subscribing HBWStorage
	private String cloudHBWTopic;

	public String getCloudHBWTopic() {
		return cloudHBWTopic;
	}

	@Value("${ftfactorymqttclient.cloudHBWTopic:bpalab/ftfactory/f/i/stock}")
	public void setCloudHBWTopic(String cloudHBWTopic) {
		this.cloudHBWTopic = cloudHBWTopic;
	}
	
// Topic for subscribing HBWStorage
	private String cloudBME680Topic;

	public String getCloudBME680Topic() {
		return cloudBME680Topic;
	}

	@Value("${ftfactorymqttclient.cloudBME680Topic:bpalab/ftfactory/i/bme680}")
	public void setCloudBME680Topic(String cloudBME680Topic) {

		this.cloudBME680Topic = cloudBME680Topic;
	}

}
