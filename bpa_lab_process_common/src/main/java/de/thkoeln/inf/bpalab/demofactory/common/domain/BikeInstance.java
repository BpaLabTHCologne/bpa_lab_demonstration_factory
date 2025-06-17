package de.thkoeln.inf.bpalab.demofactory.common.domain;

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

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID serialNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bike_model_id", nullable = false)
    private BikeModel bikeModel;

    private String customerOrderNumber;

    @Column
    private Boolean shipped;

    public String getCustomerOrderNumber() {return customerOrderNumber;}

    public void setCustomerOrder(String customerOrderNumber) {this.customerOrderNumber = customerOrderNumber;}

    public Boolean getShipped() {return shipped;}

    public void setShipped(Boolean shipped) {this.shipped = shipped;}

    public Boolean getReserved() {return customerOrderNumber != null;}

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

}
