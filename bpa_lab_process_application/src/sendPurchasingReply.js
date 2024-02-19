const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

const sendPurchasingReply = zbc.createWorker({
  taskType: 'sendPurchasingReply',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => sendPurchasingReply.log('Job worker started successfully!')
});

function handler(job) {
  console.log("sendPurchasingReply");
    console.log(job.variables);
    zbc.publishMessage({
      name: 'receiveFromPurchasing',
      correlationKey: "124",
      variables: {
      },
      // timeToLive: Duration.seconds.of(10), // seconds
    })
    return job.complete();
}


module.exports = sendPurchasingReply;
  
