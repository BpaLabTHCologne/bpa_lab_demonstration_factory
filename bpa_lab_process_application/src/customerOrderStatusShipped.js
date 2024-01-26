const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

//External job worker to store customer order
const customerOrderStatusShipped = zbc.createWorker({
  taskType: 'customerOrderStatusShipped',
  taskHandler: handler,
  debug: true,
  loglevel: 'INFO',
  onReady: () => customerOrderStatusShipped.log('Job worker started successfully!')
});

function handler(job) {
	// customerOrderStatusShipped.log('Task variables', job.variables)
    try {
        const orderID = parseInt(job.variables.orderID)
        var connection = mysql.createConnection({
            connectionLimit: 50,
            host: process.env.MYSQL_HOST_NAME,
            user: process.env.MYSQL_USER,
            password: process.env.MYSQL_PASSWORD,
            database: process.env.MYSQL_DATABASE,
            port: process.env.MYSQL_HOST_PORT,
        });
    
        connection.connect();
    
        connection.query('UPDATE `customer_order` SET `orderStatus` = "ORDER_IN_PRODUCTION" WHERE `customer_order`.`id` = ' + orderID + ';',
          async function (error, results, fields) {
            if (error) throw error;
            console.log('Results: ', JSON.stringify(results));
          });
    
        
    
      } catch (error) {
        console.log(error)
      }

      const updateToBrokerVariables = {
        orderStatus: 'ORDER_SHIPPED',
	}
  connection.end();
	return job.complete(updateToBrokerVariables)
}

  module.exports = customerOrderStatusShipped;