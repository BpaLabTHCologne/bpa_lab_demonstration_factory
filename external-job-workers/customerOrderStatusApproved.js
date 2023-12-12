const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient();

//External job worker to store customer order
const customerOrderStatusApproved = zbc.createWorker({
  taskType: 'customerOrderStatusApproved',
  taskHandler: handler,
  debug: true,
  loglevel: 'INFO',
  onReady: () => customerOrderStatusApproved.log('Job worker started successfully!')
});

function handler(job) {
	customerOrderStatusApproved.log('Task variables', job.variables)
    try {
        const orderID = parseInt(job.variables.orderID)
        var connection = mysql.createConnection({
            host: process.env.MYSQL_HOST_NAME,
            user: process.env.MYSQL_USER,
            password: process.env.MYSQL_PASSWORD,
            database: process.env.MYSQL_DATABASE,
            port: process.env.MYSQL_HOST_PORT,
        });
    
        connection.connect();
    
        connection.query('UPDATE `customer_order` SET `orderStatus` = "ORDER_APPROVED" WHERE `customer_order`.`id` = ' + orderID + ';',
          async function (error, results, fields) {
            if (error) throw error;
            console.log('Results: ', JSON.stringify(results));
          });
    
        connection.end();
    
      } catch (error) {
        console.log(error)
      }

      const updateToBrokerVariables = {
        orderStatus: 'ORDER_REJECTED',
	}

	return job.complete(updateToBrokerVariables)
}

  module.exports = customerOrderStatusApproved;