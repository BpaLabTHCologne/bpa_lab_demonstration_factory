const ZB = require('zeebe-node')

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


const zbc = new ZB.ZBClient();

// Define an async main function to deploy a process, create a process instance, and log the outcome
async function main() {
  // Deploy the 'new-customer.bpmn' process

  const res = await zbc.deployResource({
    processFilename: `../Bicycle BPMN Model/bicycle-process-model.bpmn`,
    decisionFilename: `../Bicycle BPMN Model/orderApproval.dmn`,
  })

  // Log the deployment result
  console.log('\nProcess deployed successfully:', JSON.stringify(res, null, 2));

  // Create a process instance of the 'new-customer-process' process, with a customerId variable set
  // 'createProcessInstanceWithResult' awaits the outcome
  // const outcome = await zbc.createProcessInstanceWithResult({
  //     bpmnProcessId: 'Process_1gu1lel',
  //     variables: { 
  //       name: "Tommy Shelby",
  //       email: "tshelby@gmail.com",
  //       phone: "+491515151515",
  //       address: "Small Heath, Birmingham, England",
  //       product: "Mountain Bike",
  //       quantity: 5,
  //       orderStatus: "ORDER_RECEIVED",
  //       orderType: "single-order",
  //     }
  // });
  // Log the process outcome
  // console.log('Process outcome', JSON.stringify(outcome, null, 2));

}

// Call the main function to execute the script
main();