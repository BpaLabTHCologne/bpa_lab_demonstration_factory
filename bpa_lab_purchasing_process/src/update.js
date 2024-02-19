const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

//External job worker to store customer order
const customerOrderStatusApproved = zbc.createWorker({
  taskType: 'updateCustomerOrder',
  taskHandler: handler,
  debug: true,
  loglevel: 'INFO',
  onReady: () => customerOrderStatusApproved.log('Job worker started successfully!')
});

function handler(job) {
	// customerOrderStatusApproved.log('Task variables', job.variables)
    try {
        const orderID = parseInt(job.variables.orderID)
        var connection = mysql.createConnection({
            connectionLimit: 10,
            host: process.env.MYSQL_HOST_NAME,
            user: process.env.MYSQL_USER,
            password: process.env.MYSQL_PASSWORD,
            database: process.env.MYSQL_DATABASE,
            port: process.env.MYSQL_HOST_PORT,
          });
    
        connection.connect();

       
    
        connection.query('UPDATE `purchasing_order` SET `approved` = "REJECTED" WHERE `purchasing_order`.`id`= '+orderID+ ';',
        async function (error, results, fields) {
            if (error) throw error;
            console.log('Results: ', JSON.stringify(results));
          });

         

    
        
    
      } catch (error) {
        console.log(error)
      }

      const updateToBrokerVariables = {
        orderStatus: 'ORDER_APPROVED',
	}
  connection.end();
	return job.complete(updateToBrokerVariables)
}

  module.exports = customerOrderStatusApproved;