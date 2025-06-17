package de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.common.dto.BikeModelDTO;
import de.thkoeln.inf.bpalab.demofactory.common.dto.ProductionOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.*;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.CustomerOrderService;
import de.thkoeln.inf.bpalab.demofactory.common.service.ProductionOrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootTest
class ProductionOrderTest {
    static final Logger LOG = LoggerFactory.getLogger(ProductionOrderTest.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    CustomerOrderRepository customerOrderRepository;

    @Autowired
    CustomerOrderService customerOrderService;

    OfferOrderDTO offerOrderDTO;

    @Autowired
    private BikeModelRepository bikeModelRepository;

    @Autowired
    private ProductionOrderService productionOrderService;

    @BeforeEach
    void setUp() {
        offerOrderDTO = new OfferOrderDTO();
        offerOrderDTO.orderCustomer = new CustomerOrderCustomerDTO();
        offerOrderDTO.orderCustomer.name = "Paul Paulsen";
        offerOrderDTO.orderCustomer.email = "paul@paulsen.de";
        offerOrderDTO.orderCustomer.adress = "Paul Straße 12, 51611 Pauldorf";
        offerOrderDTO.orderDate = LocalDate.now().toString();
        offerOrderDTO.offerBikeModelList = new ArrayList<>();
        for (BikeModel bikeModel : bikeModelRepository.findAll()) {
            BikeModelDTO bikeModelDTO = new BikeModelDTO();
            bikeModelDTO.amount = 2;
            bikeModelDTO.color = bikeModel.getColor();
            bikeModelDTO.title = bikeModel.getTitle();
            bikeModelDTO.weight = bikeModel.getWeight();
            offerOrderDTO.offerBikeModelList.add(bikeModelDTO);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testProductionOrder() throws JsonProcessingException {
        CustomerOrder customerOrder = customerOrderRepository.findAll().getFirst();
        if (customerOrder != null) {
            LOG.info("testProductionOrder found {}", objectMapper.writeValueAsString(customerOrder));
            OrderOrderDTO orderOrderDTO = customerOrderService.getOrderOrderDTO(customerOrder);
            LOG.info("testProductionOrder orderOrder {}",
                    objectMapper.writeValueAsString(orderOrderDTO));
            for (OrderItemDTO bikeModel : orderOrderDTO.produceBikeModelList) {
                productionOrderService.createProductionOrder(customerOrder.getCustomerOrderNumber(), bikeModel);
            }
            for (ProductionOrderDTO productionOrder : productionOrderService.getAllProductionOrders()) {
                LOG.info("testProductionOrder allProductionOrders {}",
                        objectMapper.writeValueAsString(productionOrder));
            }
        } else
            LOG.info("testOrderOrderDTO customerOrder not found");
    }
}