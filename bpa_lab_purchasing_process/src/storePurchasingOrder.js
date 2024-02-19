const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

//External job worker to store customer order
const storePurchasingOrder = zbc.createWorker({
  taskType: 'storePurchasingOrder',
  taskHandler: handler,
  // debug: true,
  // loglevel: 'INFO',
  onReady: () => storePurchasingOrder.log('Job worker started successfully!')
});

function handler(job) {
  storePurchasingOrder.log('\nTask variables', job.variables);

  // Accessing optional variables
  // const optionalVariable = job.variables.optionalVariable;
  console.log('Optional variables: ', job.variables);

  var customerOrderConnection = mysql.createConnection({
    connectionLimit: 50,
    host: process.env.MYSQL_HOST_NAME,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE_PURCHASING,
    port: process.env.MYSQL_HOST_PORT,
  });

  customerOrderConnection.connect();

  customerOrderConnection.query('INSERT INTO `purchasing_order` (`purchasingOrderID`, `material`, `price`, `vendor`, `amount`, `approved`) VALUES (NULL, "' + job.variables.material_key +'", "' + job.variables.price_key + '","' + job.variables.vendor_key + '","' + job.variables.quantity_key + '","' + job.variables.approve_key + '");', (err, insertResults, fields) => {
    if (err) {
        console.error('Error executing insert query:', err.message);
        return;
    }
    return job.complete()
});

}

module.exports = storePurchasingOrder;
