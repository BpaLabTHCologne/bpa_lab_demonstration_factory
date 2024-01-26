# BPA Lab: Warehouse operations process application

## Description 
This repository contains the code of a process application that records the inventory of a high-bay warehouse and enables the storage and retrieval of items in a physical high-bay warehouse by a warehouse robot. 
The process application is available as a Docker container and consists of the following services:
* process-app: control of the process flow, checking of stock levels and recording of changes to stock levels
* robot-app: Physical control of the warehouse robot
* mysql-db: Database for the permanent storage of stock levels
* mqtt-broker: Message broker for communication between the process-app and robot-app service

## Associated BPMN process model
![warehouse-operations-process (1)](https://github.com/BpaLabTHCologne/bpa_lab_warehouse_operations_docker_version/assets/134142150/a983ea04-e4a5-4329-bebd-c020b131111a)


## Entry point for concepts and important components of Camunda 8 and Zeebe
* Documentation for the use and understanding of service tasks:
  * https://docs.camunda.io/docs/components/modeler/bpmn/service-tasks/
* Documentation to understand the job worker concept:
  * https://docs.camunda.io/docs/components/concepts/job-workers/

# Getting started

## Cloning/Download of the repository
Clone or download this repository to your local machine. 
* Here is a guide from Github on how to clone a repository: https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository.
* You also need to install Git to be able to clone: https://git-scm.com/downloads 

## Installation of Docker Desktop
To install Docker Desktop, follow this link: https://docs.docker.com/get-docker/ and select the version for your operating system. Then follow the installation instructions. **Please note that your PC must be restarted once during or after the installation.**

## Needed configurations
Please first open the folder in which you have cloned or saved the repository with a code editor of your choice, such as Visual Studio Code.

### Configuration of environment variables in own .env file.
The file **.env_example.txt** in the repository serves as an example for an .env-file. 

1. First you should create an .env-file (name of the file: ".env")
2. Copy the text from the **.env_example.txt** file into the newly created **.env** file
3. The first environment value **IS_CLOUD** must be **'true'** or **'false'** depending on whether you are using Camunda 8 Cloud or Self-managed
4. The second environment value **IS_PROD** must again be **'true'** or **'false'**
   * Set the value to **false** if you want to run/test the process application without having access to the physical robot
   * Set the value to **true** if you want to run the process application with the active movements of the warehouse robot **(only when you have access to the physical warehouse robot and the correct setup)**

## Execution of the process application
**Before execution, it is important that all installations and configurations have been done completely and successfully!**

1. Start the prompt and direct to the path of the folder in which you saved/cloned the repository
2. Then start the docker container by using the following command:
   ```bash
   docker compose up --build
    ```
3. Check in **Docker Desktop App** under the **"Container "** tab whether all services are in **"Running "** mode, see the following image:

![image](https://github.com/DomenicGonzalez/bpa_lab_warehouse_operations_docker_version/assets/134142150/09986dcd-6966-401c-9ad4-f104d47ff440)

4. Run an instance of the warehouse operations process defined by **warehouse-operations-process.bpmn** in Camunda:
   * **When you click on "run" the process, the following variables must be passed in JSON format!:**
     
  | Variable name  | Description | Possible values |
  | ------------- | ------------- | ------------- |
  | item | The name of the object for which the action will be executed | Can be any string value |
  | place_id  | The place in a shelf for which the action should be executed  | Must be a numeric value between 1 and 6 |
  | shelf_id  | The shelf for which the action is to be executed | Always set the value to 1 |
  | task  | The action that should be executed  | Must be either "store" or "retrieve" |
  | transactionId  | An unique number needed for the receiving tasks  | always set the value to 0 |


* Example: Storing a bicycle on the third place:
  ```bash
  {"item":"Bicycle","place_id":3,"shelf_id":1,"task":"store","transactionId":0}
  ```
  ![image](https://github.com/DomenicGonzalez/bpa_lab_warehouse_operations_docker_version/assets/134142150/e746d89d-b290-4e6a-ab5b-a2a1812d6893)
