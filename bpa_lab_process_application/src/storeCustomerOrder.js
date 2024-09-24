const ZB = require('zeebe-node')
const mysql = require('mysql');

const zbc = new ZB.ZBClient({
  hostname: 'zeebe'
});

//External job worker to store customer order
const storeCustomerOrder = zbc.createWorker({
  taskType: 'storeCustomerOrder',
  taskHandler: handler,
  onReady: () => storeCustomerOrder.log('Job worker started successfully!')
});

function handler(job) {
  let orderDateTime = '';

  // Establishing MySQL connection
  var dbConnection = mysql.createConnection({
    connectionLimit: 50,
    host: process.env.MYSQL_HOST_NAME,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE,
    port: process.env.MYSQL_HOST_PORT,
    server: 'localhost',
  });

 dbConnection.connect((error) => {
    if(error){
      console.log("storeCustomerOrder:: Error connecting to mysql database", error)
      return job.fail(error.message);
    }
  });


  // Retrieve the product_id based on the product name
  const productName = job.variables.customerProduct;

  dbConnection.query('SELECT product_id FROM product_stock WHERE product_name = ?', [productName], (err, productResults) => {
    if (err) {
      console.error('Error executing product query:', err.message);
      return job.fail(err.message); 
    }

    if (productResults.length === 0) {
      console.error('No product found with the name:', productName);
      return job.fail('Product not found');
    }

    const product_id = productResults[0].product_id;

    // Debugging
    console.log("Product ID: ", product_id);

    // Insert the customer order into the customer_order table
    dbConnection.query('INSERT INTO customer_order (product_id, customer_name, customer_email, customer_phone_number, ordered_quantity) VALUES (?, ?, ?, ?, ?)', 
      [product_id, job.variables.customerName, job.variables.customerEmail, job.variables.customerPhone, job.variables.customerQuantity], 
      (err, insertResults) => {
        if (err) {
          console.error('Error executing insert query:', err.message);
          return job.fail(err.message); 
        }

        // Access the insertId (co_id) from the callback
        const insertId = insertResults.insertId;

        // Insert the order status into the customer_order_status table
        const orderStatus = job.variables.orderStatus;

        dbConnection.query('INSERT INTO customer_order_status (co_id, order_status) VALUES (?, ?)', 
          [insertId, orderStatus], 
          (err, statusInsertResults) => {
            if (err) {
              console.error('Error inserting order status:', err.message);
              return job.fail(err.message); 
            }

            console.log("Order and status inserted successfully");

            const updateToBrokerVariables = {
              orderID: insertId,
              orderDateTime: orderDateTime,
            };

            return job.complete(updateToBrokerVariables);  
        });
    });
  });
}

module.exports = storeCustomerOrder;  
