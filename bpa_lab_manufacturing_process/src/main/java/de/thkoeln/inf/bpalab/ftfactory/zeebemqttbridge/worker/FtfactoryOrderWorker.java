package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.worker;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.Order;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.CloudTopics;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.publisher.FtfactoryPubOrder;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.sender.OrderShippedMessage;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber.FtfactoryOrder;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;

@Component
public class FtfactoryOrderWorker extends AWorker {
		
	@Autowired
	private FtfactoryOrder ftfactoryOrder;		
	
//	@Value("${ftfactorymqttclient.cloudTopicPrefix}")
//	private String cloudTopicPrefix;

	@Autowired
	private FtfactoryPubOrder pubOrder;
//	private CloudTopicPrefix cloudTopicPrefix;

	@JobWorker(type = "orderWithShippedReplyMessage")
	public Map<String, Object> orderWithShippedReplyMessage(final ActivatedJob job) {
		logJobStart(job);

		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());

		if (!this.ftfactoryMQTTClient.isConnected()) {
			  throw new ZeebeBpmnError("factoryOrderError", "factory order failed due to an error");						
		}
		if (this.ftfactoryOrder.isOrdered()) {
			  throw new ZeebeBpmnError("factoryOrderOtherError", "factory busy for another order");						
		}
//		prepare and publish Order to ftfactoryMQTT
//		FtfactoryPubOrder pubOrder = new FtfactoryPubOrder(job.getVariables());
		pubOrder.updateOrder(job.getVariables());
		this.ftfactoryMQTTClient.publish(pubOrder.getTopicPayload());
		
//		prepare ReplyMessage
		ftfactoryOrder.initTypeCorrelationValue(pubOrder.type);
		
//		set process variable for MessageEvent
//		variables.put("orderReplyMessage", ftfactoryOrder.getReplyMessageName());
		
		logJobEnd(job);

		return variables;
	}

	@JobWorker(type = "orderState")
	public Map<String, Object> orderState(final ActivatedJob job) {
		logJobStart(job);

		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());
		if (this.ftfactoryOrder != null && this.ftfactoryOrder.isMessageReceived()) {
			if (variables.get("OrderType") != null && variables.get("OrderType").equals(this.ftfactoryOrder.getType())) {
				variables.put("f_orderType", this.ftfactoryOrder.getType());
				variables.put("f_orderState", this.ftfactoryOrder.getState());
			}
		} else {
			variables.put("f_orderType", "NONE");
			variables.put("f_orderState", "NONE");			
		}

		logJobEnd(job);
		
		return variables;
	}

}
