package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse.mqtt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WarehouseTopics {
			
// Topic for fetching bike instance by color
	private String fetchTopic;

    public String getFetchTopic() {return fetchTopic;}
    @Value("${ftwarehousemqttclient.fetchTopic:bpalab/ftwarehouse/fetch}")
	public void setFetchTopic(String fetchTopic) {
		this.fetchTopic = fetchTopic;
	}

// Topic for fetching bike instance by id
    private String getTopic;
    public String getGetTopic() {return getTopic;}
    @Value("${ftwarehousemqttclient.getTopic:bpalab/ftwarehouse/get}")
    public void setGetTopic(String getTopic) {
        this.getTopic = getTopic;
    }

// Topic for fetch/get no Bike
    private String noBikeTopic;
    public String getNoBikeTopic() {return noBikeTopic;}
    @Value(("${ftwarehousemqttclient.noBikeTopic:bpalab/ftwarehouse/nobike}"))
    public void setNoBikeTopic(String noBikeTopic) {
        this.noBikeTopic = noBikeTopic;
    }

// Topic for putting bike instance
	private String putTopic;
	public String getPutTopic() {
		return putTopic;
	}
	@Value("${ftwarehousemqttclient.putTopic:bpalab/ftwarehouse/put}")
	public void setPutTopic(String putTopic) {
		this.putTopic = putTopic;
	}

// Topic for put no Place
    private String noPlaceTopic;
    public String getNoPlaceTopic() {return noPlaceTopic;}
    @Value(("${ftwarehousemqttclient.noPlaceTopic:bpalab/ftwarehouse/noplace}"))
    public void setNoPlaceTopic(String noPlaceTopic) {
        this.noPlaceTopic = noPlaceTopic;
    }

// Topic for storage info
	private String storageTopic;
	public String getStorageTopic() {
		return storageTopic;
	}
	@Value("${ftwarehousemqttclient.storageTopic:bpalab/ftwarehouse/storage}")
	public void setStorageTopic(String storageTopic) { this.storageTopic = storageTopic; }

//    Topic for storage info topic
    private String infoTopic;
    public String getInfoTopic() {return infoTopic;}
    @Value("${ftwarehousemqttclient.infoTopic:bpalab/ftwarehouse/info}")
    public void setInfoTopic(String infoTopic) { this.infoTopic = infoTopic; }

//    Topic for fetched bike topic
    private String fetchedBikeTopic;
    public String getFetchedBikeTopic() {return fetchedBikeTopic;}
    @Value("${ftwarehousemqttclient.fetchedBikeTopic:bpalab/ftwarehouse/fetchedBike}")
    public void setFetchedBikeTopic(String fetchedBikeTopic) { this.fetchedBikeTopic = fetchedBikeTopic; }

// Topic for storedPlace Topic
    private String storedPlaceTopic;
    public String getStoredPlaceTopic() {return storedPlaceTopic;}
    @Value("${ftwarehousemqttclient.storedPlaceTopic:bpalab/ftwarehouse/storedPlace}")
    public void setStoredPlaceTopic(String storedPlaceTopic) {
        this.storedPlaceTopic = storedPlaceTopic;
    }
}

