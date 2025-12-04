package de.thkoeln.inf.bpalab.demofactory.productioncontrol.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeComponent;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.common.dto.OrderItemDTO;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.productioncontrol.dto.ProductionOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.productioncontrol.dto.PurchaseSendDTO;
import de.thkoeln.inf.bpalab.demofactory.productioncontrol.repos.ProductionOrderRepository;
import de.thkoeln.inf.bpalab.demofactory.common.service.BikeComponentService;
import de.thkoeln.inf.bpalab.demofactory.common.service.BikeInstanceService;
import de.thkoeln.inf.bpalab.demofactory.productioncontrol.service.ProductionOrderService;
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

import static io.camunda.zeebe.client.impl.util.VersionUtil.LOG;

@Component
public class ProductionControlWorker {
	private final static Logger LOG = LoggerFactory.getLogger(ProductionControlWorker.class);
	private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductionOrderRepository productionOrderRepository;
	@Autowired
	private ProductionOrderService productionOrderService;
	@Autowired
	private BikeComponentService bikeComponentService;
	@Autowired
	private ZeebeClient zeebeClient;
    @Autowired
    private BikeInstanceService bikeInstanceService;
    @Autowired
    private BikeInstanceRepository bikeInstanceRepository;

    private final String MSG_WAREHOUSE_START_PUT = "MsgStartWarehousePut";

    @JobWorker(type = "createProductionOrder", fetchVariables={"orderNumber", "produceBikeModel"})
	public ProductionOrderDTO createProductionOrder(final ActivatedJob job) throws JsonProcessingException {
		ProductionOrderDTO productionOrderDTO = job.getVariablesAsType(ProductionOrderDTO.class);
//		if (productionOrderDTO.orderNumber == null)
//			productionOrderDTO.orderNumber = "no Order";
		LOG.info("saveProductionOrder {} ", objectMapper.writeValueAsString(productionOrderDTO));
		productionOrderDTO = productionOrderService.createProductionOrder(productionOrderDTO.orderNumber, productionOrderDTO.produceBikeModel);
		LOG.info("productionOrderSaved {} ", objectMapper.writeValueAsString(productionOrderDTO));
		return productionOrderDTO;
	}

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
		BikeComponent bikeComponent = bikeComponentService.getBikeComponentsByBikeModelTitle(productionOrderDTO.produceBikeModel.title);
		PurchaseSendDTO purchaseSendDTO = new PurchaseSendDTO();
		purchaseSendDTO.productionOrderNumber = productionOrderDTO.productionOrderNumber;
		purchaseSendDTO.purchaseBikeComponent = new OrderItemDTO();
		purchaseSendDTO.purchaseBikeComponent.title = bikeComponent.getTitle();
		purchaseSendDTO.purchaseBikeComponent.amount = productionOrderDTO.produceBikeModel.amount;
		variables.put("purchaseBikeComponent", purchaseSendDTO.purchaseBikeComponent);

		return variables;
	}

	@JobWorker(type = "sendPurchaseOrder", fetchVariables={"productionOrderNumber", "purchaseBikeComponent"})
	public Map<String, Object> sendPurchaseOrder(final ActivatedJob job) throws JsonProcessingException {
		PurchaseSendDTO purchaseSendDTO = job.getVariablesAsType(PurchaseSendDTO.class);
		LOG.info("sendPurchaseOrder {} ", objectMapper.writeValueAsString(purchaseSendDTO));
		String purchaseOrderCorrelation = purchaseSendDTO.productionOrderNumber;
		Map<String, Object> variables = job.getVariablesAsMap();
		variables.put("purchaseOrderCorrelation", purchaseOrderCorrelation);
		LOG.info("sendPurchaseOrder correlationKey: {}"
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

	@JobWorker(type = "sendManufactureOrder", fetchVariables={"productionOrderNumber", "produceBikeModel", "loopCounter"})
	public Map<String, Object> sendManufactureOrder(final ActivatedJob job) throws JsonProcessingException {
		Map<String, Object> variables = job.getVariablesAsMap();
		LOG.info("sendManufactureOrder variables {}"
				, objectMapper.writeValueAsString(variables));

		ProductionOrderDTO productionOrderDTO = job.getVariablesAsType(ProductionOrderDTO.class);
		String loopCounter = job.getVariable("loopCounter").toString();
		String manufactureOrderCorrelation = productionOrderDTO.productionOrderNumber + "-" + loopCounter;
		variables.put("manufactureOrderCorrelation", manufactureOrderCorrelation);

		BikeComponent bikeComponent = bikeComponentService.getBikeComponentsByBikeModelTitle(productionOrderDTO.produceBikeModel.title);
		variables.put("orderType", bikeComponent.getColor());
		LOG.info("sendManufactureOrder correlationKey: {} orderType {}"
				, manufactureOrderCorrelation, bikeComponent.getColor());
		zeebeClient.newPublishMessageCommand()
				.messageName("MsgStartManufactureOrder")
				.correlationKey(manufactureOrderCorrelation)
				.variables(variables)
				.send().join();
		return variables;

	}

	@JobWorker(type = "reserveProductionBikeInstance", fetchVariables={"bikeInstanceSerialNumber", "orderNumber"})
	public void reserveProductionBikeInstance(final ActivatedJob job) throws JsonProcessingException {
		Map<String, Object> variables = job.getVariablesAsMap();
		String bikeInstanceSerialNumber = variables.get("bikeInstanceSerialNumber").toString();
		String orderNumber = null;
		if (variables.get("orderNumber") != null)
			orderNumber = variables.get("orderNumber").toString();
		bikeInstanceService.reserveBikeInstance(bikeInstanceSerialNumber, orderNumber);
	}

// Worker for starting WarehousePut

    @JobWorker(type = "startWarehousePut")
    public void startWarehousePut(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        Map<String, Object> varsOut = new HashMap<>();

        String manufactureOrderCorrelation = (String) vars.get("manufactureOrderCorrelation");
        vars.put("warehousePutCorrelation", manufactureOrderCorrelation);
        varsOut.put("warehousePutCorrelation", manufactureOrderCorrelation);
        String id = vars.get("bikeInstanceSerialNumber").toString();
        varsOut.put("id", id);
        String color = vars.get("orderType").toString();
        varsOut.put("color", color);
        LOG.info("startWarehousePut {}", varsOut);
        zeebeClient.newPublishMessageCommand()
                .messageName(MSG_WAREHOUSE_START_PUT)
                .correlationKey(manufactureOrderCorrelation)
                .variables(varsOut)
                .send().join();
    }


    @JobWorker(type = "sendFinishedBikeModelProductionOrder", fetchAllVariables = true)
	public void sendFinishedBikeModelProductionOrder(final ActivatedJob job) throws JsonProcessingException {
		Map<String, Object> variables = job.getVariablesAsMap();
		LOG.info("sendFinishedBikeModelProductionOrder variables {}"
				, objectMapper.writeValueAsString(variables));
//		String productionOrderCorrelation = job.getVariable("productionOrderCorrelation").toString();
		if (variables.containsKey("productionOrderCorrelation")) {
			String productionOrderCorrelation = variables.get("productionOrderCorrelation").toString();
			LOG.info("sendFinishedBikeModelProductionOrder correlationKey {}"
					, productionOrderCorrelation);
			zeebeClient.newPublishMessageCommand()
					.messageName("MsgProductionFinished")
					.correlationKey(productionOrderCorrelation)
					.send().join();
		} else {
			LOG.info("finished BikeModel Production Order without sending message");
		}
	}

}
