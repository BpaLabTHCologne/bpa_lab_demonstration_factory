const { ZBClient } = require('zeebe-node');

//Importing all the job workers
require('./sendDeliveryEmail');
require('./sendShippedEmail');
require('./shipmentCompleted');


// Define an async main function to deploy a process, create a process instance, and log the outcome
async function main() {
  // Create a new ZBClient instance with the provided configuration
  const zbc = new ZBClient('localhost:26500')

  try {
    // Deploy the shipment process BPMN diagram
    const bpmnResult = await zbc.deployResource({
      processFilename: `./bpa_lab_bpm_models/shipment-process-connect.bpmn`,
    });

    // Log the shipment process BPMN diagram deployment result
    console.log('\nShipment process BPMN deployed successfully:', JSON.stringify(bpmnResult, null, 2));

    // Deploy the DMN decision model for the transport company
    const dmnResult = await zbc.deployResource({
      decisionFilename: `./bpa_lab_bpm_models/transportCompany.dmn`,
    });

    // Log the shipment process BPMN diagram deployment result
    console.log('\nShipment process BPMN deployed successfully:', JSON.stringify(dmnResult, null, 2));

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