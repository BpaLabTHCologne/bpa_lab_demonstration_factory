package de.thkoeln.inf.bpalab.demofactory.common.repos;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface BikeInstanceRepository extends JpaRepository<BikeInstance, UUID> {

    BikeInstance findFirstByBikeModelAndCustomerOrderNumberIsNull(BikeModel bikeModel);

    int countByBikeModelAndCustomerOrderNumber(BikeModel bikeModel, String customerOrderNumber);

    List<BikeInstance> findAllByCustomerOrderNumber(String customerOrderNumber);

    BikeModel findBySerialNumber(String bikeModelSerialNumber);
}
