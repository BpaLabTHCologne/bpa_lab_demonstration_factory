package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.camunda.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.FtwarehouseMQTTClient;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.publisher.FtWarehousePublisher;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.FtWarehouseFetchedBike;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.FtWarehouseStorage;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.FtWarehouseStoredPlace;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static io.camunda.zeebe.client.impl.util.VersionUtil.LOG;

@Component
public class FtWarehouseStorageWorker extends AWorker {

    @Autowired
    FtWarehouseStorage ftWarehouseStorage;

    @Autowired
    FtWarehousePublisher ftWarehousePublisher;

    @Autowired
    FtWarehouseStoredPlace ftWarehouseStoredPlace;

    @Autowired
    FtWarehouseFetchedBike ftWarehouseFetchedBike;

    private final String MSG_WAREHOUSE_FETCH_FINISHED = "MsgWarehouseFetchFinished";
    private final String MSG_WAREHOUSE_FETCH_NOBIKE_FINISHED = "MsgWarehouseFetchNoBikeFinished";
    private String warehouseFetchCorrelation;

    private final String MSG_WAREHOUSE_PUT_FINISHED = "MsgWarehousePutFinished";
    private final String MSG_WAREHOUSE_PUT_NOPLACE_FINISHED = "MsgWarehousePutNoPlaceFinished";
    private String warehousePutCorrelation;

    private ObjectMapper objectMapper = new ObjectMapper();

    public FtWarehouseStorageWorker(ZeebeClient zeebeClient, FtwarehouseMQTTClient ftWarehouseMQTTClient) {
        this.ftWarehouseMQTTClient = ftWarehouseMQTTClient;
        this.ftfactoryZEEBEClient = zeebeClient;
    }
// Worker for BPALabWarehouseFetch

    @JobWorker(type = "fetchBikeInstance")
    public void fetchBikeInstance(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        String fetchCorrelation = null;
        if (vars.containsKey("warehouseFetchCorrelation") && vars.get("warehouseFetchCorrelation") != null)
            fetchCorrelation = warehouseFetchCorrelation = vars.get("warehouseFetchCorrelation").toString();
        String idOrColor = String.valueOf(vars.get("id"));
        if (idOrColor != null && !idOrColor.equals("")) {
            if (fetchCorrelation == null)
                fetchCorrelation = idOrColor;
            ftWarehouseFetchedBike.setFetchCorrelation(fetchCorrelation);
            ftWarehousePublisher.publishGetCommand(idOrColor);
        } else {
            idOrColor = String.valueOf(vars.get("color"));
            if (idOrColor != null && !idOrColor.equals("")) {
                if (fetchCorrelation == null)
                    fetchCorrelation = idOrColor;
                ftWarehouseFetchedBike.setFetchCorrelation(fetchCorrelation);
                ftWarehousePublisher.publishFetchCommand(idOrColor);
            }
        }
        try {
            vars.put("fetchCorrelation", fetchCorrelation);
            LOG.info("fetchBicycle in:: {}", vars);
            client.newCompleteCommand(job.getKey()).variables(vars).send().join();
        } catch (Exception e) {
            LOG.error("Error in getBicycle", e);
            failJob(client, job, e.getMessage());
        }
    }

    @JobWorker(type = "sendFinishedWarehouseFetch")
    public void sendFinishedWarehouseFetch(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        LOG.info("sendFinishedWarehouseFetch :: {}", variables);
        if (variables.containsKey("warehouseFetchCorrelation")) {
            warehouseFetchCorrelation = variables.get("warehouseFetchCorrelation").toString();
            LOG.info("sendFinishedWarehouseFetch correlationKey {}"
                    , warehouseFetchCorrelation);
            ftfactoryZEEBEClient.newPublishMessageCommand()
                    .messageName(MSG_WAREHOUSE_FETCH_FINISHED)
                    .correlationKey(warehouseFetchCorrelation)
                    .variables(variables)
                    .send().join();
        } else {
            LOG.info("finished Warehouse Fetch Process without sending message");
        }
    }

