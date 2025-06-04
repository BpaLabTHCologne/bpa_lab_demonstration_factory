package de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
public class BikeInstance {

//    @Id
//    @Column(nullable = false, updatable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Id
//    @Column(
//            nullable = false,
//            updatable = false,
//            columnDefinition = "UUID"
//    )

    @GeneratedValue
    @UuidGenerator
    private UUID serialNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bike_model_id", nullable = false)
    private BikeModel bikeModel;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "bike_stock_id")
//    private BikeStock bikeStock;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private CustomerOrder customerOrder;

    @Column
    private Boolean shipped;

    public CustomerOrder getCustomerOrder() {return customerOrder;}

    public void setCustomerOrder(CustomerOrder order) {this.customerOrder = order;}

    public Boolean getShipped() {return shipped;}

    public void setShipped(Boolean shipped) {this.shipped = shipped;}

    public Boolean getReserved() {return customerOrder != null;}

    public UUID getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(final UUID serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BikeModel getBikeModel() {
        return bikeModel;
    }

    public void setBikeModel(final BikeModel bikeModel) {
        this.bikeModel = bikeModel;
    }

//    public BikeStock getBikeStock() {
//        return bikeStock;
//    }
//
//    public void setBikeStock(final BikeStock bikeStock) {
//        this.bikeStock = bikeStock;
//    }

}
