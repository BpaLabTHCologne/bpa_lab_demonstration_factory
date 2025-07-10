package de.thkoeln.inf.bpalab.demofactory.common.service;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeComponent;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.common.dto.OrderItemDTO;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeComponentRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class BikeComponentService {
    @Autowired
    private BikeModelRepository bikeModelRepository;

    @Autowired
    private BikeComponentRepository bikeComponentRepository;

    public int countBikeComponentsByBikeModelId(String bikeModelId) {
        BikeModel bikeModel = bikeModelRepository.getReferenceById(bikeModelId);
        BikeComponent bikeComponent = bikeComponentRepository.getAllByBikeModel(bikeModel).getFirst();
        if (bikeComponent != null) return bikeComponent.getQuantity();
        else throw new NoSuchElementException();
    }

    public BikeComponent getBikeComponentsByBikeModelTitle(String title) {
        BikeModel bikeModel = bikeModelRepository.getReferenceById(title);
        return bikeComponentRepository.getAllByBikeModel(bikeModel).getFirst();
    }

    public void decreaseBikeComponentQuantity(String title, Integer quantity) {
        BikeComponent bikeComponent = bikeComponentRepository.getReferenceById(title);
        bikeComponent.setQuantity(bikeComponent.getQuantity() - quantity);
        bikeComponentRepository.save(bikeComponent);
    }
}
