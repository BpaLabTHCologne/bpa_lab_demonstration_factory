package de.thkoeln.inf.bpalab.demofactory.productioncontrol;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ProductionControlApplication implements CommandLineRunner {

    private final static Logger LOG = LoggerFactory.getLogger(ProductionControlApplication.class);

    public static void main(final String[] args) {
        SpringApplication.run(ProductionControlApplication.class, args);
    }
    @Autowired
    private ZeebeClient zeebeClient;

    @Override
    public void run(String... args) throws Exception {
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("bpmn/BPALabBikeFactoryProductionControl.bpmn")
                .send().join();
        LOG.info("Deployed bpmn/BPALabBikeFactoryProductionControl.bpmn");
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("bpmn/bpa_lab_production_process_start.form")
                .send().join();
        LOG.info("Deployed bpmn/bpa_lab_production_process_start.form");
    }

}
