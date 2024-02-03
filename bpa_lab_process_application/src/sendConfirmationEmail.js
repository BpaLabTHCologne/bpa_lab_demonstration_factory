const ZB = require('zeebe-node')
const nodemailer = require('nodemailer');

const zbc = new ZB.ZBClient({
	hostname: 'zeebe'
  });

//External job worker for sending order rejection email
const sendConfirmationEmail = zbc.createWorker({
	// debug: true,
	// loglevel: "DEBUG", 
	taskType: 'sendConfirmationEmail',
	taskHandler: handler,
	 // Called when the connection to the broker is (re-)established
        onReady: () => sendConfirmationEmail.log('Job worker started successfully!')
})

async function handler(job) {
	try {
			// worker.log('Task variables', job.variables)
			sendConfirmationEmail.log('sendConfirmationEmail variables:', job.variables)

			const transporter = nodemailer.createTransport({
				host: 'smtp.ethereal.email',
				port: 587,
				auth: {
					user: 'hobart78@ethereal.email',
					pass: '3HVw4m8m3gDawPhZ49'
				}
			});

			const info = await transporter.sendMail({
				from: '"Fred" <fred@newbike.com>', // sender address
				to: job.variables.customerEmail, // list of receivers
				cc: "orders@newbike, qa@newbike.com", // list of cc's
				subject: "Your NewBike order of " + job.variables.customerProduct + " has been confirmed!", // Subject line
				html: "<p><b>Dear " + job.variables.customerName + ",</b></p>" + 
					"<p>We are thrilled to inform you that your order with NewBike GmbH has been successfully confirmed! Thank you for choosing us.</p>" + 
					"<p>Here are the key details of your order:</p>" +
					"<ul>" +
					"<li><b>Order Number:</b> " + job.variables.orderID + "</li>" +
					"<li><b>Date of Order:</b> " + job.variables.customerOrderDate + "</li>" +
					"<li><b>Time of Order:</b> " + job.variables.customerOrderTime + "</li>" +
					"<li><b>Expected Delivery Date:</b> " + job.variables.expectedDeliveryDate + "</li>" +
					"<li><b>Order details:</b><br>" + "<b>Product: </b>" + job.variables.customerProduct + "<br><b>Quantity: </b>" + job.variables.customerQuantity + "</li>" +
					"</ul>" +
					"<p><b>Shipping Address:</b> " + job.variables.customerAddress + "</p>" +
					"<p><b>Your order is now being processed, and you can expect it to be dispatched shortly. We will notify you with the tracking details once your package is on its way.</b></p>" +
					"<p>If you have any questions or concerns, feel free to reply to this email. We are here to assist you!</p>" +
					"<p>Thank you again for choosing NewBike GmbH.</p>" +
					"<p><b>Best regards,</b><br><b>Fred</b><br><b>NewBike GmbH</b><br><b>Contact Information: orders@newbike</b></p>", // html body
			});
			
			

			  console.log("Message sent: %s", info.messageId);
			  console.log("Preview URL: %s", nodemailer.getTestMessageUrl(info));

			// Task worker business logic goes here
			const updateToBrokerVariables = {
			}
		
			return job.complete(updateToBrokerVariables)

	} catch (error) {
		console.log("Got error:", error)
	}
}

module.exports = sendConfirmationEmail;