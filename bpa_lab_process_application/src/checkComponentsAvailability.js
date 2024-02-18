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

    let componentName;
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

    console.log('Executing query for production_order');

    const productionOrderResults = await new Promise((resolve, reject) => {
      productionOrderDBPool.query('SELECT * FROM `production_order` WHERE `production_order`.`orderID` = ?', [job.variables.orderID], (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from production_order ID error???', queryErr.message);
          reject(queryErr);
        } else {
          //troubleshooting 5
          console.log('Query successful. Results:', results);
          
          resolve(results);
        }
      });
    });

    orderProduct = productionOrderResults[0].customerProduct;
    orderQuantity = productionOrderResults[0].quantityNeededForProduction;
    console.log("\nThe production order is: ", orderProduct);
    console.log("The quantity is: ", orderQuantity);

    // Now we have to look for the components of this product. Mountain Bike -> frame, seat, wheel...

    if(orderProduct === "Mountain Bike") {
      componentName = "Mountain bike frame";
    }
    else;

    // delete this if block???
    /*
    if (componentResults.length > 0) {
      componentName = componentResults[0].componentName;
      componentQuantityAvailable = componentResults[0].componentQuantity;
      console.log("\Component name from component_stock: ", componentName);
      console.log("Component quantity available in component_stock: ", componentQuantityAvailable);
    }
    */
    


    // Query customer_order

    //troubleshooting 4

    console.log('Executing query for component_stock');
    
    const componentResults = await new Promise((resolve, reject) => {
      availableComponentsDBPool.query('SELECT * FROM component_stock WHERE componentName = ?', [componentName], (queryErr, results, fields) => {
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

    //componentName = componentResults[0].componentName; //??
    componentQuantityAvailable = componentResults[0].componentQuantity;
    console.log("\Component name from component_stock: ", componentName);
    console.log("Component quantity available in component_stock: ", componentQuantityAvailable);


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

//it seems to be ok until this point

function checkStock(componentName, componentQuantityAvailable, orderProduct, orderQuantity) {
  let quantityNeededToPurchase = 0;
  let purchasingRequired = "";
  //componentName = "Mountain Bike";
  //orderProduct = "Mountain Bike";

  if(componentQuantityAvailable === 0) {
    purchasingRequired = "yes";
    quantityNeededToPurchase = orderQuantity;
    console.log("\nComponent stock is empty! Purchasing needed.");
  }
  else if(componentQuantityAvailable >= orderQuantity) {
    console.log("\nComponent stock available. Production starting...");
    purchasingRequired = "no";
  }
  else if(componentQuantityAvailable < orderQuantity) {
    console.log("\nNot enough components in the stock! Purchasing needed.");
  }

  return {
    componentName,
    orderQuantity,
    orderProduct,
    componentQuantityAvailable,
    orderQuantity,
    purchasingRequired
  };
}

module.exports = checkComponentsAvailability;
