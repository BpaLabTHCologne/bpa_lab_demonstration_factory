package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.sender;

public interface IReplyMessage {
	
	public String getReplyMessageCorrelationValue();

	public void setReplyMessageCorrelationValue(String correlationValue);
	
}
