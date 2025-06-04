package de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, String> {

    boolean existsByCustomerOrderNumberIgnoreCase(String customerOrderNumber);

    CustomerOrder findAllOrderByCustomerOrderNumber(String customerOrderNumber, Sort sort);
}
