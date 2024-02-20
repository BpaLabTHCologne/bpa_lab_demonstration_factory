const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});
let purchasingStatus = '';

//External job worker to store customer order
const rejectPurchasingOrder = zbc.createWorker({
  taskType: 'rejectPurchasingOrder',
  taskHandler: handler,
  onReady: () => rejectPurchasingOrder.log('Job worker started successfully!')
});

function handler(job) {
	// rejectPurchasingOrder.log('Task variables', job.variables)
  purchasingStatus = job.variables.approve_key;
    try {
        const purchasingOrderID = parseInt(job.variables.purchasingOrderID)
        console.log("Purchasing order ID from job variables is: ", purchasingOrderID)
        var connection = mysql.createConnection({
          connectionLimit: 50,
          host: process.env.MYSQL_HOST_NAME,
          user: process.env.MYSQL_USER,
          password: process.env.MYSQL_PASSWORD,
          database: process.env.MYSQL_DATABASE_PURCHASING,
          port: process.env.MYSQL_HOST_PORT,
        });
    
        connection.connect();
    
        connection.query('UPDATE `purchasing_order` SET `approved` = "PURCHASING_REJECTED" WHERE `purchasing_order`.`purchasingOrderID` = ' + purchasingOrderID + ';',
          async function (error, results, fields) {
            if (error) throw error;
            console.log('Results: ', JSON.stringify(results));
          });
      } catch (error) {
        console.log(error)
      }

      const updateToBrokerVariables = {
        purchasingStatus: 'PURCHASING_REJECTED',
      }
  console.log("Purchasing order has been approved!");
  connection.end();
	return job.complete(updateToBrokerVariables)
}

  module.exports = rejectPurchasingOrder;