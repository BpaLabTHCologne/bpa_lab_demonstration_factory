package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

import java.util.HashMap;
import java.util.Map;

import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber.FtfactoryBME680;

@Component
public class FtfactoryFactoryStateWorker extends AWorker {
	
	@Autowired
	private FtfactoryBME680 ftfactoryBME680;
	
	@JobWorker(type = "retrieveFactoryState")
	public Map<String, Object> retrieveFactoryState(final ActivatedJob job) {
		logJobStart(job);
	
		String factoryProdEnv = System.getenv("FACTORY_PROD");
		boolean isFactoryProd = factoryProdEnv != null && factoryProdEnv.equalsIgnoreCase("true");
	
		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());
	
		if (isFactoryProd) {
			if (!this.ftfactoryMQTTClient.isConnected()) {
				throw new ZeebeBpmnError("factoryStateError", "Retrieve of factory state failed due to an error");
			}
	
			if (ftfactoryBME680.isMessageReceived()) {
				float temperatur = ftfactoryBME680.getT();
				float luftfeuchtigkeit = ftfactoryBME680.getH();
				variables.put("f_temperatur", temperatur);
				variables.put("f_luftfeuchtigkeit", luftfeuchtigkeit);
				log.info("\nfactoryState>>> [temp: {}, hum: {}]", temperatur, luftfeuchtigkeit);
			}
		} else {
			log.info("FACTORY_PROD is not true. Skipping actual business logic of the worker.");
		}
	
		logJobEnd(job);
		return variables;
	}

}
