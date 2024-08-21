package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.publisher;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.mqtt.TopicPayload;

public interface IPublisher {

	TopicPayload getTopicPayload();

}