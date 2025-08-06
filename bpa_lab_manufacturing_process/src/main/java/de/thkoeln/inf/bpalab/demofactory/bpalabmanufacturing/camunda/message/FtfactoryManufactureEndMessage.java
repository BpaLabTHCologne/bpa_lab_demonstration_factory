package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.message;

import org.springframework.stereotype.Component;

@Component
public class FtfactoryManufactureEndMessage implements IReplyMessage {

	private String messageName = "MsgManufactureFinished";
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
