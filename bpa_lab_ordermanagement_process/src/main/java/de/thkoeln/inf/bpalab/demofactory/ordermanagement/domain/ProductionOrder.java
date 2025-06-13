package de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Proxy;


@Entity
@Proxy(lazy = false)
public class ProductionOrder {

    @Id
    @Column(nullable = false, updatable = false)
    private String productionOrderNumber;

    @Column
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_model_id", nullable = false)
    private BikeModel bikeModel;

    private String customerOrderNumber;

    public String getCustomerOrderNumber() {
        return customerOrderNumber;
    }

    public void setCustomerOrderNumber(String customerOrderNumber) {
        this.customerOrderNumber = customerOrderNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    public BikeModel getBikeModel() {
        return bikeModel;
    }

    public void setBikeModel(final BikeModel bikeModel) {
        this.bikeModel = bikeModel;
    }

    public String getProductionOrderNumber() {
        return productionOrderNumber;
    }

    public void setProductionOrderNumber(String productionOrderNumber) {
        this.productionOrderNumber = productionOrderNumber;
    }
}
