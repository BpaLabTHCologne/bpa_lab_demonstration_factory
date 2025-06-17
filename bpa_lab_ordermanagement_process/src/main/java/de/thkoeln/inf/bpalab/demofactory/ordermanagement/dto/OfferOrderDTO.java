package de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto;

import de.thkoeln.inf.bpalab.demofactory.common.dto.BikeModelDTO;

import java.util.ArrayList;

public class OfferOrderDTO {
    public ArrayList<BikeModelDTO> offerBikeModelList;
    public CustomerOrderCustomerDTO orderCustomer;
    public String orderNumber;
    public String orderDate;
}

