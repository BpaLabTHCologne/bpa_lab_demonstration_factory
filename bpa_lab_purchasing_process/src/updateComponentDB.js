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

    let componentName;
    let componentQuantityAvailable;
    let orderedProduct;
    let orderedQuantity;

    const db = mysql.createPool({
      connectionLimit: 10,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE,
      port: process.env.MYSQL_HOST_PORT,
      server: 'localhost',
    });

    const productIdQuery = await new Promise((resolve, reject) => {
      db.query('SELECT * FROM production_order WHERE co_id = ?', [job.variables.orderID], 
        (queryErr, results) => {
        if (queryErr) {
          console.error('Error selecting from production_order by co_id', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });

    productId = productIdQuery[0].product_id;

    const productResults = await new Promise((resolve, reject) => {
      db.query('SELECT * FROM product_stock WHERE product_id = ?', [productId], 
        (queryErr, results) => {
        if (queryErr) {
          console.error('Error selecting from production_order by co_id', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });

    orderedProduct = productResults[0].product_name;
    orderedQuantity = parseInt(job.variables.customerQuantity, 10);
    console.log("\nThe production order is: ", orderedProduct);
    console.log("The quantity is: ", orderedQuantity);

    // Looks only for the first components 
    
    if(orderedProduct === "Mountain Bike") {
      componentName = "Mountain bike frame";
    }
    else if(orderedProduct === "Hybrid 40000 Bicycle") {
      componentName = "Hybrid bicycle wheels";
    }
    else if(orderedProduct === "Speed Thriller Electric 147 Bicycle") {
      componentName = "Electric bicycle frame"
    }

    const componentResults = await new Promise((resolve, reject) => {
      db.query('SELECT * FROM component_stock WHERE component_name = ?', [componentName], (queryErr, results, fields) => {
        if (queryErr) {
          console.error('Error selecting from component_stock', queryErr.message);
          reject(queryErr);
        } else {
          resolve(results);
        }
      });
    });

    componentQuantityAvailable = componentResults[0].component_quantity;
    console.log("\Component name from component_stock: ", componentName);
    console.log("Component quantity available in component_stock: ", componentQuantityAvailable);

    componentQuantityAvailable = componentQuantityAvailable + job.variables.quantity_key

    db.query('UPDATE component_stock SET component_quantity = ? WHERE component_name = ?', [componentQuantityAvailable, componentName])

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