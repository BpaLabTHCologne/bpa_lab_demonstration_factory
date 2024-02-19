const ZB = require('zeebe-node')
const nodemailer = require('nodemailer');

const zbc = new ZB.ZBClient({
	hostname: 'zeebe'
  });


//External job worker for sending order rejection email
const sendRejectionEmail = zbc.createWorker({
	// debug: true,
	// loglevel: "DEBUG", 
	taskType: 'sendVendorMail',
	taskHandler: handler,
	 // Called when the connection to the broker is (re-)established
        onReady: () => sendRejectionEmail.log('Job worker (Mail) started successfully!')
})

async function handler(job) {
	try {
			 //worker.log('Task variables', job.variables)
			sendRejectionEmail.log('Hello this is now working.....')
			sendRejectionEmail.log('Variables: ')
			sendRejectionEmail.log(job.variables)
			//var bodyText= 'Hello '+job.variables.vendor+'the material requested is: '+job.variables.material;


			const transporter = nodemailer.createTransport({
				host: 'smtp.ethereal.email',
				port: 587,
				auth: {
					user: 'itzel.rosenbaum@ethereal.email',
					pass: 'YQSNdeWkPJqwjNV49v'
				}
			});

			const info = await transporter.sendMail({
				from: '"Fred" <fred@newbike.com>', // sender address
				to: job.variables.vendor_key+ '@ethereal.email', // list of receivers - job.variables.customerEmail
				cc: "purchasing@newbike, qa@newbike.com", // list of cc's
				subject: "Purchasing Request NEW", // Subject line
				//text: 'ariables.material, //"Dear Person, we want to order something.", // plain text body
				 html: '<b>Hello '+job.variables.vendor_key+', we would like to place the following order: </b> <br> <p> <table><tr><td>Material</td><td>Amount</td><td>Price</td></tr><tr> <td>'+job.variables.material_key+'</td><td>'+job.variables.quantity_key+'</td><td>'+job.variables.price_key+'</td></tr> </table> </p>',
				 
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