package de.thkoeln.inf.bpalab.demofactory.ordermanagement;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class OrdermanagementApplication {

    public static void main(final String[] args) {
        SpringApplication.run(OrdermanagementApplication.class, args);
    }

}
