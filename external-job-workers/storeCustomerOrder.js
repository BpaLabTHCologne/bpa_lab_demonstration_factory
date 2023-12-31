const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient();

//External job worker to store customer order
const storeCustomerOrder = zbc.createWorker({
  taskType: 'storeCustomerOrder',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => storeCustomerOrder.log('Job worker started successfully!')
});

function handler(job) {
  let insertId = 0; // Declare insertId in the outer scope
  let orderDateTime = '';

  storeCustomerOrder.log('\nTask variables', job.variables);

  // Accessing optional variables
  const optionalVariable = job.variables.optionalVariable;
  console.log('Optional variables: ', job.variables);

  var customerOrderConnection = mysql.createConnection({
    connectionLimit: 10,
    host: process.env.MYSQL_HOST_NAME,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE,
    port: process.env.MYSQL_HOST_PORT,
  });

  customerOrderConnection.connect();

  customerOrderConnection.query('INSERT INTO `customer_order` (`id`, `name`, `email`, `phone`, `address`, `product`, `quantity`, `orderStatus`) VALUES (NULL, "' + job.variables.customerName +'", "' + job.variables.customerEmail +'", "'+ job.variables.customerPhone +'", "' + job.variables.customerAddress + '", "' + job.variables.customerProduct + '", "'+ job.variables.customerQuantity + '", "'+ job.variables.orderStatus + '");', (err, insertResults, fields) => {
    if (err) {
        console.error('Error executing insert query:', err.message);
        return;
    }

    // Access the insertId from the callback
    const insertId = insertResults.insertId;
    console.log("\ninsertId:", insertId);

    // Now, perform the SELECT query using the insertId
    const updateToBrokerVariables = {
      orderID: insertId,
      updatedProperty: 'newValue',
      orderDateTime: orderDateTime,
    }
    return job.complete(updateToBrokerVariables)
  
    
});

}

module.exports = storeCustomerOrder;
