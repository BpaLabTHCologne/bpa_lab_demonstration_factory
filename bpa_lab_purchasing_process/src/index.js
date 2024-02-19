//const ZB = require('zeebe-node')
const { ZBClient } = require('zeebe-node');
require('./sendMail');
require('./storePurchasingOrder'); 
require('./message');
require('./reply');
require('./finishPurchasing');
require('./updateapprove');


// Define an async main function to deploy a process, create a process instance, and log the outcome
async function main() {
  // Create a new ZBClient instance with the provided configuration
  const zbc = new ZBClient({
    hostname: 'zeebe'
  });

  try {
    // Deploy the purchasing BPMN diagram
    const bpmnResult = await zbc.deployResource({
      processFilename: `./bpa_lab_bpm_models/purchasing_process.bpmn`,
    });

    // Log the purchasing BPMN diagram deployment result
    console.log('\nPurchasing BPMN deployed successfully:', JSON.stringify(bpmnResult, null, 2));

    // Deploy the start BPMN diagram
    const start = await zbc.deployResource({
      processFilename: `./bpa_lab_bpm_models/start.bpmn`,
    });

    // Log the purchasing BPMN diagram deployment result
    console.log('\nStart BPMN deployed successfully:', JSON.stringify(start, null, 2));

    // Deploy the DMN vendor decision model 
    const dmnResult = await zbc.deployResource({
      decisionFilename: `./bpa_lab_bpm_models/vendor-decision.dmn`,
    });

    // Log the DMN vendor decision model 
    console.log('\nDMN Vendor Decision deployed successfully:', JSON.stringify(dmnResult, null, 2));

    // Deploy the DMN price decision model 
      const dmnResult2 = await zbc.deployResource({
        decisionFilename: `./bpa_lab_bpm_models/price-decision.dmn`,
      });
  
      // Log the DMN price decision model 
      console.log('\nDMN Price Decision deployed successfully:', JSON.stringify(dmnResult2, null, 2));

    // Deploy the Requisition form
    // const requisition = await zbc.deployResource({
    //   formFilename: `../Diagramms/purchasing-requisition.form`,
    // });

    // Log the warehouse operations BPMN diagram
    // console.log('\nRequisition Form deployed successfully:', JSON.stringify(requisition, null, 2));

  } catch (error) {
    // Handle any errors that occur during deployment
    console.error('Deployment failed:', error);
  } finally {
    // Close the ZBClient instance to release resources
    zbc.close();
  }
}

// Call the main function to execute the script
main();
