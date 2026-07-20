package de.thkoeln.inf.bpalab.demofactory.ordermanagement;

import io.camunda.client.CamundaClient;
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
    private CamundaClient camundaClient;

    @Override
    public void run(String... args) throws Exception {
        camundaClient.newDeployResourceCommand()
                .addResourceFile("bpmn/BPALabBikeFactoryOrderManagement.bpmn")
                .send().join();
        LOG.info("Deployed bpmn/BPALabBikeFactoryOrderManagement.bpmn");
        camundaClient.newDeployResourceCommand()
                .addResourceFile("bpmn/ChooseBikesForm.form")
                .send().join();
        LOG.info("Deployed bpmn/ChooseBikesForm.form");
        camundaClient.newDeployResourceCommand()
                .addResourceFile("bpmn/ShowOrderForm.form")
                .send().join();
        LOG.info("Deployed bpmn/ShowOrderForm.form");
    }

}
