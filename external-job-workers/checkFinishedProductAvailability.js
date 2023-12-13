const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient()

const checkFinishedProductAvailability = zbc.createWorker({
  taskType: 'checkFinishedProductAvailability',
  taskHandler: handler,
})

async function handler(job) {
  try {
    let finishedProductName = 0;
    let finishedProductQuantity = 0;
    let customerOrderProduct = 0;
    let customerOrderQuantity = 0;

    checkFinishedProductAvailability.log('Task variables', job.variables);

    // Task worker business logic goes here

    const finishedProductDBPool = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE_FINISHED_PRODUCT,
      port: process.env.MYSQL_HOST_PORT,
    });

    const customerOrderDBPool = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE,
      port: process.env.MYSQL_HOST_PORT,
    });

    // Use the finishedProductDBPool for queries
    finishedProductDBPool.query('SELECT * FROM finished_product_stock', (queryErr, results, fields) => {
      if (queryErr) {
        console.error('Error selecting from finished_product_stock', queryErr.message);
      } else {
        finishedProductName = results[0].productName;
        finishedProductQuantity = results[0].productQuantity;
        console.log("The product name is: ", finishedProductName);
        console.log("The product quantity is: ", finishedProductQuantity);
        console.log('Query results:', results);
      }

      // Release the connection back to the finishedProductDBPool

    });

    customerOrderDBPool.query('SELECT * FROM `customer_order` WHERE `customer_order`.`id` = ?', [job.variables.orderID], (queryErr, results, fields) => {
      if (queryErr) {
        console.error('Error selecting from customer_order', queryErr.message);
      } else if (results.length > 0 && results[0].product !== undefined && results[0].quantity !== undefined) {
        // Check if the result array is not empty and 'product' and 'quantity' are defined
        customerOrderProduct = results[0].product;
        customerOrderQuantity = results[0].quantity;
        console.log("The customer ordered product is: ", customerOrderProduct);
        console.log("The customer ordered quantity is: ", customerOrderQuantity);
        console.log('Query results:', results);
      } else {
        console.log('No results found for the specified order ID or product/quantity is undefined.');
      }

      checkStock(finishedProductName, finishedProductQuantity, customerOrderProduct, customerOrderQuantity);
      // Release the connection back to the customerOrderDBPool
    });


    const updateToBrokerVariables = {
      finishedProductQuantity: finishedProductQuantity,
    }

    return job.complete(updateToBrokerVariables)
  }
  catch (error) {
    console.log("Got error:", error)
  }

}

function checkStock(finishedProductName, finishedProductQuantity, customerOrderProduct, customerOrderQuantity) {
  if (finishedProductName === customerOrderProduct) {
    if (finishedProductQuantity >= customerOrderQuantity) {
      // Update finished product stock
      const updatedQuantity = finishedProductQuantity - customerOrderQuantity;
      console.log("New stock after deductions: ", updatedQuantity)
      const finishedProductDBPool = mysql.createPool({
        connectionLimit: 10,
        host: process.env.MYSQL_HOST_NAME,
        user: process.env.MYSQL_USER,
        password: process.env.MYSQL_PASSWORD,
        database: process.env.MYSQL_DATABASE_FINISHED_PRODUCT,
        port: process.env.MYSQL_HOST_PORT,
      });

      finishedProductDBPool.query('UPDATE finished_product_stock SET productQuantity = ?', [updatedQuantity], (updateErr) => {
        if (updateErr) {
          console.error('Error updating finished product stock:', updateErr.message);
          return;
        }

        // Check if stock is getting low
        if (updatedQuantity <= 0) {
          console.log("Product stock is getting low. Please restock your warehouse!");
        }
      });
    } else {
      // Not enough quantity in stock
      console.log("Sorry, the requested product is not available in sufficient quantity inside the stock at the moment.");
    }
  } else {
    // Product mismatch or does not exist
    console.log("Sorry, the selected product does not exist!");
  }
}

// Example usage

// var finishedProductConnection = mysql.createConnection({
//   connectionLimit: 10,
//   host: process.env.MYSQL_HOST_NAME,
//   user: process.env.MYSQL_USER,
//   password: process.env.MYSQL_PASSWORD,
//   database: process.env.MYSQL_DATABASE_FINISHED_PRODUCT,
//   port: process.env.MYSQL_HOST_PORT,
// });

// finishedProductConnection.connect();

// finishedProductConnection.connect((err) => {
//   if (err) {
//     console.error('Error connecting to finished product database:', err.message);
//     return;
//   }
//   console.log('Connected to finished product database');

//   // Select everything from finished_product_stock
//   finishedProductConnection.query('SELECT * FROM finished_product_stock', (queryErr, results, fields) => {
//     if (queryErr) {
//       console.error('Error executing query:', queryErr.message);
//       return;
//     }

//     // Process the results
//     console.log('Query results:', results);

//     // When done, close the connection
//     finishedProductConnection.end((endErr) => {
//       if (endErr) {
//         console.error('Error closing finished product database connection:', endErr.message);
//       } else {
//         console.log('Finished product database connection closed');
//       }
//     });
//   });
// });

module.exports = checkFinishedProductAvailability;
