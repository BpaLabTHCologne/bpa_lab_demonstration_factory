package de.thkoeln.inf.bpalab.demofactory.ordermanagement;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class OrdermanagementApplication implements CommandLineRunner {

    private final static Logger LOG = LoggerFactory.getLogger(OrdermanagementApplication.class);

    public static void main(final String[] args) {
        SpringApplication.run(OrdermanagementApplication.class, args);
    }
    @Autowired
    private ZeebeClient zeebeClient;

    @Override
    public void run(String... args) throws Exception {
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("bpmn/BPALabBikeFactoryOrderManagement.bpmn")
                .send().join();
        LOG.info("Deployed bpmn/BPALabBikeFactoryOrderManagement.bpmn");
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("bpmn/ChooseBikesForm.form")
                .send().join();
        LOG.info("Deployed bpmn/ChooseBikesForm.form");
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("bpmn/ShowOrderForm.form")
                .send().join();
        LOG.info("Deployed bpmn/ShowOrderForm.form");
    }

}
