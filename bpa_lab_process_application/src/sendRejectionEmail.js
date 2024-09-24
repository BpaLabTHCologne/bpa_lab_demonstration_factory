const ZB = require('zeebe-node')
const nodemailer = require('nodemailer');

const zbc = new ZB.ZBClient({
	hostname: 'zeebe'
  });

//External job worker for sending order rejection email
const sendRejectionEmail = zbc.createWorker({
	taskType: 'sendRejectionEmail',
	taskHandler: handler,
	 // Called when the connection to the broker is (re-)established
        onReady: () => sendRejectionEmail.log('Job worker started successfully!')
})

async function handler(job) {
	try {
			// worker.log('Task variables', job.variables)
			sendRejectionEmail.log('sendRejectionEmail variables:', job.variables)

			const transporter = nodemailer.createTransport({
				host: 'smtp.ethereal.email',
				port: 587,
				auth: {
					user: 'delfina97@ethereal.email',
					pass: '9hQh9xQVAwpZsC1QCK' 
				}
			});

			const info = await transporter.sendMail({
				from: '"Fred" <fred@newbike.com>', // sender address
				to: job.variables.customerEmail, // list of receivers
				cc: "orders@newbike, qa@newbike.com", // list of cc's
				subject: "Your order has been rejected. Sorry for inconvenience!", // Subject line
				html: "<p><b>Dear " + job.variables.customerName + ",</b></p>" + 
					"<p>We are really sorry to inform you that your order has not been confirmed.</p>" + 
					"<p>Here are the key details of your order:</p>" +
					"<ul>" +
					"<li><b>Order Number:</b> " + job.variables.orderID + "</li>" +
					"<li><b>Date of Order:</b> " + job.variables.customerOrderDate + "</li>" +
					"<li><b>Time of Order:</b> " + job.variables.customerOrderTime + "</li>" +
					"<li><b>Order details:</b><br>" + "<b>Product: </b>" + job.variables.customerProduct + "<br><b>Quantity: </b>" + job.variables.customerQuantity + "</li>" +
					"</ul>" +
					"<p><b>Shipping Address:</b> " + job.variables.customerAddress + "</p>" +
					"<p><b>Reason: Currently, we only accept orders with quantity below five.</b></p>" +
					"<p><b>Best regards,</b><br><b>Fred</b><br><b>NewBike GmbH</b><br><b>Contact Information: orders@newbike</b></p>", // html body
			  });

			  console.log("Message sent: %s", info.messageId);
			  console.log("Preview URL: %s", nodemailer.getTestMessageUrl(info));
		
			return job.complete()

	} catch (error) {
		console.log("Got error:", error)
	}
}

module.exports = sendRejectionEmail;