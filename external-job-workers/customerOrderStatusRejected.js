const ZB = require('zeebe-node')
const mysql = require('mysql');
const nodemailer = require('nodemailer');

const zbc = new ZB.ZBClient();

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
        const updatedProperty = parseInt(job.variables.updatedProperty)
        var connection = mysql.createConnection({
            host: process.env.MYSQL_HOST_NAME,
            user: process.env.MYSQL_USER,
            password: process.env.MYSQL_PASSWORD,
            database: process.env.MYSQL_DATABASE,
            port: process.env.MYSQL_HOST_PORT,
        });
    
        connection.connect();
    
        connection.query('UPDATE `customer_order` SET `orderStatus` = "ORDER_REJECTED" WHERE `customer_order`.`id` = ' + updatedProperty + ';',
          async function (error, results, fields) {
            if (error) throw error;
            console.log('Results: ', JSON.stringify(results));
          });
    
        connection.end();
    
      } catch (error) {
        console.log(error)
      }

      const updateToBrokerVariables = {
		updatedProperty: 'newValue',
	}

	return job.complete(updateToBrokerVariables)
}

  module.exports = customerOrderStatusRejected;