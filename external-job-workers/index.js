const ZB = require('zeebe-node')
const mysql = require('mysql');
const zbc = new ZB.ZBClient();

const zbWorker = zbc.createWorker({
  taskType: 'storeCustomerOrder',
  taskHandler: handler,
  debug: true,
  loglevel: 'INFO',
  onReady: () => zbWorker.log('Job worker started successfully!')
});

function handler(job) {
  zbWorker.log('Task variables', job.variables);

  // Accessing optional variables
  const optionalVariable = job.variables.optionalVariable;
  console.log('Optional variables: ', job.variables);

  var connection = mysql.createConnection({
  host     : process.env.MYSQL_HOST_NAME,
  user     : process.env.MYSQL_USER,
  password : process.env.MYSQL_PASSWORD,
  database : process.env.MYSQL_DATABASE,
  port     : process.env.MYSQL_HOST_PORT,
});

connection.connect();

// connection.query('SELECT * from customer_order', function(err, rows, fields) {
//     if(err) console.log(err);
//     console.log('The solution is: ', rows);
//     connection.end();
// });

connection.query('INSERT INTO `customer_order` (`id`, `name`, `email`, `phone`, `address`, `product`, `quantity`, `orderStatus`) VALUES (NULL, "' + job.variables.name +'", "' + job.variables.email +'", "'+ job.variables.phone +'", "' + job.variables.address + '", "' + job.variables.product + '", "'+ job.variables.quantity + '", "");', (err, results, fields) => {
  if (err) {
    console.error('Error executing query:', err.message);
    return;
  }
  console.log('Query results:', results);
});

  const updateToBrokerVariables = {
    updatedProperty: 'newValue',
    optionalVariable: optionalVariable, // Add optional variables to the update object
  };

  return job.complete(updateToBrokerVariables);
}


// Define an async main function to deploy a process, create a process instance, and log the outcome
async function main() {
  // Deploy the 'new-customer.bpmn' process
  const res = await zbc.deployProcess('../Bicycle BPMN Model/bicycle-process-model.bpmn');
  // Log the deployment result
  console.log('Deployed process:', JSON.stringify(res, null, 2));

  // Create a process instance of the 'new-customer-process' process, with a customerId variable set
  // 'createProcessInstanceWithResult' awaits the outcome
  const outcome = await zbc.createProcessInstanceWithResult({
      bpmnProcessId: 'order-management-id',
      variables: { 
        name: "Rahib",
        email: "test@gmail.com",
        phone: "+491515151515",
        address: "221B Baker Street",
        product: "Mountain Bike",
        quantity: 5,
        orderStatus: "ORDER_IN_PROGRESS",
      }
  });
  // Log the process outcome
  console.log('Process outcome', JSON.stringify(outcome, null, 2));
}

// Call the main function to execute the script
main();