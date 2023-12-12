const ZB = require('zeebe-node')
const nodemailer = require('nodemailer');

const zbc = new ZB.ZBClient();

//External job worker for sending order rejection email
const sendConfirmationEmail = zbc.createWorker({
	debug: true,
	loglevel: "DEBUG", 
	taskType: 'sendConfirmationEmail',
	taskHandler: handler,
	 // Called when the connection to the broker is (re-)established
        onReady: () => sendConfirmationEmail.log('Job worker started successfully!')
})

async function handler(job) {
	try {
			// worker.log('Task variables', job.variables)
			sendConfirmationEmail.log('Variables: ')
			sendConfirmationEmail.log(job.variables)

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
				subject: "Your NewBike order of " + job.variables.product + " has been confirmed!", // Subject line
				text: "Dear " + job.variables.name + 
                ",\n\nWe are thrilled to inform you that your order with NewBike GmbH has been successfully confirmed! Thank you for choosing us.\n\nHere are the key details of your order:\n\nOrder Number: " + job.variables.orderID + 
                "\nDate of Order: " + job.variables.deliveryDate + "\n\nItems Purchased:\n1. " + job.variables.product + "\n   - Quantity: " + job.variables.quantity + 
                "\n\nShipping Address: " + job.variables.address + "\n\nYour order is now being processed, and you can expect it to be dispatched shortly. We will notify you with the tracking details once your package is on its way.\n\nIf you have any questions or concerns, feel free to reply to this email. We are here to assist you!\n\nThank you again for choosing NewBike GmbH. We appreciate your business.\n\nBest regards,\n\nFred\nNewBike GmbH\nContact Information: orders@newbike", // plain text body
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

module.exports = sendConfirmationEmail;