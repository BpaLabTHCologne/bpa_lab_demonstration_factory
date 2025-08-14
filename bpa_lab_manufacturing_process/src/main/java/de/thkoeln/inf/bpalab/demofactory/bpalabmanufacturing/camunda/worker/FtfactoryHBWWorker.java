package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.worker;

import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.message.FtfactoryHBWAvailableMessage;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.subscriber.FtfactoryHBW;
import io.camunda.zeebe.client.api.command.ClientException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FtfactoryHBWWorker extends AWorker {
		
	@Autowired
	FtfactoryHBW ftfactoryHBW;

	@Autowired
	private FtfactoryHBWAvailableMessage ftfactoryHBWAvailableMessage;

	@JobWorker(type = "sendFtfactoryStorageMessage")
	public void sendFtfactoryStorageMessage(final ActivatedJob job) {
		if (this.ftfactoryMQTTClient.isConnected()) {
			String orderType = job.getVariable("orderType").toString();
			if (this.ftfactoryHBW.isMessageReceived() && this.ftfactoryHBW.isAvailable(orderType)) {
				this.sendFtfactoryStorageMessage();
			}
		} else {
			this.sendFtfactoryStorageMessage();
		}
	}

	@JobWorker(type = "retrieveHBWStorageState")
	public Map<String, Object> retrieveHBWStorageState(final ActivatedJob job) {
		logJobStart(job);

		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());

		if (!this.ftfactoryMQTTClient.isConnected()) {
			variables.put("available", true);
			log.info("\nFaked HBWStorageState>>> [available: {}]", true);
		}

		String orderType = job.getVariable("orderType").toString();
		String manufactureOrderCorrelation = null;
		try {
			if (job.getVariable("manufactureOrderCorrelation") != null)
				manufactureOrderCorrelation = job.getVariable("manufactureOrderCorrelation").toString();
			else
				manufactureOrderCorrelation = "intern";
		} catch (ClientException e) {
			manufactureOrderCorrelation = "intern";
		}

		//prepare ReplyMessage
		ftfactoryHBWAvailableMessage.setReplyMessageCorrelationValue(orderType);

		if (this.ftfactoryHBW.isMessageReceived()) {
			variables.put("f_redItems", ftfactoryHBW.getWorkpieceCountRED());
			variables.put("f_whiteItems", ftfactoryHBW.getWorkpieceCountWHITE());
			variables.put("f_blueItems", ftfactoryHBW.getWorkpieceCountBLUE());
			if (ftfactoryHBW.isAvailable(orderType))
				variables.put("available", true);
			else
				variables.put("available", false);
		}

		logJobEnd(job);
		return variables;
	}

	public void sendFtfactoryStorageMessage() {
		if (this.ftfactoryHBWAvailableMessage != null) {
			this.ftfactoryZEEBEClient.newPublishMessageCommand()
					.messageName(this.ftfactoryHBWAvailableMessage.getReplyMessageName())
					.correlationKey(this.ftfactoryHBWAvailableMessage.getReplyMessageCorrelationValue())
					.send();
			log.info("\nmessagename {} correlationvalue {}", this.ftfactoryHBWAvailableMessage.getReplyMessageName(), this.ftfactoryHBWAvailableMessage.getReplyMessageCorrelationValue());
		}
	}

}
