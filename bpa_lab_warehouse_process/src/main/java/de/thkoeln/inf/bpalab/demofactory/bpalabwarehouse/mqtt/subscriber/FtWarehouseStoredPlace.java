package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.camunda.message.CamundaMessenger;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.FtwarehouseMQTTClient;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.WarehouseTopics;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.ftwarehousestorage.BikeInstance;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class FtWarehouseStoredPlace implements IMqttMessageListener {
    private static final Logger log = LoggerFactory.getLogger(BikeInstance.class);

    @Autowired
    CamundaMessenger messenger;

    @Autowired
    WarehouseTopics warehouseTopics;

    @Autowired
    FtwarehouseMQTTClient ftWarehouseMQTTClient;

    @PostConstruct
    public void postConstruct() {
//		prepare Subscriber
        log.info("PostConstruct {}", warehouseTopics.getStoredPlaceTopic());
        ftWarehouseMQTTClient.subscribe(warehouseTopics.getStoredPlaceTopic(), this);
        log.info("PostConstruct {}", warehouseTopics.getNoPlaceTopic());
        ftWarehouseMQTTClient.subscribe(warehouseTopics.getNoPlaceTopic(), this);
    }

    @JsonProperty("place")
    private String place;

    @JsonProperty("place")
    public String getPlace() { return place; }

    @JsonProperty("place")
    public void setPlace(String place) {this.place = place;}

    @JsonIgnore
    private String storeCorrelation;
    @JsonIgnore
    public void setStoreCorrelation(String storeCorrelation) {this.storeCorrelation = storeCorrelation;}
    @JsonIgnore
    public String getStoreCorrelation() { return storeCorrelation; }

    private static final Logger LOG = LoggerFactory.getLogger(FtWarehouseStoredPlace.class);

    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        if (topic.equals(warehouseTopics.getStoredPlaceTopic())) {
            String storedPlace = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
            place = storedPlace;
            if (place != null) {
                LOG.info("messageArrived storedPlace : " + storedPlace);
                try {
                    messenger.sendStoredPlace(place, storeCorrelation);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (topic.equals(warehouseTopics.getNoPlaceTopic())) {
            LOG.info("messageArrived no Place");
            messenger.sendNoPlace(storeCorrelation);
        }
    }
}
