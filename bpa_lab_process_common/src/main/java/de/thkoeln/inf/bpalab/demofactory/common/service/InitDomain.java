package de.thkoeln.inf.bpalab.demofactory.common.service;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeComponent;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeComponentRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitDomain {

    @Autowired
    BikeModelRepository bikeModelRepository;

    @Autowired
    BikeInstanceRepository bikeInstanceRepository;

    @Autowired
    BikeComponentRepository bikeComponentRepository;

    public InitDomain(BikeModelRepository bikeModelRepository
            ,BikeInstanceRepository bikeInstanceRepository
            ,BikeComponentRepository bikeComponentRepository) {
        this.bikeModelRepository = bikeModelRepository;
        this.bikeInstanceRepository = bikeInstanceRepository;
        this.bikeComponentRepository = bikeComponentRepository;
        initBikeModel(bikeModelRepository);
        initBikeInstance(bikeInstanceRepository);
        initBikeComponent(bikeComponentRepository);
    }

    private void initBikeModel(BikeModelRepository bikeModelRepository) {
        BikeModel bikeModel = new BikeModel();
        bikeModel.setTitle("Esel Lastenrad");
        bikeModel.setColor("RED");
        bikeModel.setWeight(24.5);
        bikeModelRepository.save(bikeModel);
        bikeModel = new BikeModel();
        bikeModel.setTitle("Ziege Mountainbike");
        bikeModel.setColor("BLUE");
        bikeModel.setWeight(17.5);
        bikeModelRepository.save(bikeModel);
        bikeModel = new BikeModel();
        bikeModel.setTitle("Schaf Citybike");
        bikeModel.setColor("WHITE");
        bikeModel.setWeight(20.6);
        bikeModelRepository.save(bikeModel);
    }

    private void initBikeInstance(BikeInstanceRepository bikeInstanceRepository) {
        if (bikeInstanceRepository.count() != 0)
            return;
        BikeInstance bikeInstance = new BikeInstance();
        bikeInstance.setBikeModel(bikeModelRepository.getReferenceById("Esel Lastenrad"));
        bikeInstance.setShipped(false);
        bikeInstanceRepository.save(bikeInstance);
        bikeInstance = new BikeInstance();
        bikeInstance.setBikeModel(bikeModelRepository.getReferenceById("Esel Lastenrad"));
        bikeInstance.setShipped(false);
        bikeInstanceRepository.save(bikeInstance);
        bikeInstance = new BikeInstance();
        bikeInstance.setBikeModel(bikeModelRepository.getReferenceById("Ziege Mountainbike"));
        bikeInstance.setShipped(false);
        bikeInstanceRepository.save(bikeInstance);
    }

    private void initBikeComponent(BikeComponentRepository bikeComponentRepository) {
        if (bikeComponentRepository.count() != 0)
            return;
        BikeComponent bikeComponent = new BikeComponent();
        BikeModel bikeModel = bikeModelRepository.getReferenceById("Schaf Citybike");
        bikeComponent.setBikeModel(bikeModel);
        bikeComponent.setColor(bikeModel.getColor());
        bikeComponent.setQuantity(0);
        bikeComponent.setTitle("Citybike Component Kit");
        bikeComponentRepository.save(bikeComponent);
        bikeComponent = new BikeComponent();
        bikeModel = bikeModelRepository.getReferenceById("Ziege Mountainbike");
        bikeComponent.setBikeModel(bikeModel);
        bikeComponent.setColor(bikeModel.getColor());
        bikeComponent.setQuantity(3);
        bikeComponent.setTitle("Mountainbike Component Kit");
        bikeComponentRepository.save(bikeComponent);
        bikeComponent = new BikeComponent();
        bikeModel = bikeModelRepository.getReferenceById("Esel Lastenrad");
        bikeComponent.setBikeModel(bikeModel);
        bikeComponent.setColor(bikeModel.getColor());
        bikeComponent.setQuantity(3);
        bikeComponent.setTitle("Lastenrad Component Kit");
        bikeComponentRepository.save(bikeComponent);
    }
}
