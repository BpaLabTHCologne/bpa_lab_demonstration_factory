package de.thkoeln.inf.bpalab.demofactory.common.service;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeComponent;
import de.thkoeln.inf.bpalab.demofactory.common.domain.PurchaseOrder;
import de.thkoeln.inf.bpalab.demofactory.common.dto.OrderItemDTO;
import de.thkoeln.inf.bpalab.demofactory.common.dto.PurchaseOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.common.repos.PurchaseOrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseOrderService {
    private final BikeComponentService bikeComponentService;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final BikeModelRepository bikeModelRepository;

    public PurchaseOrderService(BikeComponentService bikeComponentService, PurchaseOrderRepository purchaseOrderRepository, BikeModelRepository bikeModelRepository) {
        this.bikeComponentService = bikeComponentService;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.bikeModelRepository = bikeModelRepository;
    }

    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
        return purchaseOrders.stream()
                .map(purchaseOrder -> mapToDto(purchaseOrder, new PurchaseOrderDTO()))
                .toList();
    }

    public PurchaseOrderDTO mapToDto(PurchaseOrder purchaseOrder, PurchaseOrderDTO purchaseOrderDTO) {
        purchaseOrderDTO.productionOrderNumber = purchaseOrder.getProductionOrderNumber();
        purchaseOrderDTO.purchaseOrderNumber = purchaseOrder.getPurchaseOrderNumber();
        purchaseOrderDTO.purchaseBikeComponent = new OrderItemDTO();
        purchaseOrderDTO.purchaseBikeComponent.title = purchaseOrder.getBikeComponent().getTitle();
        purchaseOrderDTO.purchaseBikeComponent.amount = purchaseOrder.getQuantity();
        return purchaseOrderDTO;
    }

    public String getNextOrderNumber() {
        Long orderCount = purchaseOrderRepository.count();
        String orderNumber;
        if (orderCount > 0) {
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "purchaseOrderNumber")).get(0);
            Long foundOrderNumber = Long.parseLong(purchaseOrder.getPurchaseOrderNumber());
            orderNumber = String.format("%08d", foundOrderNumber + 1);
        } else {
            orderNumber = String.format("%08d", 1);
        }
        return orderNumber;
    }

    public PurchaseOrderDTO createPurchaseOrder(String productionOrderNumber, OrderItemDTO orderItemDTO) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderNumber(getNextOrderNumber());
        purchaseOrder.setProductionOrderNumber(productionOrderNumber);
        BikeComponent bikeComponent = bikeComponentService.getBikeComponentsByBikeModelTitle(orderItemDTO.title);
        purchaseOrder.setBikeComponent(bikeComponent);
        purchaseOrder.setQuantity(orderItemDTO.amount);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return mapToDto(purchaseOrder, new PurchaseOrderDTO());
    }

    public List<PurchaseOrderDTO> getAllPurchaseOrderByProductionOrder(String orderNumber) {
        List<PurchaseOrder> purchaseOrderList
                = new ArrayList<>(purchaseOrderRepository
                        .findByProductionOrderNumber(orderNumber));
        return purchaseOrderList.stream()
                .map(purchaseOrder -> mapToDto(purchaseOrder, new PurchaseOrderDTO()))
                .toList();
    }
}
