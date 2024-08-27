package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.TopicPayload;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.publisher.IPublisher;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.time.UTCTime;
import io.camunda.zeebe.client.api.response.ActivatedJob;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.CloudTopics;

public class Order {
	private static Logger log = LoggerFactory.getLogger(Order.class);
	

	@JsonProperty("ts")
	public String ts;
	@JsonProperty("type")
	public String type;
	
	public Order(String variables) {
		// time must be greater than TXT-time + 60
		this.ts = UTCTime.getUTCTimeNowPlus1Day(); // adds 1 day to be before TXTController time

		this.type = decodeOrder(variables);
	}
	
	public String decodeOrder(String variables) {
		String decodeType = null;
		ObjectMapper objectMapper = new ObjectMapper();
	    try {
			JsonNode rootNode = objectMapper.readTree(variables);
	        decodeType = rootNode.path("OrderType").asText();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return decodeType;
	}

}
