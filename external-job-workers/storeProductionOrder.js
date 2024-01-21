const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient();

//External job worker to store customer order
const storeProductionOrder = zbc.createWorker({
  taskType: 'storeProductionOrder',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => storeProductionOrder.log('Job worker started successfully!')
});

function handler(job) {
  let productionDateTime ='';
  storeProductionOrder.log('\nTask variables', job.variables);

  // Accessing optional variables
  const optionalVariable = job.variables.optionalVariable;
  console.log('Optional variables: ', job.variables);

  var customerOrderConnection = mysql.createConnection({
    connectionLimit: 10,
    host: process.env.MYSQL_HOST_NAME,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE_PRODUCTION,
    port: process.env.MYSQL_HOST_PORT,
  });

  customerOrderConnection.connect();

  customerOrderConnection.query('INSERT INTO `production_order` (`productionOrderID`, `orderID`, `orderProduct`, `quantityNeededForProduction` ) VALUES (NULL, "' + + job.variables.orderID +'", "' + job.variables.customerProduct + '","' + job.variables.quantityNeededForProduction + '");', (err, insertResults, fields) => {
    if (err) {
        console.error('Error executing insert query:', err.message);
        return;
    }
    return job.complete()
});

}

module.exports = storeProductionOrder;
