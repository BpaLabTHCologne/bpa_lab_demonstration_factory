package de.thkoeln.inf.bpalab.demofactory.ordermanagement.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.dto.ProductionOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.common.dto.ReserveOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.*;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderRepository;
import de.thkoeln.inf.bpalab.demofactory.common.service.BikeInstanceService;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.CustomerOrderService;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.OfferService;
import de.thkoeln.inf.bpalab.demofactory.common.service.ProductionOrderService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomerOrderWorker {
	private final static Logger LOG = LoggerFactory.getLogger(CustomerOrderWorker.class);
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private OfferService offerService;
	@Autowired
	private CustomerOrderService customerOrderService;
	@Autowired
	private BikeInstanceService bikeInstanceService;
	@Autowired
	private ZeebeClient zeebeClient;
    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @Autowired
    private ProductionOrderService productionOrderService;

	@JobWorker(type = "getOffer")
	public OfferDTO getOffer(final ActivatedJob job) {
		OfferDTO offerDTO = offerService.getOfferDTO();
		String offerDTOString;
		try {
			offerDTOString = objectMapper.writeValueAsString(offerDTO);
		} catch (JsonProcessingException e) {
			offerDTOString = "{\"error\": \"" + e.getMessage() + "\"}";
		}

		LOG.info("offerDTO {} bike models", offerDTOString);
		return offerDTO;
	}

	@JobWorker(type = "putOrder")
	public OfferOrderDTO putOrder(final ActivatedJob job) throws JsonProcessingException {
		OfferOrderDTO offerOrderDTO = job.getVariablesAsType(OfferOrderDTO.class);
		LOG.info("put Order offer {} ", objectMapper.writeValueAsString(offerOrderDTO));

		CustomerOrder customerOrder = customerOrderService.createCustomerOrder(offerOrderDTO);
		offerOrderDTO.orderNumber = customerOrder.getCustomerOrderNumber();
		return offerOrderDTO;
	}

	@JobWorker(type = "createMaterialDemands")
	public OrderOrderDTO createMaterialDemands(final ActivatedJob job) throws JsonProcessingException {
		OfferOrderDTO offerOrderDTO = job.getVariablesAsType(OfferOrderDTO.class);
		LOG.info("createMaterialDemands offer {} ", objectMapper.writeValueAsString(offerOrderDTO));

		CustomerOrder customerOrder = customerOrderRepository.getReferenceById(offerOrderDTO.orderNumber);
		LOG.info("createMaterialDemands customerOrder {}", objectMapper.writeValueAsString(customerOrder));
		OrderOrderDTO orderOrderDTO = customerOrderService.getOrderOrderDTO(customerOrder);
		LOG.info("createMaterialDemand order {}",
				objectMapper.writeValueAsString(orderOrderDTO));
		return customerOrderService.getOrderOrderDTO(customerOrder);
	}

	@JobWorker(type = "saveProductionOrder", fetchVariables={"orderNumber", "produceBikeModel"})
	public ProductionOrderDTO saveProductionOrder(final ActivatedJob job) throws JsonProcessingException {
		ProductionOrderDTO productionOrderDTO = job.getVariablesAsType(ProductionOrderDTO.class);
		LOG.info("saveProductionOrder {} ", objectMapper.writeValueAsString(productionOrderDTO));
		productionOrderDTO = productionOrderService.createProductionOrder(productionOrderDTO.orderNumber, productionOrderDTO.produceBikeModel);
		LOG.info("productionOrderSaved {} ", objectMapper.writeValueAsString(productionOrderDTO));
		return productionOrderDTO;
	}

	@JobWorker(type = "reserveBikeInstance", fetchVariables={"orderNumber", "reserveBikeInstance"})
	public ReserveOrderDTO reserveBikeInstance(final ActivatedJob job) throws JsonProcessingException {
		ReserveOrderDTO reserveOrderDTO = job.getVariablesAsType(ReserveOrderDTO.class);
		LOG.info("reserveBikeInstance reserveOrderDTO {} ", objectMapper.writeValueAsString(reserveOrderDTO));
		bikeInstanceService.reserveBikeInstance(reserveOrderDTO);
		return reserveOrderDTO;
	}

	@JobWorker(type = "sendProductionOrder", fetchVariables={"productionOrderNumber", "orderNumber", "produceBikeModel"})
	public Map<String, Object> sendProductionOrder(final ActivatedJob job) throws JsonProcessingException {
		ProductionOrderDTO productionOrderDTO = job.getVariablesAsType(ProductionOrderDTO.class);
		String productionOrderCorrelation = productionOrderDTO.orderNumber + "-" + productionOrderDTO.productionOrderNumber;
		Map<String, Object> variables = new HashMap<>();
		variables.put("productionOrderCorrelation", productionOrderCorrelation);
		variables.putAll(job.getVariablesAsMap());
		LOG.info("sendProductionOrder productionOrderDTO {} correlationKey: {}"
				, objectMapper.writeValueAsString(productionOrderDTO)
				, productionOrderCorrelation);
		LOG.info("sendProductionOrder variables {}", objectMapper.writeValueAsString(variables));
		zeebeClient.newPublishMessageCommand()
				.messageName("MsgStartProductionOrder")
				.correlationKey(productionOrderCorrelation)
				.variables(variables)
				.send().join();
		return variables;
	}

	@JobWorker(type = "sendShipment")
	public void sendShipment(final ActivatedJob job) throws JsonProcessingException {
		Map<String, Object> variables = job.getVariablesAsMap();
		OfferOrderDTO offerOrderDTO = job.getVariablesAsType(OfferOrderDTO.class);
		String shipmentOrderCorrelation = offerOrderDTO.orderNumber;
		variables.put("shipmentOrderCorrelation", shipmentOrderCorrelation);
		variables.put("shippingAddress", offerOrderDTO.orderCustomer.adress);
		variables.put("orderNumber", offerOrderDTO.orderNumber);
		LOG.info("sendShipment variables {}", objectMapper.writeValueAsString(variables));
		zeebeClient.newPublishMessageCommand()
				.messageName("MsgStartShippingOrder")
				.correlationKey(shipmentOrderCorrelation)
				.variables(variables)
				.send().join();
	}
}
