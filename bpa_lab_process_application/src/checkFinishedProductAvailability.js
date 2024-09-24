const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

const checkFinishedProductAvailability = zbc.createWorker({
  taskType: 'checkFinishedProductAvailability',
  taskHandler: handler,
  onReady: () => checkFinishedProductAvailability.log('Job worker started successfully!')
})

async function handler(job) {
  try {
    let finishedProductQuantityAvailable;
    let customerOrderQuantity;

    const db = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE,
      port: process.env.MYSQL_HOST_PORT,      
      server: 'localhost',
    });

    // Query customer_order
    const customerOrderResults = await new Promise((resolve, reject) => {
      db.query('SELECT * FROM customer_order WHERE co_id = ?', [job.variables.orderID], (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from customer_order', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });

    console.log("\nCustomer product results: ", customerOrderResults);
    customerOrderQuantity = customerOrderResults[0].ordered_quantity;
    customerProductID = customerOrderResults[0].product_id;
    console.log("\nCustomer ordered quantity is: ", customerOrderQuantity);

    // Query finished_product_stock
    const productResults = await new Promise((resolve, reject) => {
      db.query('SELECT * FROM product_stock WHERE product_id = ?', [customerProductID], 
        (queryErr, results) => {
        if (queryErr) {
          console.error('Error selecting from finished_product_stock', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });

    console.log("\nProduct results: ", productResults);
    finishedProductQuantityAvailable = productResults[0].product_quantity;
    console.log("\nProduct quantity available: ", finishedProductQuantityAvailable);

    const result = checkStock(customerOrderQuantity, finishedProductQuantityAvailable);
    console.log("\nFinal results after checking stock: ", result);

    // Use updateToBrokerVariables as needed...
    const updateToBrokerVariables = {
      quantityNeededForProduction: result.quantityNeededForProduction,
      //finishedProductQuantityAvailable: result.finishedProductQuantityAvailable,
      productionRequired: result.productionRequired,
    };

    return job.complete(updateToBrokerVariables);
  } catch (error) {
    console.log("\nGot error:", error);
  }
}

function checkStock(customerOrderQuantity, finishedProductQuantityAvailable) {
  let productionRequired = "no";
  let quantityNeededForProduction = 0;

  // Check if stock is totally empty
  if (finishedProductQuantityAvailable === 0) {
    productionRequired = "yes";
    quantityNeededForProduction = customerOrderQuantity;
    console.log("\nProduct stock is empty. PRODUCTION STARTING...")
  }
  // Check if finishedProductQuantityAvailable is greater than customerOrderQuantity
  else if(finishedProductQuantityAvailable > customerOrderQuantity){
    productionRequired = "no";
    console.log("\nProduct stock is available. PREPARING FOR SHIPMENT...")
  }
  // Check if finishedProductQuantityAvailable is below customerOrderQuantity
  else if(customerOrderQuantity > finishedProductQuantityAvailable){
    quantityNeededForProduction = customerOrderQuantity - finishedProductQuantityAvailable
    productionRequired = "yes";
    console.log("\nProduct stock is below the customer's request. PRODUCTION STARTING...")
  }

  return {
    customerOrderQuantity,
    quantityNeededForProduction,
    productionRequired
  };
}

module.exports = checkFinishedProductAvailability;