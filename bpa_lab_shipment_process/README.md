# BPALab_GuidedProject_23_24_Shipment Process
Project documentation of Shipment process Guided Project in winter 2023

## Overview
Shipment process starts with a trigger from order management process (message start event). The idea is to fetch the information about the placed order (Address, Product details, weight of the product, email address, delivery date of placed order etc.) for starting the calculation of the coordinates of the shipment location, travel distance and travel duration.This part of the process has been implemented by Rest API connector “GET method”. The DMN in this process identifies the carrier company by the weight and the postal code of the ordered product. There is a call activity in the process that informs Warehouse about the selected company and start the process of the shipment with an email notification to the customer that the shipment has been started!

Business requirements:

1. The system should be able to decide the carrier company.
2. The system should be able to calculate the travel distance.
3. The system should be able to calculate the travel duration.
4. The system should be able to communicate with warehouse operations.
5. The systems should be able to send notification to the customer email.

## Process design and description

![image](https://github.com/BpaLabTHCologne/BPALab_GuidedProject_23_24/assets/33633145/5f3c4450-e4d0-4d4b-b6b4-81514ec188bb)

1. The design of the shipment process is made in such a way where Input of the shipping information can be inherited from the order management process or from the manual input from a form (user task) in the process itself. 
2. To get the coordinates of the shipping address and to calculate the distance/duration API key from open route service is needed.(Using GET method) which can be generated for free by creating an account at https://openrouteservice.org/. Getting the coordinates of the shipping address calculating the distance is implemented by REST API Connector Task. 


   Link for gettting coordinants: https://openrouteservice.org/dev/#/api-docs/geocode/search/get 


   Link for calculating distance: https://openrouteservice.org/dev/#/api-docs/v2/directions/{profile}/get



   Self-managed Camunda does not provide the templet of the REST connectors. It is necessary to place the https://github.com/BpaLabTHCologne/BPALab_GuidedProject_23_24/tree/shaira/Rest-self-templet 
   …\Camunda\resources\element-templates folder.

   For more details: https://github.com/BpaLabTHCologne/bpa_lab_openroute_service_API 

3. The size of the product and the postal code of the shipping address is used for choosing the company and size of the vehicle (DMN table). 

4. The call activity process is used for getting confirmation from the warehouse operation that the shipment has been started. Warehouse operation: https://github.com/BpaLabTHCologne/bpa_lab_warehouse_operations

5. Sending email to the customer is a service task that is created from the Nodemailer: https://ethereal.email/create 


## Installation & Run
For running the project successfully these tools need to be installed in the system:
## Installation of Camunda 8 Self-managed 
[Camunda 8 Self-Managed](https://docs.camunda.io/docs/self-managed/about-self-managed/) is to be used for the implementation and testing of the process applications. To use Camunda 8 Self-Managed, Docker Desktop and the Docker images of Camunda 8 are required.
In addition, the Camunda Desktop Modeler is required to model and deploy models locally, as the Web Modeler, as it is available in the Camunda SaaS version, is only available for enterprise customers.

### Installation of Docker Desktop
To install Docker Desktop, follow this link: https://docs.docker.com/get-docker/ and select the version for your operating system. Then follow the installation instructions. Please note that your PC will restart once during or after the installation. 

### Installation of Camunda 8 Docker Images
Camunda provides the Camunda 8 platform as a multi-container application. The platform can then be installed and started via Docker Compose, which was installed together with Docker Desktop

The **recommended** installation is the Camunda 8 platform with all basic components **(all except Optimize, Identity, Keycloak and Web Modeler)**:
1. Follow this link: https://github.com/camunda/camunda-platform
2. Read the instructions in the chapter "Using docker compose" of the README and clone the repository **but then use the docker compose file for the basic/core components as described in the subchapter _"Using the basic components"_**

### Installation of Camunda Desktop Modeler
Follow this link: https://camunda.com/download/modeler/ and install the modeler for your own operating system

## Executed test cases

![image](https://github.com/BpaLabTHCologne/BPALab_GuidedProject_23_24/assets/33633145/6fd279c7-0ab5-4944-b3f9-08acedee43c3)



 
