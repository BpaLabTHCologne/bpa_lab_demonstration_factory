package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.FtfactoryMQTTClient;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.Order;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.CloudTopics;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.sender.OrderShippedMessage;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.worker.FtfactoryOrderWorker;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import jakarta.annotation.PostConstruct;

@Component
public class FtfactoryOrder implements ISubscriber {

	private static Logger log = LoggerFactory.getLogger(FtfactoryOrder.class);
			
	@Autowired
	private OrderShippedMessage orderShippedMessage;

	private boolean messageReceived;
	private boolean ordered = false;

//	@Value("${ftfactorymqttclient.cloudTopicPrefix}")
//	private String cloudTopicPrefix;

	@Autowired
	private CloudTopics cloudTopics;

	@Autowired
	private ZeebeClientLifecycle ftfactoryZEEBEClient;

	public FtfactoryOrder () {
	}
	
	@Autowired
	protected FtfactoryMQTTClient ftfactoryMQTTClient;

	@PostConstruct
	public void postConstruct() {
//		prepare Subscriber
		ftfactoryMQTTClient.subscribe(getSubscriptionTopic(), this);
	}
	
	@Override
	public boolean isMessageReceived() {
		return messageReceived;
	}

	@Override
	public String getSubscriptionTopic() {
//		return "/j1/txt//f/i/order";
//		return this.cloudTopics.getCloudTopicPrefix() + "/f/i/order";
		return this.cloudTopics.getCloudSubOrderTopic();
	}

	public boolean isOrdered() {
		return ordered;
	}

	public Object getReplyMessageName() {
		return orderShippedMessage.getReplyMessageName();
	}

	public void initTypeCorrelationValue(String type) {
		orderShippedMessage.setReplyMessageCorrelationValue(type);
		this.ordered = true;
		this.type = type;
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
// 		Sample Topic: baplab/ftfactory/f/i/order
//		PayLoad(JSON):
/*		{"ts":"2023-06-22T12:12:12.123", "state":"SHIPPED", "type":"RED"}
*/
		ObjectMapper om = new ObjectMapper();
		FtfactoryOrder fH = null;
		try {
			log.info(
					"\nmessageArrived>>> [topic: {}, message: {}]",
				      topic,
				      new String(message.getPayload(), StandardCharsets.UTF_8));

			fH = om.readValue(new String(message.getPayload()), FtfactoryOrder.class);
			if (this.orderShippedMessage != null && fH.type.equals(orderShippedMessage.getReplyMessageCorrelationValue())) {
				if (fH.getState().equals("SHIPPED")) {
					this.copyFtfactoryOrder(fH);
					this.sendOrderShippedMessage();
					clearFtfactoryOrder();
				}
				if (fH.getState().equals("IN_PROCESS") || fH.getState().equals("ORDERED")) {
					this.ordered  = true;
					this.messageReceived = true;
					this.copyFtfactoryOrder(fH);
				}
			}
		} catch (JsonMappingException e) {
			log.info("JsonMappingException {}", e.getMessage());
			this.messageReceived = false;
			this.ordered  = false;
		} catch (JsonProcessingException e) {
			log.info("JsonProcessingException {}", e.getMessage());
			this.messageReceived = false;
			this.ordered  = false;
		}
	}

	public void copyFtfactoryOrder(FtfactoryOrder other) {
		this.ts = other.ts;
		this.state = other.state;
		this.type = other.type;
	}
	
	public void clearFtfactoryOrder() {
		this.ordered = false;
		this.messageReceived = false;
		this.ts = null;
		this.state = null;
		this.type = null;
		orderShippedMessage.setReplyMessageCorrelationValue(null);
	}
	
	public void sendOrderShippedMessage() {
		if (this.orderShippedMessage != null) {
			HashMap<String, Object> sendVariables = new HashMap<>();
			sendVariables.put("state", getState());
			sendVariables.put("ts", getTs());

			ftfactoryZEEBEClient.newPublishMessageCommand()
									.messageName(orderShippedMessage.getReplyMessageName())
									.correlationKey(orderShippedMessage.getReplyMessageCorrelationValue())
									.variables(sendVariables)
									.send();
			log.info("\npublished zeebemessage {} correlationvalue {}", orderShippedMessage.getReplyMessageName(), orderShippedMessage.getReplyMessageCorrelationValue());
		}
	}
	
// ------------- JSON stuff ------------------
	@JsonProperty("ts")
	private String ts;
	@JsonProperty("state")
	private String state;
	@JsonProperty("type")
	private String type;

	@JsonProperty("ts")
	public String getTs() {
		return ts;
	}

	@JsonProperty("ts")
	public void setTs(String ts) {
		this.ts = ts;
	}

	@JsonProperty("state")
	public String getState() {
		return state;
	}

	@JsonProperty("state")
	public void setState(String state) {
		this.state = state;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

}
