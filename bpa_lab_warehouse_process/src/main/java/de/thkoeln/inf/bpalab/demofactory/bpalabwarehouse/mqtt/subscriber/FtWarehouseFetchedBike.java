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
public class FtWarehouseFetchedBike implements IMqttMessageListener {
    private static final Logger log = LoggerFactory.getLogger(BikeInstance.class);

    @Autowired
    CamundaMessenger messenger;

    @Autowired
    WarehouseTopics warehouseTopics;

    @Autowired
    FtwarehouseMQTTClient ftWarehouseMQTTClient;

    @JsonIgnore
    private String fetchCorrelation;
    @JsonIgnore
    public void setFetchCorrelation(String fetchCorrelation) {this.fetchCorrelation = fetchCorrelation;}
    @JsonIgnore
    public String getFetchCorrelation() { return fetchCorrelation; }

    @PostConstruct
    public void postConstruct() {
//		prepare Subscriber
        log.info("PostConstruct {}", getSubscriptionTopic());
        ftWarehouseMQTTClient.subscribe(getSubscriptionTopic(), this);
    }

    @JsonProperty("BikeInstance")
    private BikeInstance bikeInstance = new BikeInstance() ;

    @JsonProperty("BikeInstance")
    public BikeInstance getBikeInstance() {
        return bikeInstance;
    }

    @JsonProperty("BikeInstance")
    public void setBikeInstance(BikeInstance bikeInstance) {
        this.bikeInstance = bikeInstance;
    }

    private static final Logger LOG = LoggerFactory.getLogger(FtWarehouseFetchedBike.class);

    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @JsonIgnore
    public String getSubscriptionTopic() {
        return warehouseTopics.getFetchedBikeTopic();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        BikeInstance bikeInstance = BikeInstance.fromJSON(new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));
        if (bikeInstance != null) {
            LOG.info("messageArrived bikeInstance : " + bikeInstance.asJSON());
            try {
                messenger.sendGetBike(bikeInstance, fetchCorrelation);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
