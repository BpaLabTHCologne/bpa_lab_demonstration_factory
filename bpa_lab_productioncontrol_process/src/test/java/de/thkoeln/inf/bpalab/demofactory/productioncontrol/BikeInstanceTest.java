package de.thkoeln.inf.bpalab.demofactory.productioncontrol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.common.service.BikeInstanceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class BikeInstanceTest {
    static final Logger LOG = LoggerFactory.getLogger(BikeInstanceTest.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    BikeModelRepository bikeModelRepository;

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
    void getBikeInstance() throws JsonProcessingException {

        ArrayList<BikeInstance> bikeInstances = new ArrayList<BikeInstance>(
                bikeInstanceRepository.findAll());
        for (BikeInstance bikeInstance : bikeInstances) {
            LOG.info("all bikeInstance {}", objectMapper.writeValueAsString(bikeInstance));
        }
        for (BikeModel bikeModel : bikeModelRepository.findAll()) {
            LOG.info("instanceCount free {} {}",
                    objectMapper.writeValueAsString(bikeModel),
                    objectMapper.writeValueAsString(bikeInstanceService.countBikeInstanceNotReserved(bikeModel)));
        }
    }

}