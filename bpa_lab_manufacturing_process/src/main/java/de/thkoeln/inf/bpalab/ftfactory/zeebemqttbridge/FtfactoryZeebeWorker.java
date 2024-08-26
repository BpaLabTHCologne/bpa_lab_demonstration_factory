package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.sender.FtfactoryHBWStartMessage;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.worker.AWorker;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.worker.FtfactoryFactoryStateWorker;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.worker.FtfactoryHBWWorker;
import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.worker.FtfactoryOrderWorker;

@EnableZeebeClient
@Component
public class FtfactoryZeebeWorker extends AWorker {

	@Autowired
	private FtfactoryFactoryStateWorker ftfactoryFactoryStateWorker;

	@Autowired
	private FtfactoryOrderWorker ftfactoryOrderWorker;
	
	@Autowired
	private FtfactoryHBWWorker ftfactoryHBWWorker;

	@Autowired
	private FtfactoryHBWStartMessage ftfactoryHBWStartMessage;

	@JobWorker(type="ensureStorageAvailable")	
	public Map<String, Object> ensureStorageAvailable(final ActivatedJob job) {
		logJobStart(job);

		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());

		if (!this.ftfactoryMQTTClient.isConnected()) {
			  throw new ZeebeBpmnError("factoryOrderError", "factory order failed due to an error");						
		}

		Order order = new Order(job.getVariables());
		ftfactoryHBWStartMessage.setReplyMessageCorrelationValue(order.type);
		
		HashMap<String, Object> sendVariables = new HashMap<>();
		sendVariables.put("OrderType", order.type);
				
		this.ftfactoryZEEBEClient.newPublishMessageCommand()
					.messageName(ftfactoryHBWStartMessage.getReplyMessageName())
					.correlationKey(ftfactoryHBWStartMessage.getReplyMessageCorrelationValue())
					.variables(sendVariables)
					.timeToLive(Duration.ofSeconds(60))
					.send();		

		log.info("\npublished zeebemessage {} correlationvalue {}", ftfactoryHBWStartMessage.getReplyMessageName(), ftfactoryHBWStartMessage.getReplyMessageCorrelationValue());
		logJobEnd(job);

		return variables;
	}
	
	@JobWorker(type="completedManufacturingOrder")
	public void completedManufacturingOrder(final ActivatedJob job) {
		logJobStart(job);

		// Abrufen des correlationValue als Integer
		Integer correlationValue = (Integer) job.getVariablesAsMap().get("correlationValue");

		if (correlationValue == null) {
			throw new ZeebeBpmnError("correlationValueError", "No correlation value found");
		}

		// Konvertieren des Integer-Werts in einen String für die Nachricht
		String correlationValueStr = correlationValue.toString();

		this.ftfactoryZEEBEClient.newPublishMessageCommand()
			.messageName("manufacturingOrderCompleted")
			.correlationKey(correlationValueStr)
			.send()
			.join(); // Die Nachricht wird synchron gesendet

		log.info("Published message 'manufacturingOrderCompleted' with correlationValue {}", correlationValueStr);
		logJobEnd(job);
	}
}
