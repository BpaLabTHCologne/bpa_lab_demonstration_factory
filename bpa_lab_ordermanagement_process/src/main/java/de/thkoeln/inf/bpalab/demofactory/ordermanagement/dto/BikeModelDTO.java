package de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto;

public class BikeModelDTO {
    public String title;
    public Double weight;
    public String color;
    public Integer amount;

    public BikeModelDTO(String title, Double weight, String color, Integer amount) {
        this.title = title;
        this.weight = weight;
        this.color = color;
        this.amount = amount;
    }

    public BikeModelDTO() {
    }
}
