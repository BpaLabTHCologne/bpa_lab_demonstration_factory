const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient('localhost:26500')

const shipmentCompleted = zbc.createWorker({
  taskType: 'shipmentCompleted',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => shipmentCompleted.log('Job worker started successfully!')
});

function handler(job) {
  console.log("shipmentCompleted");
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


module.exports = shipmentCompleted;
  
