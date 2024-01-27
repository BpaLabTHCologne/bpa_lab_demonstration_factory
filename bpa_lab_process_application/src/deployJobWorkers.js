const { ZBClient } = require('zeebe-node');

//Importing all the job workers
require('./storeCustomerOrder');
require('./sendRejectionEmail');
require('./customerOrderStatusRejected');
require('./sendConfirmationEmail');
require('./customerOrderStatusApproved');
require('./checkFinishedProductAvailability');
require('./customerOrderStatusProductionRequired');
require('./sendCustomerOrderForProduction');
require('./storeProductionOrder');
require('./customerOrderStatusInProduction');
require('./checkComponentsAvailability');
require('./sendShipmentConfirmation');
require('./receiveShipmentConfirmation');
require('./customerOrderStatusShipped');
require('./sendShippedEmail');
// require('./sendToWarehouseOperations');


// Define an async main function to deploy a process, create a process instance, and log the outcome
async function main() {
  // Create a new ZBClient instance with the provided configuration
  const zbc = new ZBClient({
    hostname: 'zeebe'
  });

  try {
    // Deploy the order management and production control pool BPMN diagram
    const bpmnResult = await zbc.deployResource({
      processFilename: `./bpa_lab_bpm_models/bicycle-process-model.bpmn`,
    });

    // Log the order management and production control pool BPMN diagram deployment result
    console.log('Order management/Production control BPMN deployed successfully:', JSON.stringify(bpmnResult, null, 2));

    // Deploy the DMN decision model inside order management pool
    const dmnResult = await zbc.deployResource({
      decisionFilename: `./bpa_lab_bpm_models/orderApproval.dmn`,
    });

    // Log the DMN decision model inside order management pool
    console.log('DMN Decision model deployed successfully:', JSON.stringify(dmnResult, null, 2));

    // Deploy the shipment BPMN diagram
    const shipment = await zbc.deployResource({
      decisionFilename: `./bpa_lab_bpm_models/shipment-process.bpmn`,
    });

    // Log the shipment BPMN diagram
    console.log('Shipment BPMN deployed successfully:', JSON.stringify(shipment, null, 2));

    // Deploy the warehouse operations BPMN diagram
    const warehouseOperations = await zbc.deployResource({
      decisionFilename: `./bpa_lab_bpm_models/warehouse-operations-process.bpmn`,
    });

    // Log the warehouse operations BPMN diagram
    console.log('Warehouse operations BPMN deployed successfully:', JSON.stringify(warehouseOperations, null, 2));

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