package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.worker;

import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.message.FtfactoryManufactureEndMessage;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class FtfactoryManufactureWorker extends AWorker {

	@Autowired
	FtfactoryManufactureEndMessage ftfactoryManufactureEndMessage;

	@JobWorker(type="sendFinishedBikeModelManufactureOrder")
	public Map<String, Object> ensureStorageAvailable(final ActivatedJob job) {
		logJobStart(job);
		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());

		String manufactureOrderCorrelation = job.getVariable("manufactureOrderCorrelation").toString();

		ftfactoryManufactureEndMessage.setReplyMessageCorrelationValue(manufactureOrderCorrelation);

		this.ftfactoryZEEBEClient.newPublishMessageCommand()
				.messageName(ftfactoryManufactureEndMessage.getReplyMessageName())
				.correlationKey(ftfactoryManufactureEndMessage.getReplyMessageCorrelationValue())
				.timeToLive(Duration.ofSeconds(60))
				.send();

		log.info("\npublished zeebemessage {} correlationvalue {}", ftfactoryManufactureEndMessage.getReplyMessageName(), ftfactoryManufactureEndMessage.getReplyMessageCorrelationValue());

		logJobEnd(job);

		return variables;
	}
	
}
