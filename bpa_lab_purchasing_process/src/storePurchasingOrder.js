const ZB = require("zeebe-node");
const mysql = require("mysql");

const zbc = new ZB.ZBClient({
  hostname: "zeebe",
});

//External job worker to store customer order
const storePurchasingOrder = zbc.createWorker({
  taskType: "storePurchasingOrder",
  taskHandler: handler,
  onReady: () => storePurchasingOrder.log("Job worker started successfully!"),
});

function handler(job) {
  let insertId = 0; // Declare insertId in the outer scope

  storePurchasingOrder.log("\nTask variables", job.variables);

  // Accessing optional variables
  console.log("Optional variables: ", job.variables);

  var db = mysql.createConnection({
    connectionLimit: 50,
    host: process.env.MYSQL_HOST_NAME,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE,
    port: process.env.MYSQL_HOST_PORT,
    server: 'localhost',
  });

  db.connect((error) => {
    if (error) {
      console.log(
        "storePurchasingOrder:: Error connecting to mysql database",
        error
      );
    }
  });

  db.query('SELECT component_id FROM product_stock WHERE product_name = ?', [job.varables.customerProduct], 
    (err, productResults) => {
    if (err) {
      console.error('Error executing product query:', err.message);
      return job.fail(err.message); 
    }

    const componentId = productResults[0].component_id;

    db.query('SELECT vendor_id FROM vendor WHERE vendor_name = ?', [job.varables.vendor_key], 
      (err, vendorResults) => {
      if (err) {
        console.error('Error executing product query:', err.message);
        return job.fail(err.message); 
      }

    const vendorId = vendorResults[0].vendor_id;


      db.query('INSERT INTO purchasing_order (component_id, vendor_id, price, purchasing_quantity, approved) VALUES (?, ?, ?, ?, ?)',
      [componentId, vendorId, job.variables.price_key, job.variables.quantity_key, approve_key],
      (err, insertResults) => {
        if (err) {
            console.error('Error executing insert query:', err.message);
            return;
        }

          // Access the insertId from the callback
          const insertId = insertResults.insertId;

          // Now, perform the SELECT query using the insertId
          const updateToBrokerVariables = {
            purchasingOrderID: insertId,
          }
          return job.complete(updateToBrokerVariables)
      });
    });
  });

}

module.exports = storePurchasingOrder;
