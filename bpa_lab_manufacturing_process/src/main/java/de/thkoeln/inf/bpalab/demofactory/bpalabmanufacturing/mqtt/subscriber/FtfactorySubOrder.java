package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.subscriber;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.FtfactoryMQTTClient;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.CloudTopics;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.message.OrderShippedMessage;
import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Component
public class FtfactorySubOrder implements ISubscriber {

	private static Logger log = LoggerFactory.getLogger(FtfactorySubOrder.class);
			
	@Autowired
	private OrderShippedMessage orderShippedMessage;

	private boolean messageReceived;
	private boolean ordered = false;

	@Autowired
	private CloudTopics cloudTopics;

	@Autowired
	private ZeebeClient ftfactoryZEEBEClient;

	public FtfactorySubOrder() {
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
		FtfactorySubOrder fH = null;
		try {
			log.info(
					"\nmessageArrived>>> [topic: {}, message: {}]",
				      topic,
				      new String(message.getPayload(), StandardCharsets.UTF_8));

			fH = om.readValue(new String(message.getPayload()), FtfactorySubOrder.class);
			if (this.orderShippedMessage != null && fH.type.equals(orderShippedMessage.getReplyMessageCorrelationValue())) {
				log.info("\norderShippedMessage>>> {}", orderShippedMessage.getReplyMessageName());
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

	public void copyFtfactoryOrder(FtfactorySubOrder other) {
		this.ts = other.ts;
		this.state = other.state;
		this.type = other.type;
        this.processOrderReference = other.processOrderReference;
	}
	
	public void clearFtfactoryOrder() {
		this.ordered = false;
		this.messageReceived = false;
		this.ts = null;
		this.state = null;
		this.type = null;
        this.processOrderReference = null;
		orderShippedMessage.setReplyMessageCorrelationValue(null);
	}
	
	public void sendOrderShippedMessage() {
		if (this.orderShippedMessage != null) {
			HashMap<String, Object> sendVariables = new HashMap<>();
			sendVariables.put("state", getState());
			sendVariables.put("ts", getTs());
            sendVariables.put("processOrderReference", getProcessOrderReference());

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
    @JsonProperty("processOrderReference")
    public String processOrderReference;

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

    @JsonProperty("processOrderReference")
    public String getProcessOrderReference() {
        return processOrderReference;
    }

    @JsonProperty("processOrderReference")
    public void setProcessOrderReference(String processOrderReference) {
        this.processOrderReference = processOrderReference;
    };


}
