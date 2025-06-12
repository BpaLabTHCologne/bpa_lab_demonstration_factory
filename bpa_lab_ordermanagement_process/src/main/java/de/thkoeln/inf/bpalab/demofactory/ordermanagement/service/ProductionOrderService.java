package de.thkoeln.inf.bpalab.demofactory.ordermanagement.service;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.ProductionOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.OrderItemDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.ProductionOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.ProductionOrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductionOrderService {
    private final BikeModelRepository bikeModelRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final CustomerOrderRepository customerOrderRepository;

    public ProductionOrderService(BikeModelRepository bikeModelRepository, ProductionOrderRepository productionOrderRepository, CustomerOrderRepository customerOrderRepository) {
        this.bikeModelRepository = bikeModelRepository;
        this.productionOrderRepository = productionOrderRepository;
        this.customerOrderRepository = customerOrderRepository;
    }

    public List<ProductionOrderDTO> getAllProductionOrders() {
        List<ProductionOrder> productionOrders = productionOrderRepository.findAll();
        return productionOrders.stream()
                .map(productionOrder -> mapToDto(productionOrder, new ProductionOrderDTO()))
                .toList();
    }

    public ProductionOrderDTO mapToDto(ProductionOrder productionOrder, ProductionOrderDTO productionOrderDTO) {
        productionOrderDTO.id = productionOrder.getId();
        productionOrderDTO.orderNumber = productionOrder.getCustomerOrderNumber();
        productionOrderDTO.produceBikeModel = new OrderItemDTO();
        productionOrderDTO.produceBikeModel.title = productionOrder.getBikeModel().getTitle();
        productionOrderDTO.produceBikeModel.amount = productionOrder.getQuantity();
        return productionOrderDTO;
    }

    public ProductionOrderDTO createProductionOrder(String orderNumber, OrderItemDTO orderItemDTO) {
        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setCustomerOrderNumber(orderNumber);
        productionOrder.setBikeModel(bikeModelRepository.getReferenceById(orderItemDTO.title));
        productionOrder.setQuantity(orderItemDTO.amount);
        productionOrder = productionOrderRepository.save(productionOrder);
        return mapToDto(productionOrder, new ProductionOrderDTO());
    }

    public List<ProductionOrderDTO> getAllProductionOrdersForCustomerOrder(String orderNumber) {
        List<ProductionOrder> productionOrderList
                = new ArrayList<>(productionOrderRepository
                        .findByCustomerOrderNumber(orderNumber));
        return productionOrderList.stream()
                .map(productionOrder -> mapToDto(productionOrder, new ProductionOrderDTO()))
                .toList();
    }
}
