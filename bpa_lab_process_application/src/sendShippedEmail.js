const ZB = require('zeebe-node')
const nodemailer = require('nodemailer');

const zbc = new ZB.ZBClient({
	hostname: 'zeebe'
  });

//External job worker for sending order rejection email
const sendShippedEmail = zbc.createWorker({
	// debug: true,
	// loglevel: "DEBUG", 
	taskType: 'sendShippedEmail',
	taskHandler: handler,
	 // Called when the connection to the broker is (re-)established
        onReady: () => sendShippedEmail.log('Job worker started successfully!')
})

async function handler(job) {
	try {
			// worker.log('Task variables', job.variables)
			sendShippedEmail.log('sendShippedEmail variables:', job.variables)

			const transporter = nodemailer.createTransport({
				host: 'smtp.ethereal.email',
				port: 587,
				auth: {
					user: 'arturo.roberts@ethereal.email',
					pass: 'JrTD1b89g5hEjN1UBh'
				}
			});

			const info = await transporter.sendMail({
				from: '"Fred" <fred@newbike.com>', // sender address
				to: job.variables.customerEmail, // list of receivers
				cc: "orders@newbike, qa@newbike.com", // list of cc's
				subject: "Your NewBike order of " + job.variables.customerProduct + " has shipped!", // Subject line
				text: "Dear " + job.variables.customerName + 
                ",\n\nWe are thrilled to inform you that your order with NewBike GmbH has been successfully shipped! Thank you for choosing us.\n\nHere are the key details of your order:\n\nOrder Number: " + job.variables.orderID + 
                "\nDate of Order: " + job.variables.deliveryDate + "\n\nItems Purchased:\n1. " + job.variables.customerProduct + "\n   - Quantity: " + job.variables.customerQuantity + 
                "\n\nShipping Address: " + job.variables.customerAddress + "\n\nYour order is now complete.\n\nIf you have any questions or concerns, feel free to reply to this email. We are here to assist you!\n\nThank you again for choosing NewBike GmbH.\n\nBest regards,\n\nFred\nNewBike GmbH\nContact Information: orders@newbike", // plain text body
				// html: "<b>Hello world?</b>", html body
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

module.exports = sendShippedEmail;