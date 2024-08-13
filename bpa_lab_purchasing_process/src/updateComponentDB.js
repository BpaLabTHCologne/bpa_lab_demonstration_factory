const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

const updateComponentDB = zbc.createWorker({
  taskType: 'updateComponentDB',
  taskHandler: handler,
  onReady: () => updateComponentDB.log('Job worker started successfully!')
})

async function handler(job) {
  try {

    //trobuleshooting
    console.log('Worker handling task. Job variables:', job.variables);

    let componentName;
    let componentQuantityAvailable;
    let orderProduct;
    let orderQuantity;

    const availableComponentsDBPool = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE_COMPONENTS,
      port: process.env.MYSQL_HOST_PORT,
      server: 'localhost',
    });

    const productionOrderDBPool = mysql.createPool({
        connectionLimit: 10,
        host: process.env.MYSQL_HOST_NAME,
        user: process.env.MYSQL_USER,
        password: process.env.MYSQL_PASSWORD,
        database: process.env.MYSQL_DATABASE_PRODUCTION,
        port: process.env.MYSQL_HOST_PORT,
        server: 'localhost',
    });

    const productionOrderResults = await new Promise((resolve, reject) => {
        productionOrderDBPool.query('SELECT * FROM `production_order` WHERE `production_order`.`orderID` = ?', [job.variables.orderID], (queryErr, results, fields) => {
          if (queryErr) {
            console.error('Error selecting from production_order ID error???', queryErr.message);
            reject(queryErr);
          } else {
            //troubleshooting
            console.log('Query successful. Results:', results);
            
            resolve(results);
          }
        });
    });

    orderProduct = productionOrderResults[0].customerProduct;
    orderQuantity = parseInt(job.variables.customerQuantity, 10); //productionOrderResults[0].quantityNeededForProduction;
    console.log("\nThe production order is: ", orderProduct);
    console.log("The quantity is: ", orderQuantity);

    // Looks only for the first components 
    
    if(orderProduct === "Mountain Bike") {
      componentName = "Mountain bike frame";
    }
    else if(orderProduct === "Hybrid 40000 Bicycle") {
      componentName = "Hybrid bicycle wheels";
    }
    else if(orderProduct === "Speed Thriller Electric 147 Bicycle") {
      componentName = "Electric bicycle frame"
    }

    //troubleshooting
    console.log('Executing update for component_stock');
    
    const componentResults = await new Promise((resolve, reject) => {
      availableComponentsDBPool.query('SELECT * FROM component_stock WHERE componentName = ?', [componentName], (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from component_stock', queryErr.message);
          reject(queryErr);
        } else {
          //troubleshooting
          console.log('Query successful. Results:', results);

          resolve(results);
        }
      });
    });

    componentQuantityAvailable = componentResults[0].componentQuantity;
    console.log("\Component name from component_stock: ", componentName);
    console.log("Component quantity available in component_stock: ", componentQuantityAvailable);

    componentQuantityAvailable = componentQuantityAvailable + job.variables.quantity_key

    availableComponentsDBPool.query('UPDATE component_stock SET componentQuantity = ? WHERE componentName = ?', [componentQuantityAvailable, componentName])

    // Use updateToBrokerVariables as needed
    const updateToBrokerVariables = {
      componentQuantityAvailable: componentQuantityAvailable
    };

    return job.complete(updateToBrokerVariables);
  } catch (error) {
    console.log("Got error:", error);
  }
}

module.exports = updateComponentDB;