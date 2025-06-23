package de.thkoeln.inf.bpalab.demofactory.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Proxy;


@Entity
@Proxy(lazy=false)
public class BikeModel {

    @Id
    @Column(nullable = false, updatable = false)
    private String title;

    @Column
    private Double weight;

    @Column
    private String color;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(final Double weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

}
