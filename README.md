# BPA Lab Bicycle Manufacturing Factory

The Business Process Automation Lab (BPA Lab) at the TH Cologne is a small and modular model factory focusing on business process automation and analytics. One of its goals is to demonstrate modern concepts and technologies for the automation and analysis of business processes to different stakeholders (companies, students, ...).

This repository contains the source code and configuration files of the implementation of the demonstration scenario: the ordering, manufacturing and shipping of custom-made bicycles. The implementation is based on Camunda 8, a Business Process Management System. This BPMS orchestrates different job workers for different processes. Most of the process applications (job workers) are implemented using the official zeebe node repository. [Zeebe Node](https://github.com/camunda-community-hub/zeebe-client-node-js) provided by the Camunda Platform. Moreover, the robots for the warehouse operations like storing or retrieving items are implemented in Python.

## Prerequisites

-> Docker desktop application based on your system preference (Windows/macOS)

## Using docker compose

> :information_source: Docker 20.10.16+ is required.

> :information_source: This project uses the basic components of Camunda Platform 8. For more information, follow the official Camunda Platform link: [Camunda Platform 8](https://github.com/camunda/camunda-platform)

1. Clone this repository to a directory of your choice
2. The next steps depend on the environment you want to use ([Test](#test-environment-no-connection-to-the-physical-components-of-the-model-factory-needed) or [Productive](#productive-environment))

---

### Test environment (No connection to the physical components of the model factory needed!)

3. Go into the **.env** file of the project an set the environment variables IS_PROD and FACTORY_PROD to 'false'

4. Run the following command from the directory of the project to pull, create and run all the containers (docker service need to be running):

```
docker compose -f docker-compose-core.yaml --profile=TestSetup up -d
```

5. Run the following command only to shut down the containers gracefully

```
docker compose -f docker-compose-core.yaml --profile=TestSetup down
```

### Productive environment

> :information_source: Error-free use of the productive environment is currently only possible with the BPA-Lab computer

3. Go into the **.env** file of the project an set the environment variables IS_PROD and FACTORY_PROD to 'true'

4. Before running the productive environment, [further preparations](https://github.com/BpaLabTHCologne/bpa_lab_demonstration_factory/wiki/Use-of-the-productive-environment-of-the-model-factory#preparations) must be made to the physical environment of the model factory

5. Run the following command from the directory of the project to pull, create and run all the containers:

```
docker compose -f docker-compose-core.yaml --profile=ProdSetup up -d
```

6. Run the following command only to shut down the containers gracefully

```
docker compose -f docker-compose-core.yaml --profile=ProdSetup down
```

---

Wait a few minutes for the environment to start up and settle down. Monitor the logs inside the containers, to ensure the all the containers have started successfully.

Now you can navigate to the different web apps and log in with the user `demo` and password `demo`:
- Operate: [http://localhost:8081](http://localhost:8081)
- Tasklist: [http://localhost:8082](http://localhost:8082)

Navigate the BPA Lab containers:
- Front-end (Single Page Application): [http://localhost:5173](http://localhost:5173)
- phpMyAdmin: [http://localhost:8183](http://localhost:8183)

### Deploying BPMN diagrams

In addition to the local environment setup with docker compose, you can download the [Camunda Desktop Modeler](https://camunda.com/download/modeler/) to locally model BPMN diagrams for execution and directly deploy them to your local environment.

> :information_source: This project deployes all the process diagrams automatically including order management, production control, purchasing, shipment, warehouse operations and manufacturing.

### Before starting a process for the first time after installation!

If you want to use the possibilities of the model factory for process analysis and monitoring, you need to carry out a few one-off configurations. 
See wiki entry: https://github.com/BpaLabTHCologne/bpa_lab_demonstration_factory/wiki/Necessary-configurations-for-the-use-of-the-data-architecture-during-initial-installation-or-reinstallation

### Start the process

The process can be started by placing an order from the front-end single page application: [http://localhost:5173](http://localhost:5173)

If you want to go through an end-to-end process instance with a production process using instructions, you can look into this wiki entry: [User guide for end‐to‐end process execution](https://github.com/BpaLabTHCologne/bpa_lab_demonstration_factory/wiki/User-guide-for-end%E2%80%90to%E2%80%90end-process-execution)

### Order management process overview

![process image](https://github.com/BpaLabTHCologne/bpa_lab_demonstration_factory/blob/main/docs/OrderManagementProcess.png?raw=true)

### Docker overview

![docker_overview](https://github.com/BpaLabTHCologne/bpa_lab_demonstration_factory/blob/main/docs/docker-overview.png?raw=true)

### UML Deployment diagram

![deployment](https://github.com/BpaLabTHCologne/bpa_lab_demonstration_factory/blob/main/docs/BPALABDeploymentDiagram.png?raw=true")
