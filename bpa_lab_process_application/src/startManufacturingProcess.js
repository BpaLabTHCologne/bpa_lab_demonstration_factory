const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

let orderID = '';

const startManufacturingProcess = zbc.createWorker({
  taskType: 'startManufacturingProcess',
  taskHandler: handler,
  onReady: () => startManufacturingProcess.log('Job worker started successfully!')
});

function handler(job) {
  const correlationValue = job.variables.orderID;
  orderID = job.variables.orderID;

  zbc.publishStartMessage({
    name: 'receivedManufacturingOrder',
    variables: {
      correlationValue,
      orderID: orderID,
    },
  })

  return job.complete({ correlationValue: correlationValue });
}


module.exports = startManufacturingProcess;




