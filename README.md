# BPALab_GuidedProject_23_24
Project documentation of Guided Project in winter 2023

## Part 1: Evaluation of the learning concept 
Learning concept: We would like to investigate the pros and cons of using BPALab hardware components in process automation project with Camunda 8 in the bachelor program.

Your contribution: Please work on the project order in this repository (Project_Order_BicycleManufacturer_V1_2.pdf). 
For the implementation, the following BPALab elements are required
- Open Route Service (repository: [bpa_lab_openroute_service_API](https://github.com/BpaLabTHCologne/bpa_lab_openroute_service_API))
- Fischertechnik warehouse robot (repository: [bpa_lab_warehouse_operations](https://github.com/BpaLabTHCologne/bpa_lab_warehouse_operations))

As part of this process and as the most important deliverable, please prepare a report with answers to the questions below (min. 3-4 pages). This report shall provide comprehensive feedback on the learning concept.
Remark: At the same time, the technical work is  a good exercise for the upcoming work packages (see part 2).

Question on learning concepts:
- What is your previous knowledge in process automation in general and with BPMS in particular?
- While working on the project, what positive experiences did you have?
- What problems or questions did you encounter while working on the project assignment?
- What requirements would you have for the course materials/documentation? (Please assume you have read the book: Marlon Dumas, Fundamentals of Business Process Management, 2018 as a substitute for the lecture notes).
- What did you learn from working on this project in addition to your previous knowledge of business process management systems?
- For which scenarios can a BPMS like Camunda be used?
- How did you feel about using the software component (Open Route) and the hardware component (Fischertechnik robot)? What are pros and cons of involving hardware components?
- What suggestions do you have for improving the project assignment or the teaching concept when using BPA Lab building blocks?
- Do you have any other feedback you would like to share?

A few notes about the grading criteria for your report:
- Demonstrate extensive work on project assignment 
- Understand the learning concept and questions
- Answer the questions above
- Provide (honest) feedback/insights into your learning experience

Please do not hesitate to contact me, if you have any questions!!!

## Part 2: Designing and implementing the end to end business process of the Demonstration Factory
Released work items will be maintained in the project board of this repository!

This is a list of potential work packages for discussion on 09.10.2023

### Task group 1: Designing and implementing new or existing process applications with Camunda 8
Design operational process model using strategic process model/process description and doing own research on the domain. 
In addition to process define scenarios/ events and align interfaces and process variables with other process applications

Implement the process applications
- Define forms in Camunda
- Implement job worker / Zeebe client

 Process applications available:
 - Purchasing (new)
 - Order management (new, existing solution for Camunda 7)
 - Shipment (new, existing solution for Camunda 7 and UIPath)
 - Only to be integrated: Manufacturing and logistics (existing in Camunda 8) 

### Task group 2: industrial IOT and its integration into process applications
Document different options of IOT integration in BPMS (based on master thesis IOT aware business processes)
Develop further IOT applications for logistics and manufacturing
Integration with process applications

### Task group 3: Coordination task (rewarded)
Coordination of topics like 
- Alignment and documentation of interfaces / messages / data exchange between process applications.
- Infrastructure: Docker & co
- Testing: defining test cases and coordination

### Task group 4: Integration of other process automation technologies
Integrating other platform / tools in the overall end to end demonstration scenario  

