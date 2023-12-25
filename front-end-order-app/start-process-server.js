const express = require('express');
const { ZBClient } = require('zeebe-node');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
const port = 3000;

// Create a Zeebe client
const zbc = new ZBClient({
  // Specify your Zeebe broker contact point(s)
  brokerContactPoint: 'localhost:26500',
});

// Use middleware to parse JSON in the request body
app.use(bodyParser.json());

// Use the cors middleware to enable CORS for all routes
app.use(cors());

// Define an endpoint to start a process instance
app.post('/start-process-server', async (req, res) => {
  try {
    const { workflowKey, variables } = req.body;

    // Use createProcessInstance method
    const response = await zbc.createProcessInstanceWithResult({
      bpmnProcessId: workflowKey,
      variables,
    });

    res.json({ success: true, response });
  } catch (error) {
    console.error('Error starting process instance:', error);
    res.status(500).json({ success: false, error: error.message });
  }
});

// Other endpoints and middleware...

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
