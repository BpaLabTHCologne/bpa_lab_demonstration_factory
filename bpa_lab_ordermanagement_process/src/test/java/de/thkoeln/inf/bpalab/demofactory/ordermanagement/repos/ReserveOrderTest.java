package de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.*;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.BikeInstanceService;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.ProductionOrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

@SpringBootTest
class ReserveOrderTest {
    static final Logger LOG = LoggerFactory.getLogger(ReserveOrderTest.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductionOrderService productionOrderService;
    @Autowired
    private BikeInstanceService bikeInstanceService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testReserveOrder() throws JsonProcessingException {
        for (ProductionOrderDTO productionOrder :
                productionOrderService.getAllProductionOrdersForCustomerOrder("00000009")) {
            LOG.info("testReserveOrder allProductionOrders {}",
                    objectMapper.writeValueAsString(productionOrder));
            ReserveOrderDTO reserveOrderDTO = new ReserveOrderDTO();
            reserveOrderDTO.orderNumber = productionOrder.orderNumber;
            reserveOrderDTO.reserveBikeInstance = new OrderItemDTO();
            reserveOrderDTO.reserveBikeInstance.title = productionOrder.produceBikeModel.title;
            for (int i = 1; i <= productionOrder.produceBikeModel.amount; i++) {
                try {
                    bikeInstanceService.reserveBikeInstance(reserveOrderDTO);
                } catch (NoSuchElementException e) {
                    LOG.info("testReserveOrder reserveBikeInstance fail {}",
                            objectMapper.writeValueAsString(reserveOrderDTO));
                }
            }
            for (BikeInstance bikeInstance : bikeInstanceService.getBikeInstancesForCustomerOrder(productionOrder.orderNumber)) {
                LOG.info("testReserveOrder reservedBikeInstance {}",
                        objectMapper.writeValueAsString(bikeInstance));

            }

        }
    }
}