package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.camunda.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.ftwarehousestorage.BikeInstance;
import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Worker implementations using Camunda Spring Boot SDK annotations.
 *
 * Each handler is annotated with @JobWorker so the SDK registers them as workers automatically.
 */
@Component
public class CamundaMessenger {
    private static final Logger LOG = LoggerFactory.getLogger(CamundaMessenger.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private final ZeebeClient zeebeClient;

    public CamundaMessenger(ZeebeClient cClient) {
        this.zeebeClient = cClient;
    }

    private static final String MQTT_FETCHED_BIKE = "MqttWarehouseFetched";
    public void sendGetBike(BikeInstance bikeInstance, String correlation) throws JsonProcessingException {
        String stockItemJSON = objectMapper.writeValueAsString(bikeInstance);
        Map<String, Object> variables = new HashMap<>();
        variables.put("bikeInstance", bikeInstance);
        variables.put("id", bikeInstance.getId());
        LOG.info("sendGetBike :: {} correlationKey{}",
                stockItemJSON, correlation);
        zeebeClient.newPublishMessageCommand()
                .messageName(MQTT_FETCHED_BIKE)
                .correlationKey(correlation)
                .variables(variables)
                .send().join();
    }

    private static final String MQTT_NO_BIKE = "MqttWarehouseNoBike";
    public void sendNoBike(String correlation) {
        Map<String, Object> variables = new HashMap<>();
        LOG.info("sendNoPlace :: correlationKey{}", correlation);
        zeebeClient.newPublishMessageCommand()
                .messageName(MQTT_NO_BIKE)
                .correlationKey(correlation)
                .variables(variables)
                .send().join();
    }

    private static final String MQTT_PUTTED_BIKE = "MqttWarehousePutted";
    public void sendStoredPlace(String place, String correlation) throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("place", place);
        LOG.info("sendStoredPlace :: {} correlationKey{}",
                place, correlation);
        zeebeClient.newPublishMessageCommand()
                .messageName(MQTT_PUTTED_BIKE)
                .correlationKey(correlation)
                .variables(variables)
                .send().join();
    }

    private static final String MQTT_NO_PLACE = "MqttWarehouseNoPlace";
    public void sendNoPlace(String correlation) {
        Map<String, Object> variables = new HashMap<>();
        LOG.info("sendNoPlace :: correlationKey{}", correlation);
        zeebeClient.newPublishMessageCommand()
                .messageName(MQTT_NO_PLACE)
                .correlationKey(correlation)
                .variables(variables)
                .send().join();
    }

}