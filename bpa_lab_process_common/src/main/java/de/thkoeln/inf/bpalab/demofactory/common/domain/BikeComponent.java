package de.thkoeln.inf.bpalab.demofactory.common.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@Proxy(lazy=false)
public class BikeComponent {

    @Id
    @Column(nullable = false, updatable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bike_model_id", nullable = false)
    private BikeModel bikeModel;

    @Column
    private String color;

    @Column
    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public BikeModel getBikeModel() {
        return bikeModel;
    }

    public void setBikeModel(final BikeModel bikeModel) {
        this.bikeModel = bikeModel;
    }

}
