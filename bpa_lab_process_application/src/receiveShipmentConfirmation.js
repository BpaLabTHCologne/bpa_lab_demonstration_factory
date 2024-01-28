const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

const receiveShipmentConfirmation = zbc.createWorker({
  taskType: 'receiveShipmentConfirmation',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => receiveShipmentConfirmation.log('Job worker started successfully!')
});

function handler(job) {
  console.log("receiveShipmentConfirmation");
    console.log(job.variables);
    zbc.publishMessage({
      name: 'shipmentDone',
      correlationKey: job.variables["correlationValue"],
      variables: {
      },
      // timeToLive: Duration.seconds.of(10), // seconds
    })
    return job.complete();
}


module.exports = receiveShipmentConfirmation;
  
