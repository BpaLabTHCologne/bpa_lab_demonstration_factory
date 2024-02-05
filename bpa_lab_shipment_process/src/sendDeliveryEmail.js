const ZB = require('zeebe-node')
const nodemailer = require('nodemailer');

// const zbc = new ZB.ZBClient('localhost:26500')
const zbc = new ZB.ZBClient({
	hostname: 'zeebe'
  });

//External job worker for sending order rejection email
const sendDeliveryEmail = zbc.createWorker({
	// debug: true,
	// loglevel: "DEBUG", 
	taskType: 'sendDeliveryEmail',
	taskHandler: handler,
	 // Called when the connection to the broker is (re-)established
        onReady: () => sendDeliveryEmail.log('Job worker started successfully!')
})

async function handler(job) {
	try {
			// worker.log('Task variables', job.variables)
			sendDeliveryEmail.log('sendShippedEmail variables:', job.variables)

			const transporter = nodemailer.createTransport({
				host: 'smtp.ethereal.email',
				port: 587,
				auth: {
					user: 'tessie.tillman94@ethereal.email',
					pass: 'PEW7jjMfhhQGsP92AR'
				}
			});

			const info = await transporter.sendMail({
				from: '"Peter" <peter@newbike.com>', // sender address
				to: job.variables.customerEmail, // list of receivers
				cc: "orders@newbike, qa@newbike.com", // list of cc's
				subject: "Your NewBike order of Mountain bike has Delivered!", // Subject line
				text: "Dear " + job.variables.customerName +
                ",\n\nWe are thrilled to inform you that your order with NewBike GmbH has been successfully delivered! Thank you for choosing us."
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

module.exports = sendDeliveryEmail;