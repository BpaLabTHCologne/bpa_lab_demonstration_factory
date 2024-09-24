const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

//External job worker to store customer order
const customerOrderStatusProductionRequired = zbc.createWorker({
  taskType: 'customerOrderStatusProductionRequired',
  taskHandler: handler,
  debug: true,
  loglevel: 'INFO',
  onReady: () => customerOrderStatusProductionRequired.log('Job worker started successfully!')
});

function handler(job) {
  try {
    // Create MySQL connection
    var dbConnection = mysql.createConnection({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE,
      port: process.env.MYSQL_HOST_PORT,
      server: 'localhost',
    });

    dbConnection.connect((error) => {
      if (error) {
        console.log("Error connecting to MySQL database", error);
        return job.fail(error.message);
      }
    });

    // Retrieve the orderID from the Zeebe job variables
    const orderID = parseInt(job.variables.orderID);

    // Insert the 'ORDER_PRODUCTION_REQUIRED' status into the customer_order_status table
    dbConnection.query(
      'INSERT INTO customer_order_status (co_id, order_status) VALUES (?, ?)',
      [orderID, 'ORDER_PRODUCTION_REQUIRED'],
      (err, insertResults) => {
        if (err) {
          console.error('Error inserting order status:', err.message);
          dbConnection.end();
          return job.fail(err.message);
        }

        // Return the new order status to Zeebe as a variable
        const updateToBrokerVariables = {
          orderStatus: 'ORDER_PRODUCTION_REQUIRED',
        };

        // Close the database connection
        dbConnection.end((endError) => {
          if (endError) {
            console.error("Error closing MySQL connection:", endError.message);
            return job.fail(endError.message);
          }

          // Complete the Zeebe job and return the updated variables
          return job.complete(updateToBrokerVariables);
        });
      }
    );
  } catch (error) {
    console.error('Error handling the job:', error.message);
    return job.fail(error.message);
  }
}

module.exports = customerOrderStatusProductionRequired;