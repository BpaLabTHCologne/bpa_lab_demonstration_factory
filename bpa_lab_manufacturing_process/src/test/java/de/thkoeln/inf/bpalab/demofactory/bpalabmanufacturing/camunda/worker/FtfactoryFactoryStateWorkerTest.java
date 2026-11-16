package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.worker;

import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.FtfactoryMQTTClient;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.subscriber.FtfactoryBME680;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.exception.JobError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Runs the retrieveFactoryState worker with a given BME680 temperature
 * and checks the start condition f_temperatureOk (range -40 to 85 °C).
 * No Spring context, Zeebe or MQTT broker needed: collaborators are mocked.
 */
@ExtendWith(MockitoExtension.class)
class FtfactoryFactoryStateWorkerTest {

	@Mock
	private FtfactoryMQTTClient ftfactoryMQTTClient;

	@Mock
	private FtfactoryBME680 ftfactoryBME680;

	@Mock
	private ActivatedJob job;

	@InjectMocks
	private FtfactoryFactoryStateWorker worker;

	@BeforeEach
	void setUp() {
		// @Value fields are not populated without a Spring context
		ReflectionTestUtils.setField(worker, "temperatureMin", -40.0f);
		ReflectionTestUtils.setField(worker, "temperatureMax", 85.0f);
	}

	@Test
	@DisplayName("Normal range: 24.6 °C → start allowed")
	void normalRange() {
		assertThat(startConditionFor(24.6f)).isTrue();
	}

	@Test
	@DisplayName("Boundary value: 85.0 °C → start allowed (inclusive)")
	void boundaryValue() {
		assertThat(startConditionFor(85.0f)).isTrue();
	}

	@Test
	@DisplayName("Error range: 100.0 °C → execution stopped (job fails without retry)")
	void errorRange() {
		assertThatThrownBy(() -> startConditionFor(100.0f)).isInstanceOf(JobError.class);
	}

	/** Runs the worker as if the sensor reported the given temperature and returns f_temperatureOk. */
	private boolean startConditionFor(float temperature) {
		when(ftfactoryMQTTClient.isConnected()).thenReturn(true);
		when(ftfactoryBME680.isMessageReceived()).thenReturn(true);
		when(ftfactoryBME680.getT()).thenReturn(temperature);
		when(job.getVariablesAsMap()).thenReturn(new HashMap<>());

		Map<String, Object> variables = worker.retrieveFactoryState(job);
		return (Boolean) variables.get("f_temperatureOk");
	}
}
