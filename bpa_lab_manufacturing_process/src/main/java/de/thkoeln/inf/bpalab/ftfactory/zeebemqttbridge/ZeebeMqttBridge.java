package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;

@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
public class ZeebeMqttBridge {

	@Autowired
	private FtfactoryMQTTClient ftfactoryMQTTClient;
	
	@Autowired
 	private ZeebeClientLifecycle ftfactoryZEEBEClient;

	@Autowired
	private FtfactoryZeebeWorker zeebeWorker;

	public static void main(String[] args) {
		SpringApplication.run(ZeebeMqttBridge.class, args);
	}

}
