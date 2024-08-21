package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TopicPayload {

	private String topic;
	private String payLoad;
		
	public TopicPayload(String topic, String payLoad) {
		this.topic = topic;
		this.payLoad = payLoad;
	}
	
	public TopicPayload(String topic) {
		this(topic, null);
	}
		
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getPayLoad() {
		return payLoad;
	}
	public void setPayLoad(String payLoad) {
		this.payLoad = payLoad;
	}
	
	public MqttMessage mqttMessage() throws IllegalStateException {
		if (this.topic != null) {
			if (this.payLoad != null && !this.payLoad.equals("")) {
				return new MqttMessage(this.payLoad.getBytes());
			} else
				return new MqttMessage();
		} else
			throw(new IllegalStateException("no topic specified"));
	}
}
