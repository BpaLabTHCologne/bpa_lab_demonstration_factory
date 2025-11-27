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
public class FtWarehouseStorageSuperWorker extends AWorker {

    @Autowired
    FtWarehouseStorage ftWarehouseStorage;

    private final String MSG_WAREHOUSE_START_FETCH = "MsgStartWarehouseFetch";
    private String warehouseFetchCorrelation;

    private final String MSG_WAREHOUSE_START_PUT = "MsgStartWarehousePut";
    private String warehousePutCorrelation;

    private ObjectMapper objectMapper = new ObjectMapper();

    public FtWarehouseStorageSuperWorker(ZeebeClient zeebeClient) {
        this.ftfactoryZEEBEClient = zeebeClient;
    }
// Worker for inventory
    @JobWorker(type = "inventory")
    public void inventory(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        try {
            if (ftWarehouseStorage != null) {
                vars.put("inventory", ftWarehouseStorage.jsonBikeIstanceList());
                LOG.info("inventory out:: {}", vars);
                client.newCompleteCommand(job.getKey()).variables(vars).send().join();
            }
        } catch(Exception e){
            LOG.error("Error in inventory", e);
            failJob(client, job, e.getMessage());
        }
    }

// Worker for starting WarehouseFetch

    @JobWorker(type = "startWarehouseFetch")
    public void startWarehouseFetch(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        warehouseFetchCorrelation = (String) vars.get("warehouseFetchCorrelation");
        LOG.info("startWarehouseFetch vars {}", vars);
        ftfactoryZEEBEClient.newPublishMessageCommand()
                .messageName(MSG_WAREHOUSE_START_FETCH)
                .correlationKey(warehouseFetchCorrelation)
                .variables(vars)
                .send().join();
    }

// Worker for starting WarehousePut

    @JobWorker(type = "startWarehousePut")
    public void startWarehousePut(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        warehousePutCorrelation = (String) vars.get("warehousePutCorrelation");
        LOG.info("startWarehousePut {}", vars);
        ftfactoryZEEBEClient.newPublishMessageCommand()
                .messageName(MSG_WAREHOUSE_START_PUT)
                .correlationKey(warehousePutCorrelation)
                .variables(vars)
                .send().join();
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
