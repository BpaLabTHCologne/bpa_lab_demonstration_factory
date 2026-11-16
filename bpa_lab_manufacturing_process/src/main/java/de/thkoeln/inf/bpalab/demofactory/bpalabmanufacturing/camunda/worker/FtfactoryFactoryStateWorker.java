package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.worker;

import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.subscriber.FtfactoryBME680;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.exception.CamundaError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FtfactoryFactoryStateWorker extends AWorker {
	
	@Autowired
	private FtfactoryBME680 ftfactoryBME680;

	// defaults = BME680 operation range for temperature (-40 to 85 °C)
	@Value("${ftfactory.startcondition.temperature-min:-40.0}")
	private float temperatureMin;

	@Value("${ftfactory.startcondition.temperature-max:85.0}")
	private float temperatureMax;
	
	@JobWorker(type = "retrieveFactoryState")
	public Map<String, Object> retrieveFactoryState(final ActivatedJob job) {
		logJobStart(job);
	
		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());
	
		Float temperatur = null;
		if (!this.ftfactoryMQTTClient.isConnected()) {
			temperatur = 24.6f;
			float luftfeuchtigkeit = 0.45f;
			variables.put("f_temperatur", temperatur);
			variables.put("f_luftfeuchtigkeit", luftfeuchtigkeit);
			log.info("\nFaked factoryState>>> [temp: {}, hum: {}]", temperatur, luftfeuchtigkeit);
		} else if (ftfactoryBME680.isMessageReceived()) {
			temperatur = ftfactoryBME680.getT();
			float luftfeuchtigkeit = ftfactoryBME680.getH();
			variables.put("f_temperatur", temperatur);
			variables.put("f_luftfeuchtigkeit", luftfeuchtigkeit);
			log.info("\nfactoryState>>> [temp: {}, hum: {}]", temperatur, luftfeuchtigkeit);
		}

		boolean temperatureOk = isTemperatureInRange(temperatur, temperatureMin, temperatureMax);
		variables.put("f_temperatureOk", temperatureOk);
		log.info("\nstartCondition>>> [temp: {}, range: {}..{}, ok: {}]", temperatur, temperatureMin, temperatureMax, temperatureOk);

		if (!temperatureOk) {
			String reason = (temperatur == null)
					? "no BME680 temperature reading"
					: "temperature " + temperatur + " °C outside " + temperatureMin + ".." + temperatureMax + " °C";
			// retries = 0: no automatic retry, Camunda raises an incident and the instance
			// stops at this task until it is retried in Operate
			throw CamundaError.jobError("Start condition failed: " + reason, null, 0);
		}

		logJobEnd(job);
		return variables;
	}

	// null (no sensor reading yet) and NaN are treated as out of range
	static boolean isTemperatureInRange(Float temperature, float min, float max) {
		return temperature != null && temperature >= min && temperature <= max;
	}

}
