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
  // let purchasingOrderID = 0;

  storePurchasingOrder.log("\nTask variables", job.variables);

  // Accessing optional variables
  console.log("Optional variables: ", job.variables);

  var connection = mysql.createConnection({
    connectionLimit: 50,
    host: process.env.MYSQL_HOST_NAME,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE_PURCHASING,
    port: process.env.MYSQL_HOST_PORT,
    server: 'localhost',
  });

  connection.connect((error) => {
    if (error) {
      console.log(
        "storePurchasingOrder:: Error connecting to mysql database",
        error
      );
    }
  });

  connection.query('INSERT INTO `purchasing_order` (`purchasingOrderID`, `material`, `price`, `vendor`, `amount`, `approved`) VALUES (NULL, "' + job.variables.material_key +'", "' + job.variables.price_key + '","' + job.variables.vendor_key + '","' + job.variables.quantity_key + '","' + job.variables.approve_key + '");',
  (err, insertResults, fields) => {
    if (err) {
        console.error('Error executing insert query:', err.message);
        return;
    }

    // Access the insertId from the callback
    const insertId = insertResults.insertId;
    // console.log("\ninsertId:", insertId);

    // Now, perform the SELECT query using the insertId
    const updateToBrokerVariables = {
      purchasingOrderID: insertId,
    }
    return job.complete(updateToBrokerVariables)
});

}

module.exports = storePurchasingOrder;
