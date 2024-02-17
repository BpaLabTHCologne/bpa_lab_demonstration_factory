# BPALab_GuidedProject_23_24 Shirina's Branch
Project documentation of Guided Project in winter 2023 for Purchasing

## Overview
This documentation provides a process decription and guides you through the setup of the purchasing process.

## Prerequisites
Docker installed on your machine. 
Node.js and npm installed. 


### Step 1: Installation of Camunda 8 Self-managed 
[Camunda 8 Self-Managed](https://docs.camunda.io/docs/self-managed/about-self-managed/) is to be used for the implementation and testing of the process applications. To use Camunda 8 Self-Managed, Docker Desktop and the Docker images of Camunda 8 are required.
In addition, the Camunda Desktop Modeler is required to model and deploy models locally, as the Web Modeler, as it is available in the Camunda SaaS version, is only available for enterprise customers.

#### Installation of Docker Desktop
To install Docker Desktop, follow this link: https://docs.docker.com/get-docker/ and select the version for your operating system. Then follow the installation instructions. Please note that your PC will restart once during or after the installation. 

#### Installation of Camunda 8 Docker Images
Camunda provides the Camunda 8 platform as a multi-container application. The platform can then be installed and started via Docker Compose, which was installed together with Docker Desktop

The **recommended** installation is the Camunda 8 platform with all basic components **(all except Optimize, Identity, Keycloak and Web Modeler)**:
1. Follow this link: https://github.com/camunda/camunda-platform
2. Read the instructions in the chapter "Using docker compose" of the README and clone the repository **but then use the docker compose file for the basic/core components as described in the subchapter _"Using the basic components"_**

#### Installation of Camunda Desktop Modeler
Follow this link: https://camunda.com/download/modeler/ and install the modeler for your own operating system

### Step 2: Run Camunda-Platform Container via Docker
Excute the steps in this repo https://github.com/camunda/camunda-platform

### Step 3: Clone the Repository
Clone this repository
git clone https://github.com/BpaLabTHCologne/BPALab_GuidedProject_23_24.git

### Step 4: Configure Ethereal Email 

Navigate to https://ethereal.email/create and copy the configuration for Nodemailer. 
In BPALab_GuidedProject_23_24/external-workers/sendmail.js you need to replace the old nodemailer config with your new one as they time out after some time. 

 
### Step 5: Navigate to the project directory
cd BPALab_GuidedProject_23_24
cd external-workers 
node index.js
The external worker is now listening for tasks.

### Step 6: Start a Process Instance in Camunda
Open http://localhost:8082/camunda in your browser.
Log in using the credentials demo/demo. See the Tasklist. 

### Step 7: Operate on the Process Instance
Start a new process instance of your deployed process.
Observe the external worker console for task handling.
Check the Camunda Tasklist for the task associated with the process instance.
Complete the task in the Tasklist and observe the external worker console.#


### Conclusion
You have successfully set up a project involving Camunda Platform, Docker and interacted with a process instance through the Camunda Tasklist.


## Process Documentation 

![process image](https://github.com/BpaLabTHCologne/BPALab_GuidedProject_23_24/blob/shirina/purchasing-process-overview-executable.png?raw=true)


The purpose of a purchasing process is to acquire goods efficiently and effectively for bicycle manufacturing from external vendors. This process is critical for ensuring that Bike GmbH obtains the necessary inputs to carry out its operations.

The process has two starting points. The first one being a message event triggered by the order management process. It provides the needed goods for the manufacturing of a bike via message (material_key and quantity_key). The other start entry is a manual trigger which makes the purchasing clerk enter their purchase requisition in a form. As soon as the inputs are complete the two DMN tables calculate the vendor and price (vendor_key and price_key). If the price is bigger than 10 the purchasing manager needs to approve the requisition. If the manager declines the clerk must modify the purchasing requisition and the calculation and approval starts again. If the manager approves or the price is 10 or less (direct approve) the form shows all entries again before storing the reqisition in the database as approved. After that a mail is send to the vendor on ethereal. The clerk needs to receive the reply and needs to manually confirm it within 1 minute. If the clerk doesn't confirm the reply within 1 minute another task to cancel the Purchaing Order starts before the database updates the order status to REJECTED. The process is completed. If the clerk is in time the order gets approved and manual actions in the warehouse are triggered to store the purchased items. After that the database updates the status to DONE. The process is completed. 


## Testing 
Following Testcases were tested (direct approve means price is 10 or below): 

- manual trigger + direct approve + in time 
- manual trigger + direct approve + not in time 
- manual trigger + approve manager + in time 
- manual trigger + approve manager + not in time 
- manual trigger + modify + in time  
- manual trigger + modify + not in time  
- external trigger + direct approve + in time 
- external trigger + direct approve + not in time 
- external trigger + approve manager + in time 
- external trigger + approve manager + not in time 
- external trigger + modify + in time 
- external trigger + modify + not in time 



 
