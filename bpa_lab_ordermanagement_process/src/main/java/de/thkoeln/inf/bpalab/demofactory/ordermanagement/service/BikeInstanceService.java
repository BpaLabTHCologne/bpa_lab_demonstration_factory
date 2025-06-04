package de.thkoeln.inf.bpalab.demofactory.ordermanagement.service;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.BikeInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BikeInstanceService {

    @Autowired
    private BikeInstanceRepository bikeInstanceRepository;

    public BikeInstanceService(BikeInstanceRepository bikeInstanceRepository) {
        this.bikeInstanceRepository = bikeInstanceRepository;
    }

    public int countBikeInstanceNotReserved(BikeModel bikeModel) {
        return bikeInstanceRepository.countByBikeModelAndCustomerOrder(bikeModel, null);
    }

    public List<BikeInstance> getBikeInstancesForCustomerOrder(CustomerOrder customerOrder) {
        return bikeInstanceRepository.findAllByCustomerOrder(customerOrder);
    }

    public BikeInstance createBikeInstance(BikeModel bikeModel, CustomerOrder customerOrder) {
        BikeInstance bikeInstance = new BikeInstance();
        bikeInstance.setCustomerOrder(customerOrder);
        bikeInstance.setBikeModel(bikeModel);
        return bikeInstanceRepository.save(bikeInstance);
    }
}
