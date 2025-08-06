# Run BpaLabManufacturingApplication
to run BpaLabManufacturingApplication an *application.yml* file is needed, like

    camunda:
        client:
            mode: saas
            auth:
              client-id: 
              client-secret: 
            cloud:
              cluster-id: 
              region: 
	
or for self managed	

	camunda:
        client:
            mode: self-managed
            zeebe:
                enabled: true


mqtt client conifg

    ftfactorymqttclient:
        broker: ws://localhost:9001
        clientId: FTFactoryMQTTClient

topics configuration 

    cloudTopicPrefix: bpalab/ftfactory
    cloudPubOrderTopic: bpalab/ftfactory/f/o/order
    cloudBroadcastTopic: bpalab/ftfactory/o/broadcast
    cloudSubOrderTopic: bpalab/ftfactory/f/i/order
    cloudHBWTopic: bpalab/ftfactory/f/i/stock
    cloudBME680Topic: bpalab/ftfactory/i/bme680

# Sample mqtt messages

Sample BROADCAST publish:

> Topic

		baplab/ftfactory/o/broadcast

> Payload (JSON)

		{"ts":"2023-06-05T15:20:41.613", "message":"keep-alive"}


Sample ORDER publish:

> Topic

		baplab/ftfactory/f/o/order

> Payload (JSON)

		{"ts":"2023-06-05T15:20:41.613", "type":"BLUE"}

Sample HBW subscription:

> Topic:

		baplab/ftfactory/f/i/stock
	
> PayLoad(JSON):	
	
		{"ts":"2023-06-22T12:m12:12.123",
		"stockItems": 
		[{"workpiece": { "id":"123456789ABCDE", "type":"WHITE", "state":"RAW" }, "location":"A1"},
		 {"workpiece": { "id":"156789ABCDE", "type":"RED", "state":"RAW" }, "location":"A2"},
		 {"workpiece": { "id":"7765555", "type":"BLUE", "state":"RAW" }, "location":"A3"},
		 { "workpiece":null, "location":"B1"},
		 { "workpiece":null, "location":"B2"},
		 { "workpiece":null, "location":"B3" },
		 { "workpiece":null, "location":"C1"},
		 { "workpiece":null, "location":"C2"},
		 { "workpiece":null, "location":"C3" }] }

Sample BME680 subscription:

> Topic

		baplab/ftfactory/i/bme680

> PayLoad(JSON):

		{"ts":"2023-06-05T15:20:41.613","t":15.6,"rt":0,"h":56,"rh":0,"p":56.45,"iaq":60,"aq":0,"gr":0}

Sample ORDER subscription:

> Topic

		baplab/ftfactory/f/i/order

> Payload (JSON)

		{"ts":"2023-06-05T15:20:41.613", "state":"SHIPPED", "type":"BLUE"}
