const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient()

const componentsAvailability = zbc.createWorker({
  taskType: 'componentsAvailability',
  taskHandler: handler,
  onReady: () => componentsAvailability.log('Job worker started successfully!')
})

// Get the process variables from the execution context
//var orderProduct = execution.getVariable("customerOrder");


async function handler(job) {
  try {
    let componentName;
    let componentQuantityAvailable;
    let orderProduct;
    let orderQuantity;

    // componentsAvailability.log('Task variables', job.variables);

    const availableComponentsDBPool = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE_COMPONENTS,
      port: process.env.MYSQL_HOST_PORT,
    });

    const productionOrderDBPool = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE,
      port: process.env.MYSQL_HOST_PORT,
    });

    // Query components_stock
    const componentResults = await new Promise((resolve, reject) => {
      availableComponentsDBPool.query('SELECT * FROM components_stock', (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from components_stock', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });

    if (componentResults.length > 0) {
      componentName = componentResults[0].componentName;
      componentQuantityAvailable = componentResults[0].componentQuantity;
      console.log("\Component name from components_stock: ", componentName);
      console.log("Component quantity available in components_stock: ", componentQuantityAvailable);
    }

    // Query customer_order
    const customerOrderResults = await new Promise((resolve, reject) => {
      productionOrderDBPool.query('SELECT * FROM `customer_order` WHERE `customer_order`.`id` = ?', [job.variables.orderID], (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from customer_order', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });

    if (customerOrderResults.length > 0 && customerOrderResults[0].product !== undefined && customerOrderResults[0].quantity !== undefined) {
      orderProduct = customerOrderResults[0].product;
      orderQuantity = customerOrderResults[0].quantity;
      console.log("\nThe production order is: ", orderProduct);
      console.log("The quantity is: ", orderQuantity);
    } else {
      console.log('No results found for the specified order ID or product/quantity is undefined.');
    }

    const result = checkStock(componentName, componentQuantityAvailable, orderProduct, orderQuantity);
    console.log("\nReturned result: ", result);

    // Use updateToBrokerVariables as needed...
    const updateToBrokerVariables = {
      quantityNeededToPurchase: result.quantityNeededToPurchase,
      componentQuantityAvailable: result.componentQuantityAvailable,
      purchasingRequired: result.purchasingRequired,
    };

    return job.complete(updateToBrokerVariables);
  } catch (error) {
    console.log("Got error:", error);
  }
}


function checkStock(componentName, componentQuantityAvailable, orderProduct, orderQuantity) {
  let quantityNeededToPurchase = 0
  let purchasingRequired = "";
  if (componentName === orderProduct) {
    if (componentQuantityAvailable >= orderQuantity) {
      // Update finished product stock
      quantityNeededToPurchase = componentQuantityAvailable - orderQuantity;
      console.log("\nNew stock after deductions: ", quantityNeededToPurchase);
      purchasingRequired = "no";
      const availableComponentsDBPool = mysql.createPool({
        connectionLimit: 10,
        host: process.env.MYSQL_HOST_NAME,
        user: process.env.MYSQL_USER,
        password: process.env.MYSQL_PASSWORD,
        database: process.env.MYSQL_DATABASE_FINISHED_PRODUCT,
        port: process.env.MYSQL_HOST_PORT,
      });

      availableComponentsDBPool.query('UPDATE components_stock SET productQuantity = ?', [quantityNeededToPurchase], (updateErr) => {
        if (updateErr) {
          console.error('Error updating finished product stock:', updateErr.message);
        }

        // Check if stock is getting low
        if (quantityNeededToPurchase <= 0) {
          console.log("\nProduct stock is getting low. Please restock your warehouse!");
        }
      });
      return {
        quantityNeededToPurchase,
        componentQuantityAvailable,
        purchasingRequired,
      };
    } else if (componentQuantityAvailable < orderQuantity) {
      quantityNeededToPurchase = orderQuantity - componentQuantityAvailable;
      console.log("\nSeems like the finished product quantity is below the customer's order quantity. Please restock your finished product quantity. Number of bicycles needed for production:", quantityNeededToPurchase);
      purchasingRequired = "yes";
      return {
        quantityNeededToPurchase,
        componentQuantityAvailable,
        purchasingRequired,
      };
    } else {
      // Not enough quantity in stock
      console.log("\nSorry, the requested product is not available in sufficient quantity inside the stock at the moment.");
    }
  } else {
    // Product mismatch or does not exist
    console.log("\nSorry, the selected product does not exist!");
    return {
      quantityNeededToPurchase,
      componentQuantityAvailable,
      purchasingRequired,
    };
  }
}


module.exports = componentsAvailability;
