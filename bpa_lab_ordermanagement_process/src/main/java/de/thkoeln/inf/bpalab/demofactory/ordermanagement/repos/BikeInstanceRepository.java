package de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeInstance;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BikeInstanceRepository extends JpaRepository<BikeInstance, Long> {

    BikeInstance findFirstByBikeModel(BikeModel bikeModel);

    int countByBikeModelAndCustomerOrder(BikeModel bikeModel, CustomerOrder customerOrder);

    List<BikeInstance> findAllByCustomerOrder(CustomerOrder customerOrder);

    List<BikeInstance> findAllByCustomerOrderIsNull();
}
