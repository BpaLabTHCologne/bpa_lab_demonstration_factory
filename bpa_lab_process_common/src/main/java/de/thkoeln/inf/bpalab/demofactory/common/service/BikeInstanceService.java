package de.thkoeln.inf.bpalab.demofactory.common.service;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.common.dto.OrderItemDTO;
import de.thkoeln.inf.bpalab.demofactory.common.dto.ReserveOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BikeInstanceService {

    @Autowired
    private BikeInstanceRepository bikeInstanceRepository;
    @Autowired
    private BikeModelRepository bikeModelRepository;

    public int countBikeInstanceNotReserved(BikeModel bikeModel) {
        return bikeInstanceRepository.countByBikeModelAndCustomerOrderNumberAndShippedFalse(bikeModel, null);
    }

    public List<BikeInstance> getBikeInstancesForCustomerOrder(String orderNumber) {
        return bikeInstanceRepository.findAllByCustomerOrderNumber(orderNumber);
    }

    public BikeInstance createBikeInstance(BikeModel bikeModel, String customerOrderNumber) {
        BikeInstance bikeInstance = new BikeInstance();
        bikeInstance.setCustomerOrder(customerOrderNumber);
        bikeInstance.setBikeModel(bikeModel);
        bikeInstance.setShipped(false);
        return bikeInstanceRepository.save(bikeInstance);
    }

    public BikeInstance produceBikeInstance(String bikeModelTitle) {
        BikeModel bikeModel = bikeModelRepository.getReferenceById(bikeModelTitle);
        BikeInstance bikeInstance = new BikeInstance();
        bikeInstance.setBikeModel(bikeModel);
        bikeInstance.setShipped(false);
        return bikeInstanceRepository.save(bikeInstance);
    }

    public void reserveBikeInstance(String bikeInstanceSerialNumber, String orderNumber) {
        UUID bikeInstanceUUID = UUID.fromString(bikeInstanceSerialNumber);
        BikeInstance bikeInstance = bikeInstanceRepository.getReferenceById(bikeInstanceUUID);
        if (bikeInstance != null) {
            if (orderNumber != null) {
                bikeInstance.setCustomerOrder(orderNumber);
                bikeInstanceRepository.save(bikeInstance);
            }
        } else
            throw new NoSuchElementException();
    }

    public void reserveBikeInstance(ReserveOrderDTO reserveOrderDTO) throws NoSuchElementException {
        OrderItemDTO orderItemDTO = reserveOrderDTO.reserveBikeInstance;
        BikeModel bikeModel = bikeModelRepository.findById(orderItemDTO.title).orElse(null);
        if (bikeModel != null) {
            for (int i = 0; i < reserveOrderDTO.reserveBikeInstance.amount; i++) {
                BikeInstance bikeInstance = bikeInstanceRepository
                        .findFirstByBikeModelAndCustomerOrderNumberIsNullAndShippedFalse(bikeModel);
                if (bikeInstance != null) {
                    bikeInstance.setCustomerOrder(reserveOrderDTO.orderNumber);
                    bikeInstanceRepository.save(bikeInstance);
                } else
                    throw new NoSuchElementException();
            }
        }
    }
}
