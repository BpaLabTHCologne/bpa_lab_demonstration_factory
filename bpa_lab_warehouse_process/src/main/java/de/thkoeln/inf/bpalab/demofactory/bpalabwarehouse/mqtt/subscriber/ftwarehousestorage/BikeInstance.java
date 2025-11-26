package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.ftwarehousestorage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BikeInstance {
    private String id = null;
    private String color = null;

    public BikeInstance() {}
    public BikeInstance(String none) {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    static public BikeInstance fromJSON(String json) {
        ObjectMapper mapper = new ObjectMapper();
        BikeInstance bikeInstance = new BikeInstance();
        try {
            bikeInstance = mapper.readValue(json, BikeInstance.class);
        } catch (JsonProcessingException e) {
            return null;
        }
        return bikeInstance;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String asJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}