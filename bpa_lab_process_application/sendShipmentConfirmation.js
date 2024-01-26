const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

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

const sendShipmentConfirmation = zbc.createWorker({
  taskType: 'sendShipmentConfirmation',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => sendShipmentConfirmation.log('Job worker started successfully!')
});

function handler(job) {
  const correlationValue = 124;
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


  zbc.publishStartMessage({
    name: 'startShipment',
    variables: {
      initialProcessVariable: 'here',
      anotherProcessVariable: 333,
      correlationValue,
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
    },
  })

  return job.complete({ correlationValue: correlationValue });
}


module.exports = sendShipmentConfirmation;




