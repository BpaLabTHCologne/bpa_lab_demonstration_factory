const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

//External job worker to store customer order
const customerOrderStatusRejected = zbc.createWorker({
  taskType: 'customerOrderStatusRejected',
  taskHandler: handler,
  debug: true,
  loglevel: 'INFO',
  onReady: () => customerOrderStatusRejected.log('Job worker started successfully!')
});

function handler(job) {
	customerOrderStatusRejected.log('Task variables', job.variables)
    try {
        const orderID = parseInt(job.variables.orderID)
        var connection = mysql.createConnection({
            host: process.env.MYSQL_HOST_NAME,
            user: process.env.MYSQL_USER,
            password: process.env.MYSQL_PASSWORD,
            database: process.env.MYSQL_DATABASE,
            port: process.env.MYSQL_HOST_PORT,
            server: 'localhost',
        });
    
        connection.connect();
    
        connection.query('UPDATE `customer_order` SET `orderStatus` = "ORDER_REJECTED" WHERE `customer_order`.`id` = ' + orderID + ';',
          async function (error, results, fields) {
            if (error) throw error;
            console.log('Results: ', JSON.stringify(results));
          });
    
        
    
      } catch (error) {
        console.log(error)
      }

      const updateToBrokerVariables = {
        orderStatus: 'ORDER_REJECTED',
	}
  connection.end();
	return job.complete(updateToBrokerVariables)
}

  module.exports = customerOrderStatusRejected;