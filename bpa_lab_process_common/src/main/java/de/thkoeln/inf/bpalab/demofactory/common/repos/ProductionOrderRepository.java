package de.thkoeln.inf.bpalab.demofactory.common.repos;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.common.domain.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, String> {

    ProductionOrder findFirstByBikeModel(BikeModel bikeModel);

    List<ProductionOrder> findByCustomerOrderNumber(String customerOrderNumber);

}
