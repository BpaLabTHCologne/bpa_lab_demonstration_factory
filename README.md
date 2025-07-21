# BPA Lab Bicycle Manufacturing Factory

The Business Process Automation Lab (BPA Lab) at the TH Cologne is a small and modular model factory focusing on business process automation and analytics. One of its goals is to demonstrate modern concepts and technologies for the automation and analysis of business processes to different stakeholders (companies, students, ...).

This repository contains the source code and configuration files of the implementation of the demonstration scenario: the ordering, production, purchasing, manufacturing and shipping of custom-made bicycles. The implementation is based on Camunda 8, a Business Process Management System. This BPMS orchestrates different job workers for different processes. Moreover, the robots for the warehouse operations like storing or retrieving items are implemented in Python.

## state of affairs

## Prerequisites

-> Docker desktop application based on your system preference (Windows/macOS)
or Docker nativ (Linux)

-> mysql9 

### Database scheme

![](sql/bpa_lab_demostration_factory_db.png)



#### Run
    docker compose -f docker-compose.yaml
to start Mysql DBMS and Camunda self-managed

### BPALabBikeFactoryOrderManagement
- jdk21 spring-boot-starter-camunda-sdk(c8) mysql(9) gradle
- Application creates/updates database with hibernate/jpa on startup except
  Vendor table and Vendor-BikeComponent table

![](bpa_lab_ordermanagement_process/bpmn/BPALabBikeFactoryOrderManagement.png)
#### Run
in ./bpa_lab_ordermanagement_process

    gradle bootRun

### BPALabBikeFactoryProduction
- jdk21 spring-boot-starter-camunda-sdk(c8) mysql(9) gradle
![](/bpa_lab_productioncontrol_process/bpmn/BPALabBikeFactoryProductionControl.png)
#### Run

in ./bpa_lab_productioncontrol_process

    gradle bootRun

### BPALabBikeFactoryPurchase
- nodejs(v23.10.0) typescript mysql(9) @camunda8/sdk
- Vendor table and Vendor-BikeComponent table must be created

![](/bpa_lab_purchasing_process/bpmn/bpa_lab_purchase_process.png)
#### Run
in ./bpa_lab_purchasing_process

    npm start

### Containerisation in preparation
- docker compose, dockerfiles, .env


-> More to be added