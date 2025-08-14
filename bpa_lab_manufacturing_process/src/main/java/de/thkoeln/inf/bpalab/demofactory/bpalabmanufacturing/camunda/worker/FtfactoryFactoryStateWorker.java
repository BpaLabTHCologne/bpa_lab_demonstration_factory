package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.worker;

import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.subscriber.FtfactoryBME680;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FtfactoryFactoryStateWorker extends AWorker {
	
	@Autowired
	private FtfactoryBME680 ftfactoryBME680;
	
	@JobWorker(type = "retrieveFactoryState")
	public Map<String, Object> retrieveFactoryState(final ActivatedJob job) {
		logJobStart(job);
	
		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());
	
		if (!this.ftfactoryMQTTClient.isConnected()) {
			float temperatur = 24.6f;
			float luftfeuchtigkeit = 0.45f;
			variables.put("f_temperatur", temperatur);
			variables.put("f_luftfeuchtigkeit", luftfeuchtigkeit);
			log.info("\nFaked factoryState>>> [temp: {}, hum: {}]", temperatur, luftfeuchtigkeit);
			return variables;
		}

		if (ftfactoryBME680.isMessageReceived()) {
			float temperatur = ftfactoryBME680.getT();
			float luftfeuchtigkeit = ftfactoryBME680.getH();
			variables.put("f_temperatur", temperatur);
			variables.put("f_luftfeuchtigkeit", luftfeuchtigkeit);
			log.info("\nfactoryState>>> [temp: {}, hum: {}]", temperatur, luftfeuchtigkeit);
		}

		logJobEnd(job);
		return variables;
	}

}
