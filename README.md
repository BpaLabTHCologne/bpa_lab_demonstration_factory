# BPA Lab Bicycle Manufacturing Factory

The Business Process Automation Lab (BPA Lab) at the TH Cologne is a small and modular model factory focusing on business process automation and analytics. One of its goals is to demonstrate modern concepts and technologies for the automation and analysis of business processes to different stakeholders (enterprises, students, ...).

A general indtroduction is available here: https://github.com/BpaLabTHCologne/bpa_lab_docs

This repository contains the source code and configuration files of the implementation of the end to end demonstration scenario: the ordering, production, purchasing, manufacturing and shipping of custom-made bicycles. The implementation is based on Camunda 8, a Business Process Management System. This BPMS orchestrates different process-specific job workers. Moreover, control programs in Python are steering physical Fischertechnik robots for manufacturing and warehouse operations.

## Prerequisites

Install docker, e.g. docker desktop application based on your system preference (Windows/macOS) or Docker nativ (Linux) is required 

## Using docker compose to deploy and start entire solution 

:information_source: Docker 20.10.16+ is required.

:information_source: This project uses the basic components of Camunda Platform 8. For more information, follow the official Camunda Platform link: [Camunda Platform 8](https://github.com/camunda/camunda-platform)

1. Clone this repository to a directory of your choice
2. Run docker service (e.g Docker Desktop application)
3. Run the following command from the directory of the project to pull, create and run all the containers (and images if not existing yet):
  
   "docker compose -f docker-compose-processes.yml up -d"

  This starts: 
- Container with Mysql DBMS running [./sql/initdb.sql](./sql/initdb.sql) to create tables and data (if not existing yet) 
- Camunda self-managed (with components like tasklist, operate, zeebe, ...)
- multiple process applications 

    `BPALabBikeFactoryOrderManagement`, 

    `BPALabBikeFactoryProduction`,

    `BPALabBikeFactoryPurchase`,

    `BPALabBikeFactoryManufacture`,

    `BPALabBikeFactoryShipment`

in docker container.

Remark: In case you need to deloy a new release of the BPA Lab solution, it may be required to remove images (at least of process application) before executing docker compose -up e.g. via Docker Desktop application. 

4. (open issue) Check container status (e.g. in Docker Desktop Application). In case not all containers are started sucessfully, please restart these containers after some time. Remark: In case you have limited RAM/CPU, the start of components/container (e.g. Camunda task list) may require few minutes. 

5. (only for initial setup) To use the data architecture / dashboards: please follow the guideline "Necessary configurations for the use of the data architecture during initial installation or reinstallation" in the Wiki    

6. Run solution (refer to "User Guide for End to End Process Execution" in wiki). The application can be accesses via URL, which are visible in Docker Desktop.
   
7. Run the following command only to shut down the containers gracefully: 
   "docker compose -f docker-compose-processes.yml down" to 

### Recommended tools for developers

1. Database client
Install extension of your software development environment (or any other client) to access the data tables in the MYSQL database. This contains e.g. product information, stock, etc.   

Create connection

  `Server Address: localhost`

  `Port: 3070`

  `Database: bpa_lab_demonstration_factory`

  `Username: root`

  `Password: •••••••••`


3. MQTT client
[only when working with the physical BPA Lab Factory] Install a MQTT Client toolbox like https://mqttx.app/ to be able to test the connection to the MQTT broker (running on the rasberry pi), receive messages or simulate messages.

a) Create connection and connect:

  'Name: [e.g. BPALabBroker]' 
  
  'Host: mqtt://10.0.0.21'
  
  'Port: 1883'
  
  'Client ID: [a unique ID]'

b) Create subscription with topic "#" (all messages) or more specific subscriptions

### Database scheme
The data schema of the mysql database is shown here:

![](sql/bpa_lab_demostration_factory_db.png)


## Run process applications separately from the Camunda/MySQL containers
In general, the deployment via docker (described below) is fastest deployment method, but implies certain hardware requirement (preferrable 16 GB RAM). To run the solution in other environments, only Camunda and MySQL can be deployed on docker, while single process applications may be run separatly as described below.

#### Run
    docker compose -f docker-compose.yaml up -d
to start Mysql DBMS with [./sql/initdb.sql](./sql/initdb.sql) and Camunda self-managed without process applications

