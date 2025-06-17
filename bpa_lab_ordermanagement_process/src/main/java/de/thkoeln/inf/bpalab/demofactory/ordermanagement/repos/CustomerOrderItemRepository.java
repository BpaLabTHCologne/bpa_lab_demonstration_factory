package de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerOrderItemRepository extends JpaRepository<CustomerOrderItem, Long> {

    CustomerOrderItem findFirstByBikeModel(BikeModel bikeModel);

    CustomerOrderItem findFirstByOrder(CustomerOrder customerOrder);

}
