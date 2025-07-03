package de.thkoeln.inf.bpalab.demofactory.common.repos;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeComponent;
import de.thkoeln.inf.bpalab.demofactory.common.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {

    PurchaseOrder findFirstByBikeComponent(BikeComponent bikeComponent);

    List<PurchaseOrder> findByProductionOrderNumber(String productionOrderNumber);

}
