package de.thkoeln.inf.bpalab.demofactory.common.service;

import de.thkoeln.inf.bpalab.demofactory.common.domain.ProductionOrder;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.ProductionOrderRepository;
import de.thkoeln.inf.bpalab.demofactory.common.dto.OrderItemDTO;
import de.thkoeln.inf.bpalab.demofactory.common.dto.ProductionOrderDTO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductionOrderService {
    private final BikeModelRepository bikeModelRepository;
    private final ProductionOrderRepository productionOrderRepository;

    public ProductionOrderService(BikeModelRepository bikeModelRepository, ProductionOrderRepository productionOrderRepository) {
        this.bikeModelRepository = bikeModelRepository;
        this.productionOrderRepository = productionOrderRepository;
    }

    public List<ProductionOrderDTO> getAllProductionOrders() {
        List<ProductionOrder> productionOrders = productionOrderRepository.findAll();
        return productionOrders.stream()
                .map(productionOrder -> mapToDto(productionOrder, new ProductionOrderDTO()))
                .toList();
    }

    public ProductionOrderDTO mapToDto(ProductionOrder productionOrder, ProductionOrderDTO productionOrderDTO) {
        productionOrderDTO.productionOrderNumber = productionOrder.getProductionOrderNumber();
        productionOrderDTO.orderNumber = productionOrder.getCustomerOrderNumber();
        productionOrderDTO.produceBikeModel = new OrderItemDTO();
        productionOrderDTO.produceBikeModel.title = productionOrder.getBikeModel().getTitle();
        productionOrderDTO.produceBikeModel.amount = productionOrder.getQuantity();
        return productionOrderDTO;
    }

    public String getNextOrderNumber() {
        Long orderCount = productionOrderRepository.count();
        String orderNumber;
        if (orderCount > 0) {
            ProductionOrder productionOrder = productionOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "productionOrderNumber")).get(0);
            Long foundOrderNumber = Long.parseLong(productionOrder.getProductionOrderNumber());
            orderNumber = String.format("%08d", foundOrderNumber + 1);
        } else {
            orderNumber = String.format("%08d", 1);
        }
        return orderNumber;
    }

    public ProductionOrderDTO createProductionOrder(String orderNumber, OrderItemDTO orderItemDTO) {
        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setProductionOrderNumber(getNextOrderNumber());
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
