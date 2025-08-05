package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing;

import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.TopicPayload;
import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BpaLabManufacturingApplication implements CommandLineRunner {
    private final static Logger LOG = LoggerFactory.getLogger(BpaLabManufacturingApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BpaLabManufacturingApplication.class, args);
    }

    @Autowired
    private ZeebeClient zeebeClient;

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Starting BPA Lab Manufacturing Application");
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("./bpmn/BPALabBikeFactoryManufacture.bpmn")
                .send().join();
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("./bpmn/BPALabBikeFactoryManufactureOrder.form")
                .send().join();
    }
}
