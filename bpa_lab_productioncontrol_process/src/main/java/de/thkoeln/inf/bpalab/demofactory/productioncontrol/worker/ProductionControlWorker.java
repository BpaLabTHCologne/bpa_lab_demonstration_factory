package de.thkoeln.inf.bpalab.demofactory.productioncontrol.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeComponent;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.common.dto.*;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.ProductionOrderRepository;
import de.thkoeln.inf.bpalab.demofactory.common.service.BikeComponentService;
import de.thkoeln.inf.bpalab.demofactory.common.service.BikeInstanceService;
import de.thkoeln.inf.bpalab.demofactory.common.service.PurchaseOrderService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    @Autowired
    private BikeInstanceService bikeInstanceService;
    @Autowired
    private BikeInstanceRepository bikeInstanceRepository;

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
		// generating Input List for multiinstance subprocess
		ArrayList<String> manufactureBikeInstanceList = new ArrayList<>();
		for (int i = 0; i < productionOrderDTO.produceBikeModel.amount; i++) {
			manufactureBikeInstanceList.add(productionOrderDTO.produceBikeModel.title);
		}
		variables.put("manufactureBikeInstanceList", manufactureBikeInstanceList);
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

	@JobWorker(type = "sendPurchaseOrder", fetchVariables={"productionOrderNumber", "purchaseOrderNumber", "purchaseBikeComponent"})
	public Map<String, Object> sendPurchaseOrder(final ActivatedJob job) throws JsonProcessingException {
		PurchaseOrderDTO purchaseOrderDTO = job.getVariablesAsType(PurchaseOrderDTO.class);
		String purchaseOrderCorrelation = purchaseOrderDTO.purchaseOrderNumber + "-" + purchaseOrderDTO.productionOrderNumber;
		Map<String, Object> variables = new HashMap<>();
		variables.put("purchaseOrderCorrelation", purchaseOrderCorrelation);
		variables.putAll(job.getVariablesAsMap());
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


	@JobWorker(type = "manufactureBikeInstance", fetchVariables={"orderNumber", "produceBikeModel", "purchaseCount"})
	public Map<String, Object> manufactureBikeInstance(final ActivatedJob job) throws JsonProcessingException {
		Map<String, Object> variables = job.getVariablesAsMap();
		LOG.info("manufactureBikeInstance variables {}"
				, objectMapper.writeValueAsString(variables));
		ProductionOrderDTO productionOrderDTO = job.getVariablesAsType(ProductionOrderDTO.class);
		BikeComponent bikeComponent = bikeComponentService.getBikeComponentsByBikeModelTitle(productionOrderDTO.produceBikeModel.title);
		bikeComponentService.decreaseBikeComponentQuantity(bikeComponent.getTitle(), 1);
		BikeInstance bikeInstance = bikeInstanceService.produceBikeInstance(productionOrderDTO.produceBikeModel.title);
		variables.put("bikeInstanceSerialNumber", bikeInstance.getSerialNumber());
		return variables;
	}

	@JobWorker(type = "reserveProductionBikeInstance", fetchVariables={"bikeInstanceSerialNumber", "orderNumber"})
	public void reserveProductionBikeInstance(final ActivatedJob job) throws JsonProcessingException {
		Map<String, Object> variables = job.getVariablesAsMap();
		String bikeInstanceSerialNumber = variables.get("bikeInstanceSerialNumber").toString();
		String orderNumber = variables.get("orderNumber").toString();
		bikeInstanceService.reserveBikeInstance(bikeInstanceSerialNumber, orderNumber);
	}


	@JobWorker(type = "sendFinishedBikeModelProductionOrder", fetchAllVariables = true)
	public void sendFinishedBikeModelProductionOrder(final ActivatedJob job) throws JsonProcessingException {
		Map<String, Object> variables = job.getVariablesAsMap();
		LOG.info("sendFinishedBikeModelProductionOrder variables {}"
				, objectMapper.writeValueAsString(variables));
//		String productionOrderCorrelation = job.getVariable("productionOrderCorrelation").toString();
		String productionOrderCorrelation = variables.get("productionOrderCorrelation").toString();
		LOG.info("sendFinishedBikeModelProductionOrder correlationKey {}"
				, productionOrderCorrelation);
		zeebeClient.newPublishMessageCommand()
				.messageName("MsgProductionFinished")
				.correlationKey(productionOrderCorrelation)
				.send().join();

	}

}
