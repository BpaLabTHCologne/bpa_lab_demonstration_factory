const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

//External job worker to store customer order
const storeProductionOrder = zbc.createWorker({
  taskType: 'storeProductionOrder',
  taskHandler: handler,
  onReady: () => storeProductionOrder.log('Job worker started successfully!')
});

function handler(job) {

  var dbConnection = mysql.createConnection({
    connectionLimit: 50,
    host: process.env.MYSQL_HOST_NAME,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE,
    port: process.env.MYSQL_HOST_PORT,
    server: 'localhost',
  });

  dbConnection.connect((error) => {
    if(error){
      console.log("storeProductionOrder:: Error connecting to mysql database", error)
      return job.fail(error.message);
    }
  });

  // Retrieve the product_id based on the product name
  const productName = job.variables.customerProduct;

  dbConnection.query('SELECT product_id FROM product_stock WHERE product_name = ?', [productName], (err, productResults) => {
    if (err) {
      console.error('Error executing product query:', err.message);
      return job.fail(err.message); 
    }

    if (productResults.length === 0) {
      console.error('No product found with the name:', productName);
      return job.fail('Product not found');
    }

    const product_id = productResults[0].product_id;

    const co_id = job.variables.orderID;
    const production_quantity = job.variables.quantityNeededForProduction;

    dbConnection.query('INSERT INTO production_order (co_id, product_id, production_quantity) VALUES (?, ?, ?)',
      [co_id, product_id, production_quantity],
      (err, insertResults) => {
      if (err) {
          console.error('Error executing insert query:', err.message);
          return;
      }
      return job.complete()
    });
  });
}

module.exports = storeProductionOrder;
