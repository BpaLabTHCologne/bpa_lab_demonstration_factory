const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

const receiveShipmentConfirmation = zbc.createWorker({
  taskType: 'shipmentCompleted',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => receiveShipmentConfirmation.log('Job worker started successfully!')
});

function handler(job) {
  console.log("shipmentCompleted");
    console.log(job.variables);
    zbc.publishMessage({
      name: 'shipmentDone',
      correlationKey: job.variables["correlationValue"],
      variables: {
        replyProcessVariable: 'there',
        anotherReplyProcessVariable: 9999
      },
      // timeToLive: Duration.seconds.of(10), // seconds
    })
    return job.complete();
}


module.exports = receiveShipmentConfirmation;
  
