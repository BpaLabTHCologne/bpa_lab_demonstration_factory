# bpa_lab_warehouse_operations

## Important notice!
* The project has a test version (**warehouse_operations_process_app_test.py** and **highbay_warehouse_app_test.py**) which should be used for the process application if you are not able to interact with the warehouse robot physically 
* If you want to run the non-test/productive version of this project in the Camunda 8 Cloud u need to have both an connection to the **eduroam - Univesity Network** for internet access and you need a **local connection to the router in the BPALab** via lan cable

## Entry point for concepts and important components of Camunda 8 and Zeebe
* Documentation for the use and understanding of service tasks:
  * https://docs.camunda.io/docs/components/modeler/bpmn/service-tasks/
* Documentation to understand the job worker concept:
  * https://docs.camunda.io/docs/components/concepts/job-workers/

## Associated BPMN process model
![image](https://github.com/BpaLabTHCologne/bpa_lab_warehouse_operations/assets/134142150/15652fdd-3fdb-4fe9-9b19-9d1be8ed87c4)

## Required installations

### Python
1. First download the installer of [Python 3.11.x](https://www.python.org/downloads/release/python-3114/) for your operating system
2. When you go through the steps of the installer, make sure that Python is added to the PATH environment variable
3. When the installation is complete, use the console command `python --version` to check if Python is installed
4. Also check via the command `pip --version` if pip is installed. This is needed in the following for the installation of the libraries

### MySQL Server and Workbench
1. Download the [MySQL Community Server Version 8](https://dev.mysql.com/downloads/windows/installer/8.0.html) and follow the steps of the installer
2. Download the [MySQL Workbench](https://dev.mysql.com/downloads/workbench/) and follow the steps of the installer

## Download the needed Python libraries
A list of the Python libraries needed to run this project can be found in the **reqirements.txt**

1. Open the console/terminal 
2. Install all needed libraries with the command `pip install -r requirements.txt`

## MySQL database configuration
1. Open the file **createUserDB.sql** in an editor and adjust the places where you need to enter your own user and password to connect to the database
2. Start MySQL Workbench and click with the right mouse button on the connection to the MySQL server and then click **"Start Command Line Client "**
3. First execute the following command: `source ...\bpa_lab_warehouse_operations\MySQL_configuration_scripts\createUserDB.sql` (path must be changed according to your own location)
4. Then execute the following command: `source ...\bpa_lab_warehouse_operations\MySQL_configuration_scripts\warehouse_place.sql` (path must be changed according to your own location)

## Installation and start of Mosquitto Broker (only needed for the testing version)
1. Download the [MQTT Broker](https://mosquitto.org/download/) for your operating system
2. Open your services settings and search for "Mosquitto Broker" (example picture of Windows)

   ![image](https://github.com/BpaLabTHCologne/bpa_lab_warehouse_operations/assets/134142150/6af1d821-b1af-4218-a34e-7489aa98ba03)

3. Start the "Mosquitto Broker" Service 


## Configuration of environment variables in own .env file.
The file **.env.example** serves as an example for an .env file. 

1. First after the project on the own hard disk was cloned, an .env file (name of the file: ".env") is created
2. Copy the text from the **.env.example** file into the newly created .env file
3. In the following it is explained how to get the values of the required environment variables:

### Obtaining the values for the required environment variables
1. Log in to the Camunda Cloud and click on **Cluster**
2. Click on **API** and then on **Create new client**
3. Enter a name in the format **FIRSTNAME_LASTNAME_CREDENTIALS** and click on **Create credentials**
4. When the credentials are created, go to the **Env vars** tab
5. Here should be represented all environment variables you need in your .env file, except **MYSQL_USER**, **MYSQL_PASSWORD** and **IS_CLOUD**
6. For **MYSQL_USER** and **MYSQL_PASSWORD** you should enter the corresponding user and password already created in **Configuration of MySQL database**
7. **IS_CLOUD** must be "true" or "false" depending on whether you are using Camunda 8 Cloud or Self-managed.

## Execution of the process application

### Pre-conditions in BPALab-Setup
* The **router for the local network** must be started 
* The **TXT controller from the warehouse robot** must be started
* The **Raspberry Pi, which serves as MQTT broker**, must be started

### Parameters to be passed for execution 
* If the process is to be started, the following variables must be passed in JSON format:
* **item** defines the name of the object for which the action will be executed
* **place_id** defines the place in a shelf for which the action should be executed
* **shelf_id** defines the shelf for which the action is to be executed
* **task** is the action that should be executed
* **transactionId** is a unique number needed for the message events that control the process between both pools or processes

* Example of placing an item in storage: `{"item":"Bicycle","place_id":3,"shelf_id":1,"task":"store","transactionId":0}` 
* Example of retrieving an item: `{"item":"Bicycle","place_id":3,"shelf_id":1,"task":"retrieve","transactionId":0}` 

### Execution
1. Establish a connection to **eduroam** and the **local network**
2. Start an instance of the process **warehouse-operations-process.bpmn** in Camunda
4. Start the programs **highbay_robot_app.py** for robot control and **warehouse_operations_process_app.py** for process control 
