package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.rest;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.subscriber.FtfactoryHBW;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/")
public class FtfactoryRestController {

  private static final Logger LOG = LoggerFactory.getLogger(FtfactoryRestController.class);

  @Autowired
  private ZeebeClient zeebe;

  @Autowired
  FtfactoryHBW ftfactoryHBW;
	
  private final static Random RANDOMIZER = new Random();
  
  private long nextLong(long lowerRange, long upperRange) {
	  return (long) (RANDOMIZER.nextDouble() * (upperRange - lowerRange)) + lowerRange;
  }


  @PostMapping("/start")
  public void startProcessInstance(@RequestBody Map<String, Object> variables) {

    LOG.info(
        "Starting process with variables: " + variables);

//    zeebe
//        .newCreateInstanceCommand()
//        .bpmnProcessId(ProcessConstants.BPMN_PROCESS_ID)
//        .latestVersion()
//        .variables(variables)
//        .send();
	String correlationValue = "correlationValue" + String.valueOf(nextLong(1, 101));

	variables.put("correlationValue", correlationValue);

    zeebe.newPublishMessageCommand()
		.messageName("manufacturingStartMessage")
		.correlationKey(correlationValue)
		.variables(variables)
		.send();
  }
  
  @GetMapping("/stock")
  public String getStock() {
	  
	  return this.ftfactoryHBW.countAsJSON();
  }
}
