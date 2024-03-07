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
    let finishedProductName;
    let finishedProductQuantityAvailable;
    let customerOrderProduct;
    let customerOrderQuantity;

    // checkFinishedProductAvailability.log('Task variables', job.variables);

    const finishedProductDBPool = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE_FINISHED_PRODUCT,
      port: process.env.MYSQL_HOST_PORT,      
      server: 'localhost',
    });

    const customerOrderDBPool = mysql.createPool({
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
      customerOrderDBPool.query('SELECT * FROM `customer_order` WHERE `customer_order`.`id` = ?', [job.variables.orderID], (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from customer_order', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });
    console.log("\nCustomer product results: ", customerOrderResults);
    customerOrderProduct = customerOrderResults[0].product;
    customerOrderQuantity = customerOrderResults[0].quantity;
    console.log("\nCustomer ordered product is: ", customerOrderProduct);
    console.log("\nCustomer ordered quantity is: ", customerOrderQuantity);

    // Query finished_product_stock
    const finishedProductResults = await new Promise((resolve, reject) => {
      finishedProductDBPool.query('SELECT * FROM finished_product_stock WHERE productName = ?', [customerOrderProduct], (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from finished_product_stock', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });
    console.log("\nFinished product results: ", finishedProductResults);
    finishedProductName = finishedProductResults[0].productName;
    finishedProductQuantityAvailable = finishedProductResults[0].productQuantity;
    console.log("\nFinished product found same as what customer ordered: ", finishedProductName);
    console.log("\nFinished product quantity available: ", finishedProductQuantityAvailable);

    const result = checkStock(customerOrderProduct, customerOrderQuantity, finishedProductName, finishedProductQuantityAvailable);
    console.log("\nFinal results after checking stock: ", result);

    // Use updateToBrokerVariables as needed...
    const updateToBrokerVariables = {
      quantityNeededForProduction: result.quantityNeededForProduction,
      finishedProductQuantityAvailable: result.finishedProductQuantityAvailable,
      productionRequired: result.productionRequired,
    };

    return job.complete(updateToBrokerVariables);
  } catch (error) {
    console.log("\nGot error:", error);
  }
}

function checkStock(customerOrderProduct, customerOrderQuantity, finishedProductName, finishedProductQuantityAvailable) {
  let productionRequired = "no";
  let quantityNeededForProduction = 0;

  // Check if stock is totally empty
  if (finishedProductQuantityAvailable === 0) {
      productionRequired = "yes";
      quantityNeededForProduction = customerOrderQuantity;
      console.log("\nFinished product stock is empty. PRODUCTION STARTING...")
  }
  // Check if finishedProductQuantityAvailable is greater than customerOrderQuantity
  else if(finishedProductQuantityAvailable > customerOrderQuantity){
    finishedProductQuantityAvailable = finishedProductQuantityAvailable - customerOrderQuantity;
    const finishedProductDBPool = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE_FINISHED_PRODUCT,
      port: process.env.MYSQL_HOST_PORT,
    });
    finishedProductDBPool.query('UPDATE finished_product_stock SET productQuantity = ? WHERE productName = ?', [finishedProductQuantityAvailable, finishedProductName])
    productionRequired = "no";
    console.log("\nFinished product stock is available. PREPARING FOR SHIPMENT...")
  }
  // Check if finishedProductQuantityAvailable is below customerOrderQuantity
  else if(customerOrderQuantity > finishedProductQuantityAvailable){
    quantityNeededForProduction = customerOrderQuantity - finishedProductQuantityAvailable
    productionRequired = "yes";
    console.log("\nFinished product stock is below the customer's request. PRODUCTION STARTING...")
  }

  return {
    customerOrderProduct,
    customerOrderQuantity,
    finishedProductName,
    finishedProductQuantityAvailable,
    quantityNeededForProduction,
    productionRequired
  };
}

module.exports = checkFinishedProductAvailability;
