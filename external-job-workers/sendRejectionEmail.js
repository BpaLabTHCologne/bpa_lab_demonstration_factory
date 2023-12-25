const ZB = require('zeebe-node')
const nodemailer = require('nodemailer');

const zbc = new ZB.ZBClient();

//External job worker for sending order rejection email
const sendRejectionEmail = zbc.createWorker({
	// debug: true,
	// loglevel: "DEBUG", 
	taskType: 'sendRejectionEmail',
	taskHandler: handler,
	 // Called when the connection to the broker is (re-)established
        onReady: () => sendRejectionEmail.log('Job worker started successfully!')
})

async function handler(job) {
	try {
			// worker.log('Task variables', job.variables)
			sendRejectionEmail.log('Variables: ')
			sendRejectionEmail.log(job.variables)

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
				to: job.variables.email, // list of receivers
				cc: "orders@newbike, qa@newbike.com", // list of cc's
				subject: "Your delivery date has postponed. Sorry for inconvenience!", // Subject line
				text: "Dear " + job.variables.name + ", we are really sorry to say that due to some delay in the manufacturing unit, we had no choice but to delay the delivery date. The new expected delivery date is: " + job.variables.deliveryDate + ". We are really sorry for inconvenience. All our relevant departments have been notified regarding the issue and will get back to you as soon as possible. If you have any questions, please feel free to contact our support and we would be happy to assist you.", // plain text body
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

module.exports = sendRejectionEmail;