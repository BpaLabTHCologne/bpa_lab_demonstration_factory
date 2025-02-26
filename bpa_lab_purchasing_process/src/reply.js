const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

const receiveConfirmation = zbc.createWorker({
  taskType: 'send-reply-purchasing',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => receiveConfirmation.log('Reply started successfully!')
});

function handler(job) {
  console.log("receiveConfirmation");
    console.log(job.variables);
    zbc.publishMessage({
      name: 'receiveFromPurchasing',
      correlationKey: job.variables["correlationValue"],
      variables: {
      },
      // timeToLive: Duration.seconds.of(10), // seconds
    })
    return job.complete();
}


module.exports = receiveConfirmation;
  