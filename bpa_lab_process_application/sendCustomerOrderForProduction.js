const { ZBClient, Duration } = require('zeebe-node');
const zbc = new ZBClient({
  hostname: 'zeebe'
});
const uuid = require('uuid');

let orderID = '';
let customerName = '';
let customerEmail = '';
let customerPhone = '';
let customerAddress = '';
let customerProduct = '';
let customerQuantity = '';
let orderStatus = '';
let customerOrderApproval = '';
let expectedDeliveryDate = '';
let finishedProductQuantityAvailable = '';
let orderType = '';
let productionRequired = '';
let quantityNeededForProduction = '';
let customerOrderDate = '';
let customerOrderTime = '';
let productMass = '';
let updateToBrokerVariables = '';

// Define your message payload with the necessary variables
const messagePayload = {
  messageId: uuid.v4(),
  name: 'receiveShipmentOrder',
  variables: {
    // Include any other variables you want to pass to the workflow
  },
  timeToLive: Duration.seconds.of(10), // seconds
};

// Send the message to the workflow
//sendCustomerOrderForShipment.publishMessage(messagePayload);

const sendCustomerOrderForShipment = zbc.createWorker({
  taskType: 'sendCustomerOrderForShipment',
  taskHandler: handler,
  onReady: () => sendCustomerOrderForShipment.log('Job worker started successfully!')
});

function handler(job) {
  console.log("\nNumber of bicycles to be produced: ", job.variables.quantityNeededForProduction);

  orderID = job.variables.orderID;
  customerName = job.variables.customerName;
  customerEmail = job.variables.customerEmail;
  customerPhone = job.variables.customerPhone;
  customerAddress = job.variables.customerAddress;
  customerProduct = job.variables.customerProduct;
  customerQuantity = job.variables.customerQuantity;
  orderStatus = job.variables.orderStatus;
  customerOrderApproval = job.variables.customerOrderApproval;
  expectedDeliveryDate = job.variables.expectedDeliveryDate;
  finishedProductQuantityAvailable = job.variables.finishedProductQuantityAvailable;
  orderType = job.variables.orderType;
  productionRequired = job.variables.productionRequired;
  quantityNeededForProduction = job.variables.quantityNeededForProduction;
  customerOrderDate = job.variables.customerOrderDate;
  customerOrderTime = job.variables.customerOrderTime;
  productMass = job.variables.productMass;


  // Task worker business logic goes here
  updateToBrokerVariables = {
    //updatedProperty: 'newValue',
    orderID: orderID,
    customerName: customerName,
    customerEmail: customerEmail,
    customerPhone: customerPhone,
    customerAddress: customerAddress,
    customerProduct: customerProduct,
    customerQuantity: customerQuantity,
    orderStatus: orderStatus,
    customerOrderApproval: customerOrderApproval,
    expectedDeliveryDate: expectedDeliveryDate,
    finishedProductQuantityAvailable: finishedProductQuantityAvailable,
    orderType: orderType,
    productionRequired: productionRequired,
    quantityNeededForProduction: quantityNeededForProduction,
    customerOrderDate: customerOrderDate,
    customerOrderTime: customerOrderTime,
    productMass: productMass,
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
  zbc.publishMessage(updatedMessagePayload);

  // Complete the current job
  job.complete(updateToBrokerVariables);
  console.log("\nSending customer order for shipment: ", updatedMessagePayload)
}

module.exports = sendCustomerOrderForShipment;
