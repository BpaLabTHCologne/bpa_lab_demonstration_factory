# BPA Lab Bicycle Manufacturing Factory

The Business Process Automation Lab (BPA Lab) at the TH Cologne is a small and modular model factory focusing on business process automation and analytics. One of its goals is to demonstrate modern concepts and technologies for the automation and analysis of business processes to different stakeholders (companies, students, ...).

This repository contains the source code and configuration files of the implementation of the demonstration scenario: the ordering, production, purchasing, manufacturing and shipping of custom-made bicycles. The implementation is based on Camunda 8, a Business Process Management System. This BPMS orchestrates different job workers for different processes. Moreover, the robots for the warehouse operations like storing or retrieving items are implemented in Python.

## state of affairs

## Prerequisites

-> Docker desktop application based on your system preference (Windows/macOS)
or Docker nativ (Linux)

#### Run
    docker compose -f docker-compose.yaml
to start Mysql DBMS and Camunda self-managed

### BPALabBikeFactoryOrderManagement
- java spring-boot-starter-camunda-sdk(c8) mysql(9) gradle
![](bpa_lab_ordermanagement_process/bpmn/BPALabBikeFactoryOrderManagement.png)
#### Run
    gradle bootRun

### BPALabBikeFactoryProduction
- java spring-boot-starter-camunda-sdk(c8) mysql(9) gradle
![](/bpa_lab_productioncontrol_process/bpmn/BPALabBikeFactoryProductionControl.png)
#### Run
    gradle bootRun

### BPALabBikeFactoryPurchase
- nodejs mysql @camunda8/sdk
![](/bpa_lab_purchasing_process/bpmn/bpa_lab_purchase_process.png)
#### Run
    npm start

### Containerisation in preparation
- docker compose, dockerfiles, .env


-> More to be added