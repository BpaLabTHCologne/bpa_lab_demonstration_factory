package de.thkoeln.inf.bpalab.demofactory.ordermanagement.service;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrderItem;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.BikeModelDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.CustomerOrderCustomerDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.OfferOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.OrderOrderDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.BikeInstanceRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderItemRepository;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos.CustomerOrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderItemRepository customerOrderItemRepository;
    private final OrderItemService orderItemService;
    private final BikeInstanceRepository bikeInstanceRepository;

    public CustomerOrderService(final CustomerOrderRepository customerOrderRepository
            , CustomerOrderItemRepository customerOrderItemRepository
            , OrderItemService orderItemService, BikeInstanceRepository bikeInstanceRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderItemRepository = customerOrderItemRepository;
        this.orderItemService = orderItemService;
        this.bikeInstanceRepository = bikeInstanceRepository;
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

    public OrderOrderDTO getOrderOrderDTO(final CustomerOrder customerOrder) {
        OrderOrderDTO orderOrderDTO = new OrderOrderDTO(customerOrder);
        orderOrderDTO.availableBikeInstanceList = new ArrayList<>();
        orderOrderDTO.produceBikeModelList = new ArrayList<>();
        for (CustomerOrderItem customerOrderItem : customerOrder.getOrderItems()) {
            int availableBikeInstances = bikeInstanceRepository.countByBikeModelAndCustomerOrder(customerOrderItem.getBikeModel(), null);
            int quantity = customerOrderItem.getQuantity();
            BikeModelDTO bikeModelDTO = new BikeModelDTO();
            bikeModelDTO.title = customerOrderItem.getBikeModel().getTitle();
            bikeModelDTO.color = customerOrderItem.getBikeModel().getColor();
            bikeModelDTO.weight = customerOrderItem.getBikeModel().getWeight();
            if (availableBikeInstances >= quantity) {
                bikeModelDTO.amount = quantity;
                orderOrderDTO.availableBikeInstanceList.add(bikeModelDTO);
            }
            if (availableBikeInstances < quantity) {
                bikeModelDTO.amount = quantity - availableBikeInstances;
                orderOrderDTO.produceBikeModelList.add(bikeModelDTO);
            }
        }
        return orderOrderDTO;

    }
}
