package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.worker;

import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.FtfactoryMQTTClient;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

public abstract class AWorker {

	protected static Logger log = LoggerFactory.getLogger(AWorker.class);
	
	@Autowired
	protected FtfactoryMQTTClient ftfactoryMQTTClient;

	@Autowired
 	protected CamundaClient ftfactoryCamundaClient;

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