const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

const receiveFromWarehouseOperations = zbc.createWorker({
  taskType: 'receiveFromWarehouseOperations',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => receiveFromWarehouseOperations.log('Job worker started successfully!')
});

function handler(job) {
  console.log("receiveFromWarehouseOperations");
    console.log(job.variables);
    zbc.publishMessage({
      name: 'receiveWarehouseOperations',
      correlationKey: "124",
      variables: {
      },
      // timeToLive: Duration.seconds.of(10), // seconds
    })
    return job.complete();
}


module.exports = receiveFromWarehouseOperations;
  
