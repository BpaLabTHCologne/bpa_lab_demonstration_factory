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
    });

    const customerOrderDBPool = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE,
      port: process.env.MYSQL_HOST_PORT,
    });

    // Query finished_product_stock
    const finishedProductResults = await new Promise((resolve, reject) => {
      finishedProductDBPool.query('SELECT * FROM finished_product_stock', (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from finished_product_stock', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });

    if (finishedProductResults.length > 0) {
      finishedProductName = finishedProductResults[0].productName;
      finishedProductQuantityAvailable = finishedProductResults[0].productQuantity;
      console.log("\nProduct name from finished_product_stock: ", finishedProductName);
      console.log("Product quantity available in finished_product_stock: ", finishedProductQuantityAvailable);
    }

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

    if (customerOrderResults.length > 0 && customerOrderResults[0].product !== undefined && customerOrderResults[0].quantity !== undefined) {
      customerOrderProduct = customerOrderResults[0].product;
      customerOrderQuantity = customerOrderResults[0].quantity;
      console.log("\nThe customer ordered product is: ", customerOrderProduct);
      console.log("The customer ordered quantity is: ", customerOrderQuantity);
    } else {
      console.log('No results found for the specified order ID or product/quantity is undefined.');
    }

    const result = checkStock(finishedProductName, finishedProductQuantityAvailable, customerOrderProduct, customerOrderQuantity);
    console.log("\nReturned result: ", result);

    // Use updateToBrokerVariables as needed...
    const updateToBrokerVariables = {
      quantityNeededForProduction: result.quantityNeededForProduction,
      finishedProductQuantityAvailable: result.finishedProductQuantityAvailable,
      productionRequired: result.productionRequired,
    };

    return job.complete(updateToBrokerVariables);
  } catch (error) {
    console.log("Got error:", error);
  }
}


function checkStock(finishedProductName, finishedProductQuantityAvailable, customerOrderProduct, customerOrderQuantity) {
  let quantityNeededForProduction = 0
  let productionRequired = "";
  if (finishedProductName === customerOrderProduct) {
    if (finishedProductQuantityAvailable >= customerOrderQuantity) {
      // Update finished product stock
      quantityNeededForProduction = finishedProductQuantityAvailable - customerOrderQuantity;
      console.log("\nNew stock after deductions: ", quantityNeededForProduction);
      productionRequired = "no";
      const finishedProductDBPool = mysql.createPool({
        connectionLimit: 10,
        host: process.env.MYSQL_HOST_NAME,
        user: process.env.MYSQL_USER,
        password: process.env.MYSQL_PASSWORD,
        database: process.env.MYSQL_DATABASE_FINISHED_PRODUCT,
        port: process.env.MYSQL_HOST_PORT,
      });

      finishedProductDBPool.query('UPDATE finished_product_stock SET productQuantity = ?', [quantityNeededForProduction], (updateErr) => {
        if (updateErr) {
          console.error('Error updating finished product stock:', updateErr.message);
        }

        // Check if stock is getting low
        if (quantityNeededForProduction <= 0) {
          console.log("\nProduct stock is getting low. Please restock your warehouse!");
        }
      });
      return {
        quantityNeededForProduction,
        finishedProductQuantityAvailable,
        productionRequired,
      };
    } else if (finishedProductQuantityAvailable < customerOrderQuantity) {
      quantityNeededForProduction = customerOrderQuantity - finishedProductQuantityAvailable;
      console.log("\nSeems like the finished product quantity is below the customer's order quantity. Please restock your finished product quantity. Number of bicycles needed for production:", quantityNeededForProduction);
      productionRequired = "yes";
      return {
        quantityNeededForProduction,
        finishedProductQuantityAvailable,
        productionRequired,
      };
    } else {
      // Not enough quantity in stock
      console.log("\nSorry, the requested product is not available in sufficient quantity inside the stock at the moment.");
    }
  } else {
    // Product mismatch or does not exist
    console.log("\nSorry, the selected product does not exist!");
    return {
      quantityNeededForProduction,
      finishedProductQuantityAvailable,
      productionRequired,
    };
  }
}


module.exports = checkFinishedProductAvailability;