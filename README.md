# BPA Lab Bicycle Manufacturing Factory

The Business Process Automation Lab (BPA Lab) at the TH Cologne is a small and modular model factory focusing on business process automation and analytics. One of its goals is to demonstrate modern concepts and technologies for the automation and analysis of business processes to different stakeholders (companies, students, ...).

This repository contains the source code and configuration files of the implementation of the demonstration scenario: the ordering, production, purchasing, manufacturing and shipping of custom-made bicycles. The implementation is based on Camunda 8, a Business Process Management System. This BPMS orchestrates different job workers for different processes. Moreover, the robots for the warehouse operations like storing or retrieving items are implemented in Python.

## Prerequisites

-> Docker desktop application based on your system preference (Windows/macOS)
or Docker nativ (Linux) 

## Using docker compose to deploy entire solution 

:information_source: Docker 20.10.16+ is required.

:information_source: This project uses the basic components of Camunda Platform 8. For more information, follow the official Camunda Platform link: [Camunda Platform 8](https://github.com/camunda/camunda-platform)

1. Clone this repository to a directory of your choice
2. Run docker service
3. Run the following command from the directory of the project to pull, create and run all the containers:
  
   "docker compose -f docker-compose-processes.yml up -d"

  This starts 
- Mysql DBMS with [./sql/initdb.sql](./sql/initdb.sql) creating an tables and data (if not existing yet) 
- Camunda self-managed (several components like tasklist, operate, zeebe, ...)
- and several process applications 

    `BPALabBikeFactoryOrderManagement`, 

    `BPALabBikeFactoryProduction`,

    `BPALabBikeFactoryPurchase`,

    `BPALabBikeFactoryManufacture`,

    `BPALabBikeFactoryShipment`

as Docker Container

5. Run the following command only to shut down the containers gracefully: 
   "docker compose -f docker-compose-processes.yml down" to 

### Database scheme
The data schema of the mysql database is:

![](sql/bpa_lab_demostration_factory_db.png)


## Using docker compose to deploy and run components (to be reworked)
As an alternative, components of the solution can be delpoyed and run separately.

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
- triggers **Purchase** and **Manufacturr**
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
