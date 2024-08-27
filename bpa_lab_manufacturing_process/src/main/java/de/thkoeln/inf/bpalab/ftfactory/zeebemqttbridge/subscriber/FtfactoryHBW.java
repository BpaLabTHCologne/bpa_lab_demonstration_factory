package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber;

import com.fasterxml.jackson.databind.*;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.FtfactoryMQTTClient;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.CloudTopics;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.sender.FtfactoryHBWAvailableMessage;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.sender.OrderShippedMessage;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber.ftfactoryHBW.StockItem;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber.ftfactoryHBW.Workpiece;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import jakarta.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
public class FtfactoryHBW implements ISubscriber {
//	{"ts":"YYYY-MM-DDThh:mm:ss.fffZ", "stockItems": 
//	[{ "workpiece": { "id":"123456789ABCDE", "type":"<BLUE/WHITE/RED>", "state":"<RAW/PROCESSED>" }, "location":"A1" },
//	{ ... },
//	{ "workpiece":null, "location":"B3" }] }
	private static Logger log = LoggerFactory.getLogger(FtfactoryHBW.class);

//	@Value("${ftfactorymqttclient.cloudTopicPrefix}")
//	private String cloudTopicPrefix;

	@Autowired
	private CloudTopics cloudTopics;

	@Autowired
	protected FtfactoryMQTTClient ftfactoryMQTTClient;

	@PostConstruct
	public void postConstruct() {
//		prepare Subscriber
		ftfactoryMQTTClient.subscribe(getSubscriptionTopic(), this);
	}
	
	@Autowired
	private ZeebeClientLifecycle ftfactoryZEEBEClient;

	@Autowired
	private FtfactoryHBWAvailableMessage ftfactoryHBWAvailableMessage;

	public FtfactoryHBW() {
	}
		
	@JsonIgnore
	private boolean messageReceived;

	@Override
	@JsonIgnore
	public String getSubscriptionTopic() {
//		return "/j1/txt//f/i/stock";
//		return this.cloudTopics.getCloudTopicPrefix() + "/f/i/stock";
		return this.cloudTopics.getCloudHBWTopic();
	}

	public void setReplyMessageCorrelationValue(String type) {
		ftfactoryHBWAvailableMessage.setReplyMessageCorrelationValue(type);
	}	
		
	@JsonIgnore
	public boolean isMessageReceived() {
		return messageReceived;
	}
	
	@JsonIgnore
	public boolean isAvailable(String type) {
		if (getWorkpieceCount(type) > 0) {
			return true;
		} else
			return false;
		
	}

	@JsonIgnore
	public int getWorkpieceCount(String color) {
		if (this.getStockItems() == null)
			return 0;
		else {
			int count = 0;
			ListIterator iter = this.getStockItems().listIterator();
			while (iter.hasNext()) {
				StockItem item = (StockItem)iter.next();
				if (item.getWorkpiece() != null) {
					if (item.getWorkpiece().getType().equals(color))
						count++;
				}
			}
			return count;
		}
	}
	
	@JsonIgnore
	public int getWorkpieceCountRED() {
		return this.getWorkpieceCount(Workpiece.typeRED);
	}
	
	@JsonIgnore
	public int getWorkpieceCountWHITE() {
		return this.getWorkpieceCount(Workpiece.typeWHITE);
	}
	
	@JsonIgnore
	public int getWorkpieceCountBLUE() {
		return this.getWorkpieceCount(Workpiece.typeBLUE);
	}
	
	
	@Override
	public void messageArrived(String topic, MqttMessage message) {
// 		Sample Topic: baplab/ftfactory/f/i/stock
//		PayLoad(JSON):
/*		{"ts":"2023-06-22T12:12:12.123Z",
		"stockItems": 
		[{"workpiece": { "id":"123456789ABCDE", "type":"WHITE", "state":"RAW" }, "location":"A1"},
		 {"workpiece": { "id":"156789ABCDE", "type":"RED", "state":"RAW" }, "location":"A2"},
		 {"workpiece": { "id":"7765555", "type":"BLUE", "state":"RAW" }, "location":"A3"},
		 { "workpiece":null, "location":"B1"},
		 { "workpiece":null, "location":"B2"},
		 { "workpiece":null, "location":"B3" },
		 { "workpiece":null, "location":"C1"},
		 { "workpiece":null, "location":"C2"},
		 { "workpiece":null, "location":"C3" }] }
*/
		ObjectMapper om = new ObjectMapper();
		FtfactoryHBW fH = null;
		try {
			fH = om.readValue(new String(message.getPayload()), FtfactoryHBW.class);
			this.copyFtfactoryHBW(fH);
			this.messageReceived = true;
			System.out.println("FtfactoryHBW.messageArrived with topic: " + topic + "\nmessage: " + new String(message.getPayload(), StandardCharsets.UTF_8));
		} catch (JsonMappingException e) {
			System.out.println("JsonMappingException " + e.getMessage());
			this.messageReceived = false;
		} catch (JsonProcessingException e) {
			System.out.println("JsonProcessingException " + e.getMessage());
			this.messageReceived = false;
		}

	}

	public void copyFtfactoryHBW(FtfactoryHBW other) {
		this.ts = other.getTs();
		this.stockItems = other.getStockItems();
	}

	public void sendFtfactoryStorageMessage() {
		if (this.ftfactoryHBWAvailableMessage != null) {
			this.ftfactoryZEEBEClient.newPublishMessageCommand()
										.messageName(this.ftfactoryHBWAvailableMessage.getReplyMessageName())
										.correlationKey(this.ftfactoryHBWAvailableMessage.getReplyMessageCorrelationValue())
										.send();
			log.info("\nmessagename {} correlationvalue {}", this.ftfactoryHBWAvailableMessage.getReplyMessageName(), this.ftfactoryHBWAvailableMessage.getReplyMessageCorrelationValue());
		}
	}
	

// ------------- JSON stuff ------------------

	@JsonProperty("ts")
	private String ts;
	@JsonProperty("stockItems")
	private List<StockItem> stockItems;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

	@JsonProperty("ts")
	public String getTs() {
		return ts;
	}

	@JsonProperty("ts")
	public void setTs(String ts) {
		this.ts = ts;
	}

	@JsonProperty("stockItems")
	public List<StockItem> getStockItems() {
		return stockItems;
	}

	@JsonProperty("stockItems")
	public void setStockItems(List<StockItem> stockItems) {
		this.stockItems = stockItems;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public String asJSON() {
		ObjectMapper om = new ObjectMapper();
		
		try {
			return om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;		}
	}

	public String countAsJSON() {
		Map<String, Integer> map = new LinkedHashMap<>();
		map.put(Workpiece.typeRED, getWorkpieceCountRED());
		map.put(Workpiece.typeBLUE, getWorkpieceCountBLUE());
		map.put(Workpiece.typeWHITE, getWorkpieceCountWHITE());

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
}
