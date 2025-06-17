package de.thkoeln.inf.bpalab.demofactory.productioncontrol.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.dto.*;
import de.thkoeln.inf.bpalab.demofactory.common.repos.ProductionOrderRepository;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProductionControlWorker {
	private final static Logger LOG = LoggerFactory.getLogger(ProductionControlWorker.class);
	private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductionOrderRepository productionOrderRepository;
	@Autowired
	private ZeebeClient zeebeClient;

	@JobWorker(type = "getProductionOrder", fetchVariables={"productionOrderNumber", "orderNumber", "produceBikeModel"})
	public ProductionOrderDTO getProductionOrder(final ActivatedJob job) {
		ProductionOrderDTO productionOrderDTO = job.getVariablesAsType(ProductionOrderDTO.class);
		productionOrderRepository.findByCustomerOrderNumber(productionOrderDTO.productionOrderNumber);
		return productionOrderDTO;
	}

	@JobWorker(type = "sendFinishedBikeModelProductionOrder")
	public void sendFinishedBikeModelProductionOrder(final ActivatedJob job) throws JsonProcessingException {
		String productionOrderCorrelation = job.getVariable("productionOrderCorrelation").toString();
		Map<String, Object> variables = job.getVariablesAsMap();
		LOG.info("sendFinishedBikeModelProductionOrder correlationKey {} variables {}"
				, productionOrderCorrelation
				, objectMapper.writeValueAsString(variables));
		zeebeClient.newPublishMessageCommand()
				.messageName("MsgProductionFinished")
				.correlationKey(productionOrderCorrelation)
				.send().join();

	}

}
