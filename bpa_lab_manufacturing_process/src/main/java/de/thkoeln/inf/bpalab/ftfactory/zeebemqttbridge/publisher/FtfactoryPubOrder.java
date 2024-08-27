package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.TopicPayload;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.time.UTCTime;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.CloudTopics;

@Component
public class FtfactoryPubOrder implements IPublisher {
	private static Logger log = LoggerFactory.getLogger(FtfactoryPubOrder.class);
	

	@JsonProperty("ts")
	public String ts;
	@JsonProperty("type")
	public String type;

	@JsonIgnore
	private TopicPayload publishOrderTopicPayload = null;
	
	@Autowired
	public FtfactoryPubOrder(CloudTopics cloudTopics) {
//		publishOrderTopicPayload = new TopicPayload(cloudTopics.getCloudTopicPrefix() + "/f/o/order");
		publishOrderTopicPayload = new TopicPayload(cloudTopics.getCloudPubOrderTopic());
	}
	
	@JsonIgnore
	public TopicPayload getTopicPayload() {
		return publishOrderTopicPayload;
	}

	public void updateOrder(String variables) {
	    try {
			ObjectMapper om = new ObjectMapper();			
	    	JsonNode rootNode = om.readTree(variables);
	        this.type = rootNode.path("OrderType").asText();
			// time must be greater than TXT-time + 60
			this.ts = UTCTime.getUTCTimeNowPlus1Day(); // adds 1 day to be before TXTController time			

			ObjectMapper objectMapper = new ObjectMapper();			
	    	String JSONString = objectMapper.writeValueAsString(this);
	    	publishOrderTopicPayload.setPayLoad(JSONString);
	    	log.info("Topic: {}, Payload: {}", publishOrderTopicPayload.getTopic(), publishOrderTopicPayload.getPayLoad());
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
