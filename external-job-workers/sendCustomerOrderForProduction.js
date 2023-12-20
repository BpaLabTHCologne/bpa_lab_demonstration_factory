const { ZBClient, Duration } = require('zeebe-node');
const uuid = require('uuid');

let orderID = '';
let name = '';
let email = '';
let phone = '';
let address = '';
let product = '';
let quantity = '';
let orderStatus = '';
let customerOrderApproval = '';
let deliveryDate = '';
let finishedProductQuantityAvailable = '';
let orderType = '';
let productionRequired = '';
let quantityNeededForProduction = '';

let updateToBrokerVariables = '';

const sendCustomerOrderForProduction = new ZBClient();

// Define your message payload with the necessary variables
const messagePayload = {
  messageId: uuid.v4(),
  name: 'juststart',
  variables: {
    // Include any other variables you want to pass to the workflow
  },
  timeToLive: Duration.seconds.of(10), // seconds
};

// Send the message to the workflow
//sendCustomerOrderForProduction.publishMessage(messagePayload);

sendCustomerOrderForProduction.createWorker({
  taskType: 'sendCustomerOrderForProduction',
  taskHandler: handler,
});

function handler(job) {
  console.log("Sending customer order now for production...");
  console.log("Number of bicycles to be produced: ", job.variables.quantityNeededForProduction);
  
  orderID = job.variables.orderID;
  name = job.variables.name;
  email = job.variables.email;
  phone = job.variables.phone;
  address = job.variables.address;
  product = job.variables.product;
  quantity = job.variables.quantity;
  orderStatus = job.variables.orderStatus;
  customerOrderApproval = job.variables.customerOrderApproval;
  deliveryDate = job.variables.deliveryDate;
  finishedProductQuantityAvailable = job.variables.finishedProductQuantityAvailable;
  orderType = job.variables.orderType;
  productionRequired = job.variables.productionRequired;
  quantityNeededForProduction = job.variables.quantityNeededForProduction;


  // Task worker business logic goes here
  updateToBrokerVariables = {
    //updatedProperty: 'newValue',
    orderID: orderID,
    name: name,
    email: email,
    phone: phone,
    address: address,
    product: product,
    quantity: quantity,
    orderStatus: orderStatus,
    customerOrderApproval: customerOrderApproval,
    deliveryDate: deliveryDate,
    finishedProductQuantityAvailable: finishedProductQuantityAvailable,
    orderType: orderType,
    productionRequired: productionRequired,
    quantityNeededForProduction: quantityNeededForProduction,
  };

  // Include the updated variables in the message payload for the next step in the workflow
  const updatedMessagePayload = {
    ...messagePayload,
    variables: {
      ...messagePayload.variables,
      ...updateToBrokerVariables,
    },
  };

  // Publish the updated message to the workflow
  sendCustomerOrderForProduction.publishMessage(updatedMessagePayload);

  // Complete the current job
  job.complete(updateToBrokerVariables);
  console.log("This is after job has been completed: ", updatedMessagePayload)
}

module.exports = sendCustomerOrderForProduction;
