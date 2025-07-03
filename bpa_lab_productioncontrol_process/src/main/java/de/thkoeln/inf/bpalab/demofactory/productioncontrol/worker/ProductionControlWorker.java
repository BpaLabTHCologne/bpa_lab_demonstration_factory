package de.thkoeln.inf.bpalab.demofactory.productioncontrol.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.dto.*;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeComponentRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.ProductionOrderRepository;
import de.thkoeln.inf.bpalab.demofactory.common.service.BikeComponentService;
import de.thkoeln.inf.bpalab.demofactory.common.service.PurchaseOrderService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProductionControlWorker {
	private final static Logger LOG = LoggerFactory.getLogger(ProductionControlWorker.class);
	private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductionOrderRepository productionOrderRepository;
	@Autowired
	private BikeComponentService bikeComponentService;
	@Autowired
	private ZeebeClient zeebeClient;
    @Autowired
    private PurchaseOrderService purchaseOrderService;

	@JobWorker(type = "createComponentDemand", fetchVariables={"productionOrderNumber", "orderNumber", "produceBikeModel"})
	public Map<String, Object> createComponentDemand(JobClient client, ActivatedJob job) {
		ProductionOrderDTO productionOrderDTO = job.getVariablesAsType(ProductionOrderDTO.class);
		HashMap<String, Object> variables = new HashMap<>();
		int amount = productionOrderDTO.produceBikeModel.amount;
		int quantity = bikeComponentService.countBikeComponentsByBikeModelId(productionOrderDTO.produceBikeModel.title);
		if (amount > quantity)
			variables.put("purchaseCount", amount - quantity);
		else
			variables.put("purchaseCount", 0);
		return variables;
	}

	@JobWorker(type = "savePurchaseOrder", fetchVariables={"productionOrderNumber", "produceBikeModel", "purchaseCount"})
	public PurchaseOrderDTO savePurchaseOrder(final ActivatedJob job) {
		ProductionOrderDTO productionOrderDTO = job.getVariablesAsType(ProductionOrderDTO.class);
		int purchaseCount = (int) job.getVariable("purchaseCount");
		productionOrderRepository.getReferenceById(productionOrderDTO.productionOrderNumber);
		productionOrderDTO.produceBikeModel.amount = purchaseCount;
		return purchaseOrderService.createPurchaseOrder(productionOrderDTO.productionOrderNumber
				, productionOrderDTO.produceBikeModel);
	}

	private void addComponents(PurchaseOrderDTO purchaseOrderDTO) {
		bikeComponentService.addBikeComponet(purchaseOrderDTO.purchaseBikeComponent);
	}

	@JobWorker(type = "sendPurchaseOrder", fetchVariables={"productionOrderNumber", "purchaseOrderNumber", "purchaseBikeComponent"})
	public Map<String, Object> sendPurchaseOrder(final ActivatedJob job) throws JsonProcessingException {
		PurchaseOrderDTO purchaseOrderDTO = job.getVariablesAsType(PurchaseOrderDTO.class);
		String purchaseOrderCorrelation = purchaseOrderDTO.purchaseOrderNumber + "-" + purchaseOrderDTO.productionOrderNumber;
		Map<String, Object> variables = new HashMap<>();
		variables.put("purchaseOrderCorrelation", purchaseOrderCorrelation);
		variables.putAll(job.getVariablesAsMap());
		// fake purchasing
//		addComponents(purchaseOrderDTO);
//		zeebeClient.newPublishMessageCommand()
//				.messageName("MsgPurchaseFinished")
//				.correlationKey(purchaseOrderCorrelation)
//				.variables(variables)
//				.send().join();
		// end fake
		LOG.info("sendPurchaseOrder purchaseOrderDTO {} correlationKey: {}"
				, objectMapper.writeValueAsString(purchaseOrderDTO)
				, purchaseOrderCorrelation);
		LOG.info("sendPurchaseOrder variables {}", objectMapper.writeValueAsString(variables));
		zeebeClient.newPublishMessageCommand()
				.messageName("MsgStartPurchaseOrder")
				.correlationKey(purchaseOrderCorrelation)
				.variables(variables)
				.send().join();
		return variables;
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
