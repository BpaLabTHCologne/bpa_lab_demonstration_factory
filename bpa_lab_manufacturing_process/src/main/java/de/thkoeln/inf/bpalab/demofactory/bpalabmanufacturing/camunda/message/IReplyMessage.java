package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.camunda.message;

public interface IReplyMessage {
	
	public String getReplyMessageCorrelationValue();

	public void setReplyMessageCorrelationValue(String correlationValue);
	
}
