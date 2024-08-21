package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

//import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.Order;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber.FtfactoryHBW;

@Component
public class FtfactoryHBWWorker extends AWorker {
		
	@Autowired
	FtfactoryHBW ftfactoryHBW;
	
	@JobWorker(type = "sendFtfactoryStorageMessage")
	public void sendFtfactoryStorageMessage(final ActivatedJob job) {
		Order order = new Order(job.getVariables());
		if (this.ftfactoryHBW.isMessageReceived() && this.ftfactoryHBW.isAvailable(order.type)) {
			this.ftfactoryHBW.sendFtfactoryStorageMessage();
		}
	}
	
	@JobWorker(type = "retrieveHBWStorageState")
	public Map<String, Object> retrieveHBWStorageState(final ActivatedJob job) {
		logJobStart(job);

		if (!this.ftfactoryMQTTClient.isConnected()) {
			  throw new ZeebeBpmnError("factoryStateError", "Retrieve of factory state failed due to an error");						
		}

		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());
		
//		prepare ReplyMessage 

		Order order = new Order(job.getVariables());
		ftfactoryHBW.setReplyMessageCorrelationValue(order.type);
				
		if (this.ftfactoryHBW.isMessageReceived()) {
			variables.put("f_redItems", ftfactoryHBW.getWorkpieceCountRED());
			variables.put("f_whiteItems", ftfactoryHBW.getWorkpieceCountWHITE());
			variables.put("f_blueItems", ftfactoryHBW.getWorkpieceCountBLUE());
			if (ftfactoryHBW.isAvailable(order.type)) 
				variables.put("available", true);			
			else
				variables.put("available", false);			
		}  else {
			variables.put("available", false);			
		}

		logJobEnd(job);
		return variables;
	}

}
