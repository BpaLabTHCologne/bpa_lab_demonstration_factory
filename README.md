# BPA Lab Bicycle Manufacturing Factory

This repository contains the source code and configuration files for automating a small bicycle factory model using Camunda 8, a powerful workflow automation platform. The automation system orchestrates various manufacturing processes involved in the production of bicycles, streamlining the entire production lifecycle from order management to production control and purchasing to shipment. Most of the job workers are implemented using official zeebe-node repository [Zeebe Node](https://github.com/camunda-community-hub/zeebe-client-node-js) provided by the Camunda Platform. Moreover, the robots for the warehouse operations like storing or retrieving items are implemented in Python.

## Prerequisites

-> Docker desktop application based on your system preference (Windows/macOS)

## Using docker compose

> :information_source: Docker 20.10.16+ is required.

> :information_source: This project uses the basic components of Camunda Platform 8. For more information, follow the official Camunda Platform link: [Camunda Platform 8](https://github.com/camunda/camunda-platform)

1. Clone this repository to a directory of your choice
2. The next steps depend on the environment you want to use ([Test](#test-environment-a-connection-to-the-physiscal-components-is-not-needed) or [Prod](#prod-environment-a-connection-to-the-physiscal-components-is-needed))

---

### Test environment (A connection to the physiscal components is not needed!)

3. Go into the .env-file of the project an set the environment variables IS_PROD and FACTORY_PROD to 'false'

4. Run the following command from the directory of the project to pull, create and run all the containers:

```
docker compose -f docker-compose-core.yaml --profile==TestSetup up -d
```

5. Run the following command only to shut down the containers gracefully

```
docker compose -f docker-compose-core.yaml --profile==TestSetup down
```

### Prod environment (A connection to the physiscal components is needed!)

3. Go into the .env-file of the project an set the environment variables IS_PROD and FACTORY_PROD to 'true'

4. Run the following command from the directory of the project to pull, create and run all the containers:

```
docker compose -f docker-compose-core.yaml --profile==ProdSetup up -d
```

5. Run the following command only to shut down the containers gracefully

```
docker compose -f docker-compose-core.yaml --profile==ProdSetup down
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

### Start the process

The process can be started by placing an order from the front-end single page application: [http://localhost:5173](http://localhost:5173)

### Order management process overview

![process image](https://github.com/BpaLabTHCologne/bpa_lab_demonstration_factory/blob/main/docs/OrderManagementProcess.png?raw=true)


### Docker overview

![docker_overview](https://github.com/BpaLabTHCologne/bpa_lab_demonstration_factory/blob/main/docs/docker-overview.png?raw=true)

### UML Deployment diagram

![deployment](https://github.com/BpaLabTHCologne/bpa_lab_demonstration_factory/blob/main/docs/BPALABDeploymentDiagram.png?raw=true")
