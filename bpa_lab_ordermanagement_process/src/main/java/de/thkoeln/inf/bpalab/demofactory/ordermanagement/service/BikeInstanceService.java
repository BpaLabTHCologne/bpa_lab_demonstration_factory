package de.thkoeln.inf.bpalab.demofactory.ordermanagement.service;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.OrderItemDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.ReserveOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BikeInstanceService {

    @Autowired
    private BikeInstanceRepository bikeInstanceRepository;
    @Autowired
    private BikeModelRepository bikeModelRepository;
    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    public BikeInstanceService(BikeInstanceRepository bikeInstanceRepository) {
        this.bikeInstanceRepository = bikeInstanceRepository;
    }

    public int countBikeInstanceNotReserved(BikeModel bikeModel) {
        return bikeInstanceRepository.countByBikeModelAndCustomerOrder(bikeModel, null);
    }

    public List<BikeInstance> getBikeInstancesForCustomerOrder(String orderNumber) {
        CustomerOrder customerOrder = customerOrderRepository.getReferenceById(orderNumber);
        return bikeInstanceRepository.findAllByCustomerOrder(customerOrder);
    }

    public BikeInstance createBikeInstance(BikeModel bikeModel, CustomerOrder customerOrder) {
        BikeInstance bikeInstance = new BikeInstance();
        bikeInstance.setCustomerOrder(customerOrder);
        bikeInstance.setBikeModel(bikeModel);
        return bikeInstanceRepository.save(bikeInstance);
    }

    public void reserveBikeInstance(ReserveOrderDTO reserveOrderDTO) throws NoSuchElementException {
        CustomerOrder customerOrder = customerOrderRepository.getReferenceById(reserveOrderDTO.orderNumber);
        OrderItemDTO orderItemDTO = reserveOrderDTO.reserveBikeInstance;
        BikeModel bikeModel = bikeModelRepository.findById(orderItemDTO.title).orElse(null);
        if (bikeModel != null) {
            BikeInstance bikeInstance = bikeInstanceRepository
                    .findFirstByBikeModelAndCustomerOrderIsNull(bikeModel);
            if (bikeInstance != null) {
                bikeInstance.setCustomerOrder(customerOrder);
                bikeInstanceRepository.save(bikeInstance);
            } else
                throw new NoSuchElementException();
        }
    }
}
