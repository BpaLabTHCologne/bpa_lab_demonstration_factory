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
let approve_key = ''
let material_key = ''
let price_key = ''
let quantity_key = ''
let vendor_key = ''

const sendProductionOrderToPurchasing = zbc.createWorker({
  taskType: 'sendProductionOrderToPurchasing',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => sendProductionOrderToPurchasing.log('Job worker started successfully!')
});

function handler(job) {
  const correlationValue = job.variables.orderID;
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
  approve_key = job.variables.approve_key;
  material_key = job.variables.material_key;
  price_key = job.variables.price_key;
  quantity_key = job.variables.quantity_key;
  vendor_key = job.variables.vendor_key;


  zbc.publishStartMessage({
    name: 'startmessagePurchasing',
    variables: {
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
      approve_key: job.variables.approve_key,
      material_key: job.variables.material_key,
      price_key: job.variables.price_key,
      quantity_key: job.variables.quantity_key,
      vendor_key: job.variables.vendor_key,
    },
  })

  return job.complete({ correlationValue: correlationValue });
}


module.exports = sendProductionOrderToPurchasing;




