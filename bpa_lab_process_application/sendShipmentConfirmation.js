const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

const sendShipmentConfirmation = zbc.createWorker({
  taskType: 'sendShipmentConfirmation',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => sendShipmentConfirmation.log('Job worker started successfully!')
});

function handler(job) {
  const correlationValue = 124;
  zbc.publishStartMessage({
    name: 'startShipment',
    variables: {
      initialProcessVariable: 'here',
      anotherProcessVariable: 333,
      correlationValue
    },
  })
  return job.complete({correlationValue: correlationValue});
}


module.exports = sendShipmentConfirmation;

  

  
