package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.CloudTopics;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.TopicPayload;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.time.UTCTime;
import jakarta.annotation.PostConstruct;

@Service
public class FtfactoryBroadcast implements IPublisher {
/*
 * Helper class to set FactoryMain "KEEP_ALIVE" value to given timestamp
 * via topic "/j1/txt//o/broadcast" and
 *  payload {"ts":"YYYY-MM-DDThh:mm:ss.fffZ", "message":"keep-alive"} 
 */
	@JsonProperty("ts")
	public String ts;
	@JsonProperty("message")
	public String message;

	@JsonIgnore
	private TopicPayload broadcastTopicPayload = null;

	@JsonIgnore
	private static Logger log = LoggerFactory.getLogger(FtfactoryBroadcast.class);

//	@Value("${ftfactorymqttclient.cloudTopicPrefix}")
//	private String cloudTopicPrefix;
	
	@Autowired
	public FtfactoryBroadcast(CloudTopics cloudTopics) {
		message = "keep-alive";
		this.ts = UTCTime.getUTCTimeNowPlus1Day(); // adds 1 day to be before TXTController time
		ObjectMapper om = new ObjectMapper();
		try {
			String JSONString = om.writeValueAsString(this);
//			broadcastTopicPayload = new TopicPayload("/j1/txt//o/broadcast", JSONString);		
//			broadcastTopicPayload = new TopicPayload(cloudTopics.getCloudTopicPrefix() + "/o/broadcast", JSONString);		
			broadcastTopicPayload = new TopicPayload(cloudTopics.getCloudBroadcastTopic(), JSONString);		

			log.info("Broadcast topic: {}, JSONString {}", broadcastTopicPayload.getTopic(), JSONString);
		} catch (JsonMappingException e) {
			log.info("JsonMappingException {}", e.getMessage());
		} catch (JsonProcessingException e) {
			log.info("JsonProcessingException {}", e.getMessage());
		}
	}
	
	@JsonIgnore
	public TopicPayload getTopicPayload() {
//		this.broadcastTopicPayload.setTopic(cloudTopicPrefix + this.broadcastTopicPayload.getTopic());
		return broadcastTopicPayload;
	}
}
	