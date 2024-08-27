package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber.ftfactoryHBW;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"workpiece",
"location"
})
public class StockItem {

	@JsonProperty("workpiece")
	private Workpiece workpiece;
	@JsonProperty("location")
	private String location;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
	
	@JsonProperty("workpiece")
	public Workpiece getWorkpiece() {
		return workpiece;
	}
	
	@JsonProperty("workpiece")
	public void setWorkpiece(Workpiece workpiece) {
		this.workpiece = workpiece;
	}
	
	@JsonProperty("location")
	public String getLocation() {
		return location;
	}
	
	@JsonProperty("location")
	public void setLocation(String location) {
		this.location = location;
	}
	
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}
	
	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);	
	}
}