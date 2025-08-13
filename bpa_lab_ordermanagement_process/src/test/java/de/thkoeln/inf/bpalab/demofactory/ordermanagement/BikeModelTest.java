package de.thkoeln.inf.bpalab.demofactory.ordermanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.common.service.BikeInstanceService;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.service.OfferService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BikeModelTest {
    static final Logger LOG = LoggerFactory.getLogger(BikeModelTest.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    BikeModelRepository bikeModelRepository;
    @Autowired
    CustomerOrderRepository customerOrderRepository;
    @Autowired
    OfferService offerService;

    @Autowired
    BikeInstanceRepository bikeInstanceRepository;

    @Autowired
    BikeInstanceService bikeInstanceService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllBikes() throws JsonProcessingException {
        for (BikeModel bikeModel : bikeModelRepository.findAll()) {
            LOG.info(objectMapper.writeValueAsString(bikeModel));
        }
    }

    @Test
    void getOfferJson() throws JsonProcessingException {
        LOG.info("getOfferJson {}", objectMapper.writeValueAsString(offerService.getOfferDTO()));
    }

}