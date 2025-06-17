package de.thkoeln.inf.bpalab.demofactory.common.repos;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BikeInstanceRepository extends JpaRepository<BikeInstance, Long> {

    BikeInstance findFirstByBikeModelAndCustomerOrderNumberIsNull(BikeModel bikeModel);

    int countByBikeModelAndCustomerOrderNumber(BikeModel bikeModel, String customerOrderNumber);

    List<BikeInstance> findAllByCustomerOrderNumber(String customerOrderNumber);

}
