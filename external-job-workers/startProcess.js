const ZB = require('zeebe-node')
require('./storeCustomerOrder');
require('./sendRejectionEmail');

const zbc = new ZB.ZBClient();

// Define an async main function to deploy a process, create a process instance, and log the outcome
async function main() {
  // Deploy the 'new-customer.bpmn' process
  const res = await zbc.deployProcess('../Bicycle BPMN Model/bicycle-process-model.bpmn');
  // Log the deployment result
  console.log('Deployed process:', JSON.stringify(res, null, 2));

  // Create a process instance of the 'new-customer-process' process, with a customerId variable set
  // 'createProcessInstanceWithResult' awaits the outcome
  const outcome = await zbc.createProcessInstanceWithResult({
      bpmnProcessId: 'order-management-id',
      variables: { 
        name: "Rahib",
        email: "test@gmail.com",
        phone: "+491515151515",
        address: "221B Baker Street",
        product: "Mountain Bike",
        quantity: 5,
        orderStatus: "ORDER_IN_PROGRESS",
      }
  });
  // Log the process outcome
  console.log('Process outcome', JSON.stringify(outcome, null, 2));

}

// Call the main function to execute the script
main();