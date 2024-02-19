// const ZB = require('zeebe-node')
// const mysql = require('mysql');

// const zbc = new ZB.ZBClient({
//   hostname: 'zeebe'
// });

// //External job worker to store customer order
// const storeCustomerOrder = zbc.createWorker({
//   taskType: 'storeCustomerOrder',
//   taskHandler: handler,
//   debug: true,
//   loglevel: 'INFO',
//   onReady: () => storeCustomerOrder.log('Job worker (Store) started successfully!')
// });

// function handler(job) {
//   let insertId = 0; // Declare insertId in the outer scope

//   storeCustomerOrder.log('Task variables', job.variables);

//   // Accessing optional variables
//   const optionalVariable = job.variables.optionalVariable;
//   console.log('Optional variables: ', job.variables);

//   var customerOrderConnection = mysql.createConnection({
//     connectionLimit: 10,
//     host: process.env.MYSQL_HOST_NAME,
//     user: process.env.MYSQL_USER,
//     password: process.env.MYSQL_PASSWORD,
//     database: process.env.MYSQL_DATABASE,
//     port: process.env.MYSQL_HOST_PORT,
//   });

//   customerOrderConnection.connect();

//   customerOrderConnection.query('INSERT INTO `purchasing_order` (`id`, `material`, `price`, `vendor`, `amount`, `approved`) VALUES (NULL, "' + job.variables.material_key +'", "' + job.variables.price_key +'", "' + job.variables.vendor_key + '", "' + job.variables.quantity_key  + '", "'+ job.variables.approve_key + '");', (err, results, fields) => {
//     if (err) {
//       console.error('Error executing query:', err.message);
//       return;
//     }

//     // Access the insertId from the callback
//     console.log("insertId:", results.insertId);
//     insertId = results.insertId;

//     // Now you can use insertId in the callback or pass it to another function

//     console.log("Results: ", JSON.stringify(results));

//     // Continue with your logic, e.g., call another function
//     updateToBrokerVariables();
//   });

//   function updateToBrokerVariables() {
//     const updateToBrokerVariables = {
//       orderID: insertId,
//       // optionalVariable: optionalVariable, // Add optional variables to the update object
//     };

//     customerOrderConnection.end(); // Close the customerOrderConnection when done

//     return job.complete(updateToBrokerVariables);
//   }
// }

// module.exports = storeCustomerOrder;