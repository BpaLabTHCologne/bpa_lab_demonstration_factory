const ZB = require('zeebe-node')
const mysql = require('mysql');
const nodemailer = require('nodemailer');
const fs = require('fs');

const zbc = new ZB.ZBClient();

//External job worker to store customer order
const storeCustomerOrder = zbc.createWorker({
  taskType: 'storeCustomerOrder',
  taskHandler: handler,
  debug: true,
  loglevel: 'INFO',
  onReady: () => storeCustomerOrder.log('Job worker started successfully!')
});

function handler(job) {
  let insertId = 0; // Declare insertId in the outer scope

  storeCustomerOrder.log('Task variables', job.variables);

  // Accessing optional variables
  const optionalVariable = job.variables.optionalVariable;
  console.log('Optional variables: ', job.variables);

  var connection = mysql.createConnection({
    host: process.env.MYSQL_HOST_NAME,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE,
    port: process.env.MYSQL_HOST_PORT,
  });

  connection.connect();

  connection.query('INSERT INTO `customer_order` (`id`, `name`, `email`, `phone`, `address`, `product`, `quantity`, `orderStatus`) VALUES (NULL, "' + job.variables.name +'", "' + job.variables.email +'", "'+ job.variables.phone +'", "' + job.variables.address + '", "' + job.variables.product + '", "'+ job.variables.quantity + '", "'+ job.variables.orderStatus + '");', (err, results, fields) => {
    if (err) {
      console.error('Error executing query:', err.message);
      return;
    }

    // Access the insertId from the callback
    console.log("insertId:", results.insertId);
    insertId = results.insertId;

    // Now you can use insertId in the callback or pass it to another function

    console.log("Results: ", JSON.stringify(results));

    // Continue with your logic, e.g., call another function
    updateToBrokerVariables();
  });

  function updateToBrokerVariables() {
    const updateToBrokerVariables = {
      updatedProperty: insertId,
      // optionalVariable: optionalVariable, // Add optional variables to the update object
    };

    connection.end(); // Close the connection when done

    return job.complete(updateToBrokerVariables);
  }
}

module.exports = storeCustomerOrder;
