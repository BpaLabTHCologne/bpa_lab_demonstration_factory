const ZB = require('zeebe-node')

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

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

