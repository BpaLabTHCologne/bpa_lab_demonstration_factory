package de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    ProductionOrder findFirstByBikeModel(BikeModel bikeModel);

    List<ProductionOrder> findByCustomerOrderNumber(String customerOrderNumber);

}
