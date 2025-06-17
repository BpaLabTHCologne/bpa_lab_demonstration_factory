package de.thkoeln.inf.bpalab.demofactory.ordermanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.thkoeln.inf.bpalab.demofactory.common.dto.BikeModelDTO;
import de.thkoeln.inf.bpalab.demofactory.common.dto.OrderItemDTO;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.common.service.BikeInstanceService;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrderItem;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.*;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderItemRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemService orderItemService;
    private final BikeInstanceService bikeInstanceService;

    public CustomerOrderService(final CustomerOrderRepository customerOrderRepository
            , CustomerOrderItemRepository customerOrderItemRepository
            , OrderItemService orderItemService, BikeInstanceRepository bikeInstanceRepository
            , BikeInstanceService bikeInstanceService) {
        this.customerOrderRepository = customerOrderRepository;
        this.orderItemService = orderItemService;
        this.bikeInstanceService = bikeInstanceService;
    }

    public String getNextOrderNumber() {
        Long orderCount = customerOrderRepository.count();
        String orderNumber;
        if (orderCount > 0) {
            CustomerOrder customerOrder = customerOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "customerOrderNumber")).get(0);
            Long foundOrderNumber = Long.parseLong(customerOrder.getCustomerOrderNumber());
            orderNumber = String.format("%08d", foundOrderNumber + 1);
        } else {
            orderNumber = String.format("%08d", 1);
        }
        return orderNumber;
    }

    public List<OfferOrderDTO> getCustomerOrders() {
        List<CustomerOrder> customerOrders = customerOrderRepository.findAll();
        return customerOrders.stream()
                .map(customerOrder -> mapToDTO(customerOrder, new OfferOrderDTO()))
                .toList();
    }

    public CustomerOrder createCustomerOrder(OfferOrderDTO offerOrderDTO) {
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setCustomerOrderNumber(getNextOrderNumber());
        customerOrder.setCreationDate(offerOrderDTO.orderDate);
        customerOrder.setCustomerName(offerOrderDTO.orderCustomer.name);
        customerOrder.setCustomerAdress(offerOrderDTO.orderCustomer.adress);
        customerOrder.setCustomerEmail(offerOrderDTO.orderCustomer.email);
        customerOrderRepository.save(customerOrder);
        for (BikeModelDTO bikeModelDTO : offerOrderDTO.offerBikeModelList) {
            if (bikeModelDTO.amount != null && bikeModelDTO.amount > 0) {
                orderItemService.save(customerOrder, bikeModelDTO);
            }
        }
        customerOrder = customerOrderRepository.findById(customerOrder.getCustomerOrderNumber()).get();
        return customerOrder;
    }

    public OfferOrderDTO mapToDTO(final CustomerOrder customerOrder, final OfferOrderDTO offerOrderDTO) {
        offerOrderDTO.orderCustomer = new CustomerOrderCustomerDTO();
        offerOrderDTO.orderNumber = customerOrder.getCustomerOrderNumber();
        offerOrderDTO.orderCustomer.name = customerOrder.getCustomerName();
        offerOrderDTO.orderCustomer.adress = customerOrder.getCustomerAdress();
        offerOrderDTO.orderCustomer.email = customerOrder.getCustomerEmail();
        return offerOrderDTO;
    }

    public OrderOrderDTO getOrderOrderDTO(final CustomerOrder customerOrder) throws JsonProcessingException {
        OrderOrderDTO orderOrderDTO = new OrderOrderDTO(customerOrder);
        ArrayList<OrderItemDTO> reserveList = orderOrderDTO.reserveBikeInstanceList = new ArrayList<>();
        ArrayList<OrderItemDTO> produceList = orderOrderDTO.produceBikeModelList = new ArrayList<>();
        for (CustomerOrderItem customerOrderItem : customerOrder.getOrderItems()) {
            int quantity = customerOrderItem.getQuantity();
            if (quantity > 0) {
                int availableBikeInstances = bikeInstanceService.countBikeInstanceNotReserved(customerOrderItem.getBikeModel());
                if (availableBikeInstances >= quantity) {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.title = customerOrderItem.getBikeModel().getTitle();
                    orderItemDTO.amount = customerOrderItem.getQuantity();
                    reserveList.add(orderItemDTO);
                } else {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.title = customerOrderItem.getBikeModel().getTitle();
                    orderItemDTO.amount = quantity - availableBikeInstances;
                    produceList.add(orderItemDTO);
                    if (availableBikeInstances > 0) {
                        orderItemDTO = new OrderItemDTO();
                        orderItemDTO.title = customerOrderItem.getBikeModel().getTitle();
                        orderItemDTO.amount = availableBikeInstances;
                        reserveList.add(orderItemDTO);
                    }
                }
            }
        }
        return orderOrderDTO;

    }
}
