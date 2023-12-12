const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient();

//External job worker to store customer order
const checkFinishedProductAvailability = zbc.createWorker({
  taskType: 'checkFinishedProductAvailability',
  taskHandler: handler,
  debug: true,
  loglevel: 'INFO',
  onReady: () => checkFinishedProductAvailability.log('Job worker started successfully!')
});

function handler(job) {

  checkFinishedProductAvailability.log('Task variables', job.variables);

  var finishedProductConnection = mysql.createConnection({
    host: process.env.MYSQL_HOST_NAME,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE_FINISHED_PRODUCT,
    port: process.env.MYSQL_HOST_PORT,
  });

  finishedProductConnection.connect();

  finishedProductConnection.query('INSERT INTO `customer_order` (`id`, `name`, `email`, `phone`, `address`, `product`, `quantity`, `orderStatus`) VALUES (NULL, "' + job.variables.name +'", "' + job.variables.email +'", "'+ job.variables.phone +'", "' + job.variables.address + '", "' + job.variables.product + '", "'+ job.variables.quantity + '", "'+ job.variables.orderStatus + '");', (err, results, fields) => {
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
      orderID: insertId,
      // optionalVariable: optionalVariable, // Add optional variables to the update object
    };

    finishedProductConnection.end(); // Close the finishedProductConnection when done

    return job.complete(updateToBrokerVariables);
  }
}

module.exports = checkFinishedProductAvailability;
