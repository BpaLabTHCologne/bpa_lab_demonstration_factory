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