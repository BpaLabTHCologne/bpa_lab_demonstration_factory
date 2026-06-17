package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.worker;

import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.publisher.FtfactoryPubOrder;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.subscriber.FtfactorySubOrder;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FtfactoryOrderWorker extends AWorker {
		
	@Autowired
	private FtfactorySubOrder ftfactorySubOrder;
	

	@Autowired
	private FtfactoryPubOrder pubOrder;

	@JobWorker(type = "sendMqttOrderToFtFactory")
	public Map<String, Object> sendMqttOrderToFtFactory(final ActivatedJob job) {
		logJobStart(job);

		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());

		if (this.ftfactorySubOrder.isOrdered()) {
			throw new ZeebeBpmnError("factoryOrderOtherError", "factory busy for another order", null);
		}

		pubOrder.updateOrder(job.getVariables());
        variables.put("processOrderReference", pubOrder.processOrderReference);

        //		prepare ReplyMessage
		ftfactorySubOrder.initTypeCorrelationValue(pubOrder.type); //normally "pubOrder.type" instead of correlationValueStr needs be tested if it works with factory connected!!!
        ftfactorySubOrder.setProcessOrderReference(pubOrder.processOrderReference);

		if (this.ftfactoryMQTTClient.isConnected()) {
	//		prepare and publish Order to ftfactoryMQTT
	//		FtfactoryPubOrder pubOrder = new FtfactoryPubOrder(job.getVariables());
			this.ftfactoryMQTTClient.publish(pubOrder.getTopicPayload());
		} else {
			ftfactorySubOrder.setType(pubOrder.type);
			ftfactorySubOrder.setState("SHIPPED");
			ftfactorySubOrder.setTs(pubOrder.ts);
			ftfactorySubOrder.sendOrderShippedMessage();
			ftfactorySubOrder.clearFtfactoryOrder();
			log.info("\nFaked sendMQTTOrder>>> [ordertype: {}, processOrderReference: {}]",
                    pubOrder.type, pubOrder.processOrderReference);
		}

		logJobEnd(job);

		return variables;
	}

	@JobWorker(type = "orderState")
	public Map<String, Object> orderState(final ActivatedJob job) {
		logJobStart(job);

		HashMap<String, Object> variables = new HashMap<>(job.getVariablesAsMap());

		if (this.ftfactorySubOrder != null && this.ftfactorySubOrder.isMessageReceived()) {
			if (variables.get("orderType") != null && variables.get("orderType").equals(this.ftfactorySubOrder.getType())) {
				variables.put("f_orderType", this.ftfactorySubOrder.getType());
				variables.put("f_orderState", this.ftfactorySubOrder.getState());
			}
		} else {
			variables.put("f_orderType", "NONE");
			variables.put("f_orderState", "NONE");
		}

		logJobEnd(job);
		return variables;
	}

}
