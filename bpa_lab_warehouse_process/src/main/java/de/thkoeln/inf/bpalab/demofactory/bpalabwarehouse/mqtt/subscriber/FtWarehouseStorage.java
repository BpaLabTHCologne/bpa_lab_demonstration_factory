package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.FtwarehouseMQTTClient;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.WarehouseTopics;
import de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt.subscriber.ftwarehousestorage.BikeInstance;
import jakarta.annotation.PostConstruct;
import org.checkerframework.checker.units.qual.C;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class FtWarehouseStorage implements IMqttMessageListener {
// 		Topic: baplab/ftwarehouse/info
//		PayLoad(JSON):
/*    {"1": {"id": "123222", "color": "red"},
       "2": {"id": "12322", "color": "BLUE"},
       "3": null,
       "4": null,
       "5": null,
       "6": null
       }
 */
    private static final Logger log = LoggerFactory.getLogger(BikeInstance.class);

    @Autowired
    WarehouseTopics warehouseTopics;

    @Autowired
    FtwarehouseMQTTClient ftWarehouseMQTTClient;

    @PostConstruct
    public void postConstruct() {
//		prepare Subscriber
        log.info("PostConstruct {}", getSubscriptionTopic());
        ftWarehouseMQTTClient.subscribe(getSubscriptionTopic(), this);
    }

    @JsonProperty("Storage")
    private Map<String, BikeInstance> bikeInstance = new LinkedHashMap<>();

    @JsonProperty("Storage")
    public Map<String, BikeInstance> getBikeInstance() {
        return bikeInstance;
    }

    @JsonProperty("Storage")
    public void setBikeInstance(Map<String, BikeInstance> bikeInstance) {
        this.bikeInstance = bikeInstance;
    }

    private static final Logger LOG = LoggerFactory.getLogger(FtWarehouseStorage.class);

    static public FtWarehouseStorage fromJSON(String json) {
        ObjectMapper mapper = new ObjectMapper();
        FtWarehouseStorage ftWarehouseStorage = new FtWarehouseStorage();
        try {
            ftWarehouseStorage = mapper.readValue(json, FtWarehouseStorage.class);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            return null;
        }
        return ftWarehouseStorage;
    }

    @JsonIgnore
    public boolean isEmptyPlace() {
        if (bikeInstance != null && !bikeInstance.isEmpty()) {
            for (Map.Entry<String, BikeInstance> entry : bikeInstance.entrySet()) {
                if (entry.getValue() == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasColor(String color) {
        if (bikeInstance != null && !bikeInstance.isEmpty()) {
            for (Map.Entry<String, BikeInstance> entry : bikeInstance.entrySet()) {
                LOG.info("Checking place " + entry.getKey());
                if (entry.getValue() != null) {
                    LOG.info("Checking color " + entry.getValue().getColor());
                    if (entry.getValue().getColor().equals(color)) {
                        LOG.info("hasColor true");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasId(String id) {
        if (bikeInstance != null && !bikeInstance.isEmpty()) {
            for (Map.Entry<String, BikeInstance> entry : bikeInstance.entrySet()) {
                LOG.info("Checking place " + entry.getKey());
                if (entry.getValue() != null) {
                    LOG.info("Checking id " + entry.getValue().getId());
                    if (entry.getValue().getId().equals(id)) {
                        LOG.info("hasId true");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @JsonIgnore
    public String getSubscriptionTopic() {
        return warehouseTopics.getInfoTopic();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        ObjectMapper om = new ObjectMapper();
        FtWarehouseStorage fS = null;
        try {
            fS = om.readValue(new String(mqttMessage.getPayload()), FtWarehouseStorage.class);
            this.copyFtStorage(fS);
            log.info("FtWarehouse.messageArrived with topic:{} message {} ", topic, new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));
        } catch (JsonMappingException e) {
            System.out.println("JsonMappingException " + e.getMessage());
        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException " + e.getMessage());
        }
    }

    private void copyFtStorage(FtWarehouseStorage fS) {
        this.bikeInstance = fS.bikeInstance;
    }
}
