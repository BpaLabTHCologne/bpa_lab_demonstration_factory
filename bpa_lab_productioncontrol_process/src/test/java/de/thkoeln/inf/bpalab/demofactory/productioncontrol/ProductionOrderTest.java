package de.thkoeln.inf.bpalab.demofactory.productioncontrol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.productioncontrol.service.ProductionOrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductionOrderTest {
    static final Logger LOG = LoggerFactory.getLogger(ProductionOrderTest.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BikeModelRepository bikeModelRepository;

    @Autowired
    private ProductionOrderService productionOrderService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testProductionOrder() throws JsonProcessingException {
//        CustomerOrder customerOrder = customerOrderRepository.findAll().getFirst();
//        if (customerOrder != null) {
//            LOG.info("testProductionOrder found {}", objectMapper.writeValueAsString(customerOrder));
//            OrderOrderDTO orderOrderDTO = customerOrderService.getOrderOrderDTO(customerOrder);
//            LOG.info("testProductionOrder orderOrder {}",
//                    objectMapper.writeValueAsString(orderOrderDTO));
//            for (OrderItemDTO bikeModel : orderOrderDTO.produceBikeModelList) {
//                productionOrderService.createProductionOrder(customerOrder.getCustomerOrderNumber(), bikeModel);
//            }
//            for (ProductionOrderDTO productionOrder : productionOrderService.getAllProductionOrders()) {
//                LOG.info("testProductionOrder allProductionOrders {}",
//                        objectMapper.writeValueAsString(productionOrder));
//            }
//        } else
//            LOG.info("testOrderOrderDTO customerOrder not found");
    }
}