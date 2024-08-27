package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.sender;

import org.springframework.stereotype.Component;

@Component
public class FtfactoryHBWStartMessage implements IReplyMessage {

	private String messageName = "HBWStartMessage";
	private String correlationValue = null;

	public String getReplyMessageCorrelationValue() {
		return correlationValue;
	}
	public String getReplyMessageName() {
		return messageName;
	}
	
	public void setReplyMessageCorrelationValue(String correlationValue) {
		this.correlationValue = correlationValue;
	}	
}
