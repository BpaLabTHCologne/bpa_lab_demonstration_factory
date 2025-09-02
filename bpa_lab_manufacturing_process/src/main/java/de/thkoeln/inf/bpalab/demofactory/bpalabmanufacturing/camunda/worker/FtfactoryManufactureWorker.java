package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.worker;

import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.message.FtfactoryManufactureEndMessage;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.subscriber.FtfactorySubOrder;
import io.camunda.zeebe.client.api.command.ClientException;
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
	private FtfactorySubOrder ftfactorySubOrder;

	@Autowired
	FtfactoryManufactureEndMessage ftfactoryManufactureEndMessage;

	@JobWorker(type="sendFinishedBikeModelManufactureOrder")
	public Map<String, Object> ensureStorageAvailable(final ActivatedJob job) {
		logJobStart(job);
		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());

		if (variables.containsKey("manufactureOrderCorrelation")) {
			try {
				String manufactureOrderCorrelation = job.getVariable("manufactureOrderCorrelation").toString();

				ftfactoryManufactureEndMessage.setReplyMessageCorrelationValue(manufactureOrderCorrelation);

				this.ftfactoryZEEBEClient.newPublishMessageCommand()
						.messageName(ftfactoryManufactureEndMessage.getReplyMessageName())
						.correlationKey(ftfactoryManufactureEndMessage.getReplyMessageCorrelationValue())
						.timeToLive(Duration.ofSeconds(60))
						.send();
				log.info("\npublished zeebemessage {} correlationvalue {}", ftfactoryManufactureEndMessage.getReplyMessageName(), ftfactoryManufactureEndMessage.getReplyMessageCorrelationValue());
			} catch (ClientException e) {
				log.info("\nsendFinishedBikeModelManufactureOrder without sending");
			}
		} else {
			log.info("finished BikeModel Manufacturing without sending message");

		}
		ftfactorySubOrder.clearFtfactoryOrder();


		logJobEnd(job);

		return variables;
	}
	
}
