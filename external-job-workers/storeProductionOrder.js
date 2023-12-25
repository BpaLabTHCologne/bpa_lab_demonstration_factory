const { ZBClient } = require('zeebe-node');

const zbc = new ZBClient();

const storeProductionOrder = zbc.createWorker({
	taskType: 'storeProductionOrder',
	taskHandler: handler,
	onReady: () => storeProductionOrder.log('Job worker started successfully!')
})

function handler(job) {
	console.log('Task variables from previous pool:', job.variables)
	// Task worker business logic goes here
	const updateToBrokerVariables = {
		
	}

	return job.complete(updateToBrokerVariables)
}

module.exports = storeProductionOrder;