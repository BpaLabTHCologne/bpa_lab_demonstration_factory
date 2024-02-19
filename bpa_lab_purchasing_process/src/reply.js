
/*
const { ZBClient, Duration} = require('zeebe-node')


const zbc = new ZBClient();



async function main() {
   
  console.log('sending reply ')

    
  
 zbc.createWorker({

  //zbc.createWorker({
    taskType: "send_reply_purchasing",
    taskHandler: (job) => {
      console.log("send_reply_purchasing");
      console.log(job.variables);
      //const correlationValue = 124;
      zbc.publishMessage({
        //name: 'replymessage_processA',
        name: 'replymessage_purchasing',
        correlationKey: job.variables["correlationValue"],
        variables: {
          replyMaterialVariable: job.variables.material_key,
          replyQuantityVariable: job.variables.quantity_key
        },
        //timeToLive: Duration.seconds.of(10), // seconds
      })
      return job.complete();
    }
  });
}


main();
*/



//-------------------

const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

//const { ZBClient, Duration} = require('zeebe-node')


//const zbc = new ZBClient();

const receiveConfirmation = zbc.createWorker({
  taskType: 'send_reply_purchasing',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => receiveConfirmation.log('Reply started successfully!')
});

function handler(job) {
  console.log("receiveConfirmation");
    console.log(job.variables);
    zbc.publishMessage({
      name: 'replymessage_purchasing',
      correlationKey: job.variables["correlationValue"],
      variables: {
      },
      // timeToLive: Duration.seconds.of(10), // seconds
    })
    return job.complete();
}


module.exports = receiveConfirmation;
  