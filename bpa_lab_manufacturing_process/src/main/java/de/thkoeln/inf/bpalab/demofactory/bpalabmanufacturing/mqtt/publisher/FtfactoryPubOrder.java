package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.publisher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.CloudTopics;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.TopicPayload;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.time.UTCTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FtfactoryPubOrder implements IPublisher {
	private static Logger log = LoggerFactory.getLogger(FtfactoryPubOrder.class);
	

	@JsonProperty("ts")
	public String ts;
	@JsonProperty("type")
	public String type;
    @JsonProperty("processOrderReference")
    public String processOrderReference;

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
	        this.type = rootNode.path("orderType").asText();
            if (!rootNode.path("manufactureOrderCorrelation").asText().equals("")) {
                this.processOrderReference = rootNode.path("manufactureOrderCorrelation").asText();
            } else {
                if (!rootNode.path("processOrderReference").asText().equals(""))
                    this.processOrderReference = rootNode.path("processOrderReference").asText();
                else
                    this.processOrderReference = UUID.randomUUID().toString();
            }
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
