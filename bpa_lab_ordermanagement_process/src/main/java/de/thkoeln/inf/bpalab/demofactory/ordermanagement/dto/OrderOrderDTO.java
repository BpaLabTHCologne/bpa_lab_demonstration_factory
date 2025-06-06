package de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.CustomerOrder;

import java.util.ArrayList;

public class OrderOrderDTO {

    public OrderOrderDTO() {}

    public OrderOrderDTO(CustomerOrder customerOrder) {
        orderCustomer = new CustomerOrderCustomerDTO();
        orderCustomer.name = customerOrder.getCustomerName();
        orderCustomer.email = customerOrder.getCustomerEmail();
        orderCustomer.adress = customerOrder.getCustomerAdress();
        orderNumber = customerOrder.getCustomerOrderNumber();
        orderDate = customerOrder.getCreationDate();
    }

    public CustomerOrderCustomerDTO orderCustomer;

    public String orderNumber;

    public String orderDate;

    public ArrayList<BikeModelDTO> reserveBikeInstanceList;

    public ArrayList<BikeModelDTO> produceBikeModelList;

}

