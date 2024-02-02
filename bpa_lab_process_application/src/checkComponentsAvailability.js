const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

const checkComponentsAvailability = zbc.createWorker({
  taskType: 'checkComponentsAvailability',
  taskHandler: handler,
  onReady: () => checkComponentsAvailability.log('Job worker started successfully!')
})

// Get the process variables from the execution context
//var orderProduct = execution.getVariable("customerOrder");


async function handler(job) {
  try {
    //troubleshooting 1
    console.log('Worker handling task. Job variables:', job.variables);

    let componentName = 'Mountain bike';
    let componentQuantityAvailable;
    let orderProduct;
    let orderQuantity;

    //checkComponentsAvailability.log('Task variables', job.variables);

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
      database: process.env.MYSQL_DATABASE_PRODUCTION,
      port: process.env.MYSQL_HOST_PORT,
    });

    // Query component_stock

    //troubleshooting 2
    console.log('Executing query for component_stock');
    
    const componentResults = await new Promise((resolve, reject) => {
      availableComponentsDBPool.query('SELECT * FROM component_stock', (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from component_stock', queryErr.message);
          reject(queryErr);
        } else {
          //troubleshooting 3
          console.log('Query successful. Results:', results);

          resolve(results);
        }
      });
    });

    if (componentResults.length > 0) {
      componentName = componentResults[0].componentName;
      componentQuantityAvailable = componentResults[0].componentQuantity;
      console.log("\Component name from component_stock: ", componentName);
      console.log("Component quantity available in component_stock: ", componentQuantityAvailable);
    }

    // Query customer_order

    //troubleshooting 4
    console.log('Executing query for production_order');

    //change here customerOrderResults name to production order results!!!
    const customerOrderResults = await new Promise((resolve, reject) => {
      productionOrderDBPool.query('SELECT * FROM `production_order` WHERE `production_order`.`orderID` = ?', [job.variables.orderID], (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from production_order', queryErr.message);
          reject(queryErr);
        } else {
          //troubleshooting 5
          console.log('Query successful. Results:', results);
          
          resolve(results);
        }
      });
    });

    //this IF BLOCK doesn't work as wanted... 
    if (customerOrderResults.length > 0 && customerOrderResults[0].orderProduct !== undefined && customerOrderResults[0].quantity !== undefined) {
      orderProduct = customerOrderResults[0].orderProduct;
      orderQuantity = customerOrderResults[0].quantityNeededForProduction;
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
    console.log("Component name === Order product");
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

      availableComponentsDBPool.query('UPDATE component_stock SET productQuantity = ?', [quantityNeededToPurchase], (updateErr) => {
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


module.exports = checkComponentsAvailability;
