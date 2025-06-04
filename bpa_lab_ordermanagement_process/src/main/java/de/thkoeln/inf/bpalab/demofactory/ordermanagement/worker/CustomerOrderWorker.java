package de.thkoeln.inf.bpalab.demofactory.ordermanagement.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrderItem;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.BikeModelDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.OfferOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.OfferDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.OrderOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.CustomerOrderService;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.OfferService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomerOrderWorker {
	private final static Logger LOG = LoggerFactory.getLogger(CustomerOrderWorker.class);
	private ObjectMapper objectMapper = new ObjectMapper();

	private OfferService offerService;
	private CustomerOrderService customerOrderService;
	private BikeInstanceRepository bikeInstanceRepository;

	@Autowired
	private ZeebeClient zeebeClient;

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
	public OrderOrderDTO putOrder(final ActivatedJob job) throws JsonProcessingException {
		OfferOrderDTO offerOrderDTO = job.getVariablesAsType(OfferOrderDTO.class);
		LOG.info("put Order offer {} ", objectMapper.writeValueAsString(offerOrderDTO));

		CustomerOrder customerOrder = customerOrderService.createCustomerOrder(offerOrderDTO);
		OrderOrderDTO orderOrderDTO = customerOrderService.getOrderOrderDTO(customerOrder);
		LOG.info("put order customerOrder {}",
				objectMapper.writeValueAsString(orderOrderDTO));
		return customerOrderService.getOrderOrderDTO(customerOrder);
	}
}
