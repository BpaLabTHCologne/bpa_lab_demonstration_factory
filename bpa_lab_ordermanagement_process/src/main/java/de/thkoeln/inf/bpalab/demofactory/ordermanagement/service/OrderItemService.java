package de.thkoeln.inf.bpalab.demofactory.ordermanagement.service;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrderItem;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.BikeModelDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderItemRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    private final BikeModelRepository bikeModelRepository;
    private CustomerOrderItemRepository customerOrderItemRepository;

    public OrderItemService(BikeModelRepository bikeModelRepository, CustomerOrderItemRepository customerOrderItemRepository) {
        this.bikeModelRepository = bikeModelRepository;
        this.customerOrderItemRepository = customerOrderItemRepository;
    }

    public CustomerOrderItem save(CustomerOrder customerOrder, BikeModelDTO bikeModelDTO) {
        CustomerOrderItem customerOrderItem = new CustomerOrderItem();
        customerOrderItem.setOrder(customerOrder);
        customerOrderItem.setBikeModel(bikeModelRepository.getReferenceById(bikeModelDTO.title));
        customerOrderItem.setQuantity(bikeModelDTO.amount);
        return customerOrderItemRepository.save(customerOrderItem);
    }
}
