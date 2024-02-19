/*

const { ZBClient, Duration} = require('zeebe-node')


const zbc = new ZBClient();



async function main() {
   
  console.log('sending message ')

zbc.createWorker({

  //zbc.createWorker({
    taskType: "send_start_purchasing",
    taskHandler: (job) => {
      console.log("send_start_purchasing");
      console.log(job.variables);
      const correlationValue = 124;
      zbc.publishStartMessage({
        //name: 'startmessage_processB',
          name: 'startmessagePurchasing',
        variables: {
          material_key: "Mountain bike wheels",
          quantity_key: 5,
          correlationValue
        },
        timeToLive: Duration.seconds.of(10), // seconds
      })
      return job.complete({correlationValue: correlationValue});
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
//--

let material_key = '';
let quantity_key = '';


const sendConfirmation = zbc.createWorker({
  taskType: 'send_start_purchasing',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => sendConfirmation.log('Job worker started successfully!')
});

function handler(job) {
  const correlationValue = 124;
  material_key = job.variables.material_key;
  quantity_key = job.variables.quantity_key;


  zbc.publishStartMessage({
    name: 'startmessagePurchasing',
    variables: {
      correlationValue,
      material_key: "Mountain bike wheels",
      quantity_key: 5
    },
  })

  return job.complete({ correlationValue: correlationValue });
}


module.exports = sendConfirmation;

