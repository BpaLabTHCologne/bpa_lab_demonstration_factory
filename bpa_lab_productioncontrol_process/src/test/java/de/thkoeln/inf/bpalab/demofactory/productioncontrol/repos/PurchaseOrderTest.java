package de.thkoeln.inf.bpalab.demofactory.productioncontrol.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.dto.OrderItemDTO;
import de.thkoeln.inf.bpalab.demofactory.productioncontrol.dto.ProductionOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.common.dto.PurchaseOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeComponentRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.PurchaseOrderRepository;
import de.thkoeln.inf.bpalab.demofactory.productioncontrol.service.ProductionOrderService;
import de.thkoeln.inf.bpalab.demofactory.common.service.PurchaseOrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PurchaseOrderTest {
    static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderTest.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BikeComponentRepository bikeComponentRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private ProductionOrderService productionOrderService;
    @Autowired
    private ProductionOrderRepository productionOrderRepository;
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testPurchaseOrder() throws JsonProcessingException {
        ProductionOrderDTO productionOrderDTO = productionOrderService.getAllProductionOrders().get(0);
        if (productionOrderDTO != null) {
            LOG.info("testPurchaseOrder found {}", objectMapper.writeValueAsString(productionOrderDTO));
            OrderItemDTO orderItemDTO = productionOrderDTO.produceBikeModel;
            orderItemDTO.amount = productionOrderDTO.produceBikeModel.amount;
            PurchaseOrderDTO purchaseOrderDTO = purchaseOrderService.createPurchaseOrder(productionOrderDTO.productionOrderNumber
                    , productionOrderDTO.produceBikeModel);
            LOG.info("testPurchaseOrder created {}", objectMapper.writeValueAsString(purchaseOrderDTO));
        } else
            LOG.info("testOrderOrderDTO productionOrder not found");
    }
}