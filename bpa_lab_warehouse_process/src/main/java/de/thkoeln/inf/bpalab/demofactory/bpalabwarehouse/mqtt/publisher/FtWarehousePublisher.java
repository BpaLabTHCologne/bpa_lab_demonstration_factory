package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.publisher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.FtwarehouseMQTTClient;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.TopicPayload;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.WarehouseTopics;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.FtWarehouseStorage;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.ftwarehousestorage.BikeInstance;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class FtWarehousePublisher {

    @JsonIgnore
    private WarehouseTopics warehouseTopics;

    @JsonIgnore
	private TopicPayload getTopicPayload;

    @JsonIgnore
    private TopicPayload putTopicPayload = null;

    private FtwarehouseMQTTClient ftWarehouseMQTTClient;

    private FtWarehouseStorage ftWarehouseStorage;

	@JsonIgnore
	private static Logger log = LoggerFactory.getLogger(FtWarehousePublisher.class);

//	@Value("${ftfactorymqttclient.cloudTopicPrefix}")
//	private String cloudTopicPrefix;
	
	@Autowired
	public FtWarehousePublisher(WarehouseTopics warehouseTopics,
                                FtwarehouseMQTTClient ftWarehouseMQTTClient,
                                FtWarehouseStorage ftWarehouseStorage) {
        this.ftWarehouseMQTTClient = ftWarehouseMQTTClient;
        this.warehouseTopics = warehouseTopics;
        this.ftWarehouseStorage = ftWarehouseStorage;
	}
	
	@JsonIgnore
	public TopicPayload getGetTopicPayload() {
		return getTopicPayload;
	}

    @JsonIgnore
    public TopicPayload getPutTopicPayload() {
        return putTopicPayload;
    }

    public void publishGetCommand(String id) {
        ObjectMapper om = new ObjectMapper();
        String idString = "{\"id\" : \"" + id + "\"}";
        getTopicPayload = new TopicPayload(warehouseTopics.getGetTopic(), idString);
        if (ftWarehouseMQTTClient.isConnected()) {
            log.info("publishGetCommand topic: {}, idString {}", getGetTopicPayload().getTopic(), idString);
            ftWarehouseMQTTClient.publish(getTopicPayload);
        } else
            log.info("FAKED publishGetCommand topic: {}, idString {}", getGetTopicPayload().getTopic(), idString);
    }

    public void publishPutCommand(String id, String color) {
            BikeInstance bikeInstance = new BikeInstance();
            bikeInstance.setId(id);
            bikeInstance.setColor(color);
            String JSONString = bikeInstance.asJSON();
            putTopicPayload = new TopicPayload(warehouseTopics.getPutTopic(), JSONString);
            if (ftWarehouseMQTTClient.isConnected()) {
                log.info("publishPutCommand topic: {}, bikeInstance {}", getPutTopicPayload().getTopic(), JSONString);
                ftWarehouseMQTTClient.publish(putTopicPayload);
            } else {
                log.info("FAKED publishPutCommand topic: {}, bikeInstance {}", getPutTopicPayload().getTopic(), JSONString);
            }
    }

    public void publishFetchCommand(String color) {
        ObjectMapper om = new ObjectMapper();
        String colorString = "{\"color\" : \"" + color + "\"}";
        getTopicPayload = new TopicPayload(warehouseTopics.getFetchTopic(), colorString);
        if (ftWarehouseMQTTClient.isConnected()) {
            log.info("publishGetCommand topic: {}, idString {}", getGetTopicPayload().getTopic(), colorString);
            ftWarehouseMQTTClient.publish(getTopicPayload);
        } else
            log.info("FAKED publishGetCommand topic: {}, idString {}", getGetTopicPayload().getTopic(), colorString);
    }
}
	