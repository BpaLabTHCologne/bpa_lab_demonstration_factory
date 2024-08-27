package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.worker;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.FtfactoryMQTTClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;

public abstract class AWorker {

	protected static Logger log = LoggerFactory.getLogger(AWorker.class);
	
	@Autowired
	protected FtfactoryMQTTClient ftfactoryMQTTClient;

	@Autowired
 	protected ZeebeClientLifecycle ftfactoryZEEBEClient;

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