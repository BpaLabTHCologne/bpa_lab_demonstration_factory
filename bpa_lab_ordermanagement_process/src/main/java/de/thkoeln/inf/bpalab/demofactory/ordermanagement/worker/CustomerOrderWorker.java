package de.thkoeln.inf.bpalab.demofactory.ordermanagement.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.*;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.BikeInstanceService;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.CustomerOrderService;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.OfferService;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.ProductionOrderService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

	public CustomerOrderWorker(OfferService offerService, CustomerOrderService customerOrderService) {
		this.offerService = offerService;
		this.customerOrderService = customerOrderService;
	}

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
		LOG.info("saveProductionOrder productionOrderDTO {} ", objectMapper.writeValueAsString(productionOrderDTO));
		return productionOrderService.createProductionOrder(productionOrderDTO.orderNumber, productionOrderDTO.produceBikeModel);
	}

	@JobWorker(type = "reserveBikeInstance", fetchVariables={"orderNumber", "reserveBikeInstance"})
	public ReserveOrderDTO reserveBikeInstance(final ActivatedJob job) throws JsonProcessingException {
		ReserveOrderDTO reserveOrderDTO = job.getVariablesAsType(ReserveOrderDTO.class);
		LOG.info("reserveBikeInstance reserveOrderDTO {} ", objectMapper.writeValueAsString(reserveOrderDTO));
		bikeInstanceService.reserveBikeInstance(reserveOrderDTO);
		return reserveOrderDTO;
	}
}
