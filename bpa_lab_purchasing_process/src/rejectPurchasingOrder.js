const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

//External job worker to store customer order
const rejectPurchasingOrder = zbc.createWorker({
  taskType: 'rejectPurchasingOrder',
  taskHandler: handler,
  onReady: () => rejectPurchasingOrder.log('Job worker started successfully!')
});

function handler(job) {
  try {
        
    var db = mysql.createConnection({
      connectionLimit: 50,
      host: process.env.MYSQL_HOST_NAME,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE,
      port: process.env.MYSQL_HOST_PORT,
      server: 'localhost',
    });

    const purchasingOrderID = parseInt(job.variables.purchasingOrderID)
    console.log("Purchasing order ID from job variables is: ", purchasingOrderID)

    db.connect();

    db.query('UPDATE purchasing_order SET status = ? WHERE po_id = ?',
      ["PURCHASING_REJECTED", purchasingOrderID],
      (err, updateResults) => {
      if (err) {
        console.error('Error executing update query:', err.message);
        return;
      }
    });

  } catch (error) {
    console.log(error)
  }

  const updateToBrokerVariables = {
    purchasingStatus: 'PURCHASING_REJECTED',
  }
  console.log("Purchasing order has been rejected!");
  db.end();
  return job.complete(updateToBrokerVariables)
}

  module.exports = rejectPurchasingOrder;