### BPALabBikeFactoryOrderManagement
    jdk21 spring-boot-starter-camunda-sdk(c8) mysql(9) gradle
- shows available ***bike models***, creates ***Customer Order (Order Number)***
- triggers **Production** and **Shipment**
- application creates/updates database with hibernate/jpa on startup except
  PurchaseOrder table, Vendor table and Vendor-BikeComponent table
- Deploys bpmn/BPALabBikeFactoryOrderManagement.bpmn, bpmn/ChooseBikesForm.form, bpmn/ShowOrderForm.form

![](bpa_lab_ordermanagement_process/bpmn/BPALabBikeFactoryOrderManagement.png)
#### Run
in ./bpa_lab_ordermanagement_process
    
    gradle bootRun

### BPALabBikeFactoryProduction
    jdk21 spring-boot-starter-camunda-sdk(c8) mysql(9) gradle
- called per message, creates ***Production Order*** and ***Bike Instances*** from ***Bike Model***,
  decreases ***Bike Component*** quantity, reserves them for ***Order Number***
- started with User Task, creates ***Production Order*** and ***Bike Instances*** from ***Bike Model***,
  decreases ***Bike Component*** quantity, no reservation
- triggers **Purchase** and **Manufacture**
- Deploys bpmn/BPALabBikeFactoryProductionControl.bpmn, bpmn/bpa_lab_production_process_start.form

![](/bpa_lab_productioncontrol_process/bpmn/BPALabBikeFactoryProductionControl.png)
#### Run

in ./bpa_lab_productioncontrol_process

    gradle bootRun

### BPALabBikeFactoryPurchase
    nodejs(v23.10.0) typescript mysql(9) @camunda8/sdk
- called per message, creates ***Purchase Order*** and increases ***Bike Component*** quantity
- started with User Task, creates ***Purchase Order*** and increases ***Bike Component*** quantity
- Deploys bpmn/bpa_lab_purchase_process.bpmn, bpmn/bpa_lab_purchase_process_start.form, bpmn/chooseVendor.form

![](/bpa_lab_purchasing_process/bpmn/bpa_lab_purchase_process.png)
#### Run
in ./bpa_lab_purchasing_process

    npm run start

### BPALabBikeFactoryManufacture
    jdk21 spring-boot-starter-camunda-sdk(c8) mysql(9) gradle
- sends mqtt messages(***order***) to and receives mqtt messages(environment, order status, stock) from Fischertechnik factory
- fakes **manufacturing** if no connection to mqtt broker available
- Deploys bpmn/BPALabBikeFactoryManufacture.bpmn, bpmn/BPALabBikeFactoryManufactureOrder.form

![](/bpa_lab_manufacturing_process/bpmn/BPALabBikeFactoryManufacture.png)
#### Run

in ./bpa_lab_manufacturing_process

    gradle bootRun

### BPALabBikeFactoryShipment
    nodejs(v23.10.0) typescript mysql(9) @camunda8/sdk

- called per message, sets ***Bike Instances*** with ***Order Number*** to shipped
- started with User Task, searches ***Bike Instances*** **without** ***Order Number*** and not shipped, sets chosen to shipped
- Deploys bpmn/bpa_lab_shipment-process.bpmn, bpmn/shipmentInputData.form, bpmn/checkInformation.form

![](/bpa_lab_shipment_process/bpmn/bpa_lab_shipment_process.png)
#### Run
in ./bpa_lab_shipment_process

    npm run start


### BPALabBikeFactoryWarehouse
    jdk21 spring-boot-starter-camunda-sdk(c8) gradle
- sends mqtt messages(***put/get***) to and receives mqtt messages(info,fetched,putted) from TXT_Warehouse
- Deploys bpmn/BPALabBikeFactoryWarehouseFetch.bpmn, ./bpmn/BPALabBikeFactoryWarehouseFetchForm.form

![](/bpa_lab_warehouse_process/bpmn/BPALabBikeFactoryWarehouseFetch.png)

- Deploys bpmn/BPALabBikeFactoryWarehousePut.bpmn, ./bpmn/BPALabBikeFactoryWarehousePutForm.form

![](/bpa_lab_warehouse_process/bpmn/BPALabBikeFactoryWarehousePut.png)

#### Run

in ./bpa_lab_warehouse_process

    gradle bootRun
