const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

let orderID = '';
let productionOrderID = '';
let customerProduct = '';
let componentName = '';
let orderQuantity = '';
let task = '';
let transactionId = ''; 

const sendPurchasingConfirmation = zbc.createWorker({
  taskType: 'sendPurchasingConfirmation',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => sendPurchasingConfirmation.log('Job worker started successfully!')
});

function handler(job) {
  const correlationValue = 289;
  orderID = job.variables.orderID;
  productionOrderID = job.variables.productionOrderID;
  customerProduct = job.variables.customerProduct;
  componentName = job.variables.componentName;
  orderQuantity = job.variables.orderQuantity;
  task = job.variables.task;
  transactionId = job.variables.transactionId;


  zbc.publishStartMessage({
    name: 'startPurchasing',
    variables: {
      correlationValue,
      orderID: job.variables.orderID,
      productionOrderID: job.variables.productionOrderID,
      customerProduct: job.variables.customerProduct,
      componentName: job.variables.componentName,
      orderQuantity: job.variables.orderQuantity,
      task: job.variables.task,
      transactionId: job.variables.transactionId,
    },
  })

  return job.complete({ correlationValue: correlationValue });
}


module.exports = sendPurchasingConfirmation;