    @JobWorker(type = "sendFinishedWarehouseNoFetch")
    public void sendFinishedWarehouseNoFetch(final JobClient client, final ActivatedJob job) throws JsonProcessingException {
        Map<String, Object> variables = job.getVariablesAsMap();
        LOG.info("sendFinishedWarehouseNoFetch :: {}", objectMapper.writeValueAsString(variables));
        if (variables.containsKey("warehouseFetchCorrelation")) {
            warehouseFetchCorrelation = variables.get("warehouseFetchCorrelation").toString();
            LOG.info("sendFinishedWarehouseNoBike correlationKey {}"
                    , warehouseFetchCorrelation);
            ftfactoryZEEBEClient.newPublishMessageCommand()
                    .messageName(MSG_WAREHOUSE_FETCH_NOBIKE_FINISHED)
                    .correlationKey(warehouseFetchCorrelation)
                    .send().join();
        } else {
            LOG.info("finished Warehouse Fetch Process without sending message");
        }
    }

// Worker for BPALabWarehousePut

    @JobWorker(type = "putBikeInstance")
    public void putBikeInstance(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        LOG.info("putBikeInstance :: {}", vars);
        try {
            String id = String.valueOf(vars.get("id"));
            String color = String.valueOf(vars.get("color"));
            String putCorrelation;
            if (vars.containsKey("warehousePutCorrelation") && vars.get("warehousePutCorrelation") != null)
                putCorrelation = warehousePutCorrelation = vars.get("warehousePutCorrelation").toString();
            else
                putCorrelation = String.valueOf(vars.get("id"));
            vars.put("putCorrelation", putCorrelation);
            LOG.info("putBikeInstance out:: {}", vars);
            ftWarehouseStoredPlace.setStoreCorrelation(putCorrelation);
            ftWarehousePublisher.publishPutCommand(id, color);
            client.newCompleteCommand(job.getKey()).variables(vars).send().join();
        } catch (Exception e) {
            LOG.error("Error in putBikeInstance", e);
            failJob(client, job, e.getMessage());
        }
    }

    @JobWorker(type = "sendFinishedWarehousePut")
    public void sendFinishedWarehousePut(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        LOG.info("sendFinishedWarehousePut :: {}", variables);
        if (variables.containsKey("warehousePutCorrelation")) {
            warehousePutCorrelation = variables.get("warehousePutCorrelation").toString();
            LOG.info("sendFinishedWarehousePut correlationKey {}"
                    , warehousePutCorrelation);
            ftfactoryZEEBEClient.newPublishMessageCommand()
                    .messageName(MSG_WAREHOUSE_PUT_FINISHED)
                    .correlationKey(warehousePutCorrelation)
                    .variables(variables)
                    .send().join();
        } else {
            LOG.info("finished Warehouse Put Process without sending message");
        }
    }

    @JobWorker(type = "sendFinishedWarehouseNoPut")
    public void sendFinishedWarehouseNoPut(final JobClient client, final ActivatedJob job) throws JsonProcessingException {
        Map<String, Object> variables = job.getVariablesAsMap();
        LOG.info("sendFinishedWarehouseNoPut :: {}", objectMapper.writeValueAsString(variables));
        if (variables.containsKey("warehousePutCorrelation")) {
            warehousePutCorrelation = variables.get("warehousePutCorrelation").toString();
            LOG.info("sendFinishedWarehouseNoPut correlationKey {} "
                    , warehousePutCorrelation);
            ftfactoryZEEBEClient.newPublishMessageCommand()
                    .messageName(MSG_WAREHOUSE_PUT_NOPLACE_FINISHED)
                    .correlationKey(warehousePutCorrelation)
                    .send().join();
        } else {
            LOG.info("finished Warehouse Put Process without sending message");
        }
    }


    private void failJob(JobClient client, ActivatedJob job, String message) {
        try {
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage(message != null ? message : "Unhandled error")
                    .send()
                    .join();
        } catch (Exception e) {
            LOG.error("Failed to mark job as failed", e);
        }
    }

}
