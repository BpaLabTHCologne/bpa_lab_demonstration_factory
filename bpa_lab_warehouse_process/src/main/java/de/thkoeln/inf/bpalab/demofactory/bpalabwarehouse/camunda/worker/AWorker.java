package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.camunda.worker;

import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.FtwarehouseMQTTClient;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

public abstract class AWorker {

	protected static Logger log = LoggerFactory.getLogger(AWorker.class);
	
	@Autowired
	protected FtwarehouseMQTTClient ftWarehouseMQTTClient;

	@Autowired
 	protected ZeebeClient ftfactoryZEEBEClient;

	protected static void logJobStart(ActivatedJob job) {
	log.info(
		"\nstart job>>> [type: {}, key: {}, element: {}, workflow instance: {}]\n{deadline; {}]\n[headers: {}]\n[variables: {}]",
	      job.getType(),
	      job.getKey(),
	      job.getElementId(),
	      job.getProcessInstanceKey(),
	      Instant.ofEpochMilli(job.getDeadline()),
	      job.getCustomHeaders(),
	      job.getVariables());
	}

	protected static void logJobEnd(final ActivatedJob job) {
	log.info(
		"\ncomplete job>>> [type: {}, key: {}, element: {}, workflow instance: {}]\n{deadline; {}]\n[headers: {}]\n[variables: {}]",
	      job.getType(),
	      job.getKey(),
	      job.getElementId(),
	      job.getProcessInstanceKey(),
	      Instant.ofEpochMilli(job.getDeadline()),
	      job.getCustomHeaders(),
	      job.getVariables());
	}

}