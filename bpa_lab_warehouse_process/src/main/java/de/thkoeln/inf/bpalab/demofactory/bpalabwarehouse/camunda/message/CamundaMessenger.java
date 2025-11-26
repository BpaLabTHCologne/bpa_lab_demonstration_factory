package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.camunda.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.ftwarehousestorage.BikeInstance;
import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private BikeInstance bikeInstance;

    private static final String MSG_FETCHED_BIKE = "MsgWarehouseFetched";
    private static final String MSG_PUTTED_BIKE = "MsgWarehousePutted";

    public CamundaMessenger(ZeebeClient cClient) {
        this.zeebeClient = cClient;
    }

    public void sendFetchedBike(BikeInstance bikeInstance, String correlation) throws JsonProcessingException {
        String stockItemJSON = objectMapper.writeValueAsString(bikeInstance);
        Map<String, Object> variables = new HashMap<>();
        variables.put("bikeInstance", bikeInstance);
        variables.put("id", bikeInstance.getId());
        LOG.info("sendFetchedBike :: {} correlationKey{}",
                stockItemJSON, correlation);
        zeebeClient.newPublishMessageCommand()
                 .messageName(MSG_FETCHED_BIKE)
                 .correlationKey(correlation)
                 .variables(variables)
                 .send().join();
    }

    public void sendGetBike(BikeInstance bikeInstance, String correlation) throws JsonProcessingException {
        String stockItemJSON = objectMapper.writeValueAsString(bikeInstance);
        Map<String, Object> variables = new HashMap<>();
        variables.put("bikeInstance", bikeInstance);
        variables.put("id", bikeInstance.getId());
        LOG.info("sendGetBike :: {} correlationKey{}",
                stockItemJSON, correlation);
        zeebeClient.newPublishMessageCommand()
                .messageName(MSG_FETCHED_BIKE)
                .correlationKey(correlation)
                .variables(variables)
                .send().join();
    }

    public void sendStoredPlace(String place, String correlation) throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("place", place);
        LOG.info("sendStoredPlace :: {} correlationKey{}",
                place, correlation);
        zeebeClient.newPublishMessageCommand()
                .messageName(MSG_PUTTED_BIKE)
                .correlationKey(correlation)
                .variables(variables)
                .send().join();
    }

    public void sendWarehouseFinished(BikeInstance bikeInstance) throws JsonProcessingException {

    }
}