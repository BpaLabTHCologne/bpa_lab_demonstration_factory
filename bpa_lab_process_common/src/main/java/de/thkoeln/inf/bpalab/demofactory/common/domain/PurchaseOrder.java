package de.thkoeln.inf.bpalab.demofactory.common.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Proxy;


@Entity
@Proxy(lazy = false)
public class PurchaseOrder {

    @Id
    @Column(nullable = false, updatable = false)
    private String purchaseOrderNumber;

    @Column
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_component_id", nullable = false)
    private BikeComponent bikeComponent;

    private String productionOrderNumber;

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchasingOrderNumber) {
        this.purchaseOrderNumber = purchasingOrderNumber;
    }

    public BikeComponent getBikeComponent() {
        return bikeComponent;
    }

    public void setBikeComponent(BikeComponent bikeComponent) {
        this.bikeComponent = bikeComponent;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductionOrderNumber() {
        return productionOrderNumber;
    }

    public void setProductionOrderNumber(String productionOrderNumber) {
        this.productionOrderNumber = productionOrderNumber;
    }
}
