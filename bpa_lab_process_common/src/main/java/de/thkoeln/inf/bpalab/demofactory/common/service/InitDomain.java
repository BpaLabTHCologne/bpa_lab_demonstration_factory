package de.thkoeln.inf.bpalab.demofactory.common.service;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
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

    public InitDomain(BikeModelRepository bikeModelRepository, BikeInstanceRepository bikeInstanceRepository) {
        this.bikeModelRepository = bikeModelRepository;
        this.bikeInstanceRepository = bikeInstanceRepository;
        initBikeModel(bikeModelRepository);
        initBikeInstance(bikeInstanceRepository);
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
}
