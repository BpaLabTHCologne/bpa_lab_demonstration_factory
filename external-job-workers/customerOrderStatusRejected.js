// const ZB = require('zeebe-node')
// const mysql = require('mysql');
// const nodemailer = require('nodemailer');
// const fs = require('fs');


// const zbc = new ZB.ZBClient();

// //External job worker to store customer order
// const customerOrderStatusRejected = zbc.createWorker({
//   taskType: 'customerOrderStatusRejected',
//   taskHandler: handler,
//   debug: true,
//   loglevel: 'INFO',
//   onReady: () => customerOrderStatusRejected.log('Job worker started successfully!')
// });

// function handler(job) {
//   customerOrderStatusRejected.log('Task variables', job.variables);

//   // Accessing optional variables
//   const optionalVariable = job.variables.optionalVariable;
//   console.log('Optional variables: ', job.variables);

//   try{
//         const userId = parseInt(job.variables.get('userId'))
//         const orderId = parseInt(job.variables.get('orderId'))
//         var connection = mysql.createConnection({
//         host: process.env.MYSQL_HOST_NAME,
//         user: process.env.MYSQL_USER,
//         password: process.env.MYSQL_PASSWORD,
//         database: process.env.MYSQL_DATABASE
//       });
  
//       connection.connect();

//       connection.query('UPDATE `customer_order` SET `orderStatus` = "ORDER_REJECTED" WHERE `customer_order`.`id` = ' + orderId + ';',
//         function (error, results, fields) {
//           if (error) throw error;
//           console.log('Results: ', JSON.stringify(results));
//         });
  
//       connection.end();

//   }
//   catch (error) {
//     logger.error(error)
//   }

//   return job.complete(updateToBrokerVariables);
// }


// // connection.query('SELECT * from customer_order', function(err, rows, fields) {
// //     if(err) console.log(err);
// //     console.log('The solution is: ', rows);
// //     connection.end();
// // });

// // connection.query('INSERT INTO `customer_order` (`id`, `name`, `email`, `phone`, `address`, `product`, `quantity`, `orderStatus`) VALUES (NULL, "' + job.variables.name +'", "' + job.variables.email +'", "'+ job.variables.phone +'", "' + job.variables.address + '", "' + job.variables.product + '", "'+ job.variables.quantity + '", "");', (err, results, fields) => {
// //   if (err) {
// //     console.error('Error executing query:', err.message);
// //     return;
// //   }
// //   console.log('Query results:', results);
// // });

// //   const updateToBrokerVariables = {
// //     updatedProperty: 'newValue',
// //     optionalVariable: optionalVariable, // Add optional variables to the update object
// //   };

// //   return job.complete(updateToBrokerVariables);
// // }

// // module.exports = customerOrderStatusRejected;

// // Update customer order status to REJECTED
// client.subscribe("updateCustomerOrderStatusRejected", async function ({ task, taskService }) {
//     console.log("Updating Customer order status to ORDER_REJECTED.....", task.variables.getAll())
//     try {
//       const userId = parseInt(task.variables.get('userId'))
//       const orderId = parseInt(task.variables.get('orderId'))
//       var connection = mysql.createConnection({
//         host: process.env.MYSQL_HOST_NAME,
//         user: process.env.MYSQL_USER,
//         password: process.env.MYSQL_PASSWORD,
//         database: process.env.MYSQL_DATABASE
//       });
  
//       connection.connect();
  
//       // connection.query('UPDATE `customer_order` SET `orderStatus` = "ORDER_REJECTED" WHERE `customer_order`.`id` = ' + userId + ';',
//       // function (error, results, fields) {
//       //   if (error) throw error;
//       //   console.log('Results: ', JSON.stringify(results));
//       // });
  
//       connection.query('UPDATE `customer_order` SET `orderStatus` = "ORDER_REJECTED" WHERE `customer_order`.`id` = ' + orderId + ';',
//         function (error, results, fields) {
//           if (error) throw error;
//           console.log('Results: ', JSON.stringify(results));
//         });
  
//       connection.end();
  
//     } catch (error) {
//       logger.error(error)
//     }
  
  
//     await taskService.complete(task);
//   });