package de.thkoeln.inf.bpalab.demofactory.bpalabwarehouse;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BpaLabWarehouseApplication implements CommandLineRunner {
    private final static Logger LOG = LoggerFactory.getLogger(BpaLabWarehouseApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BpaLabWarehouseApplication.class, args);
    }

    @Autowired
    private ZeebeClient zeebeClient;

    @Override
    public void run(String... args) throws Exception {
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("./bpmn/BPALabBikeFactoryWarehouseFetch.bpmn")
                .send().join();
        LOG.info("deployed bpmn/BPALabBikeFactoryWarehouseFetch.bpmn");
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("./bpmn/StartWarehouseFetchForm.form")
                .send().join();
        LOG.info("deployed bpmn/StartWarehouseFetchForm.form");
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("./bpmn/BPALabBikeFactoryWarehousePut.bpmn")
                .send().join();
        LOG.info("deployed bpmn/BPALabBikeFactoryWarehousePut.bpmn");
        zeebeClient.newDeployResourceCommand()
                .addResourceFile("./bpmn/StartWarehousePutForm.form")
                .send().join();
        LOG.info("deployed bpmn/StartWarehousePutForm.form");
    }
}
