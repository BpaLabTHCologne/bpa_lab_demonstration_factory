# Run zeebemqttbridge
to run zeebemqttbridge an *application.properties* file is needed, like

	zeebe.client.cloud.region=<YourClientCredentials>
	zeebe.client.cloud.clusterId=<YourClientCredentials>
	zeebe.client.cloud.clientId=<YourClientCredentials>
	zeebe.client.cloud.clientSecret=<YourClientCredentials>
	zeebe.client.connection-mode=CLOUD
	
or for self managed	

	zeebe.client.broker.gateway-address=<server>:26500
	zeebe.client.security.plaintext=true
	zeebe.client.connection-mode=ADDRESS

mqtt client conifg

	ftfactorymqttclient.broker=ws://<HOST>:<PORT>
	ftfactorymqttclient.clientId=FTFactoryMQTTClient

topics configuration 

	ftfactorymqttclient.cloudTopicPrefix=bpalab/ftfactory
	ftfactorymqttclient.cloudPubOrderTopic=bpalab/ftfactory/f/o/order
	ftfactorymqttclient.cloudBroadcastTopic=bpalab/ftfactory/o/broadcast
	ftfactorymqttclient.cloudSubOrderTopic=bpalab/ftfactory/f/i/order
	ftfactorymqttclient.cloudHBWTopic=bpalab/ftfactory/f/i/stock
	ftfactorymqttclient.cloudBME680Topic=bpalab/ftfactory/i/bme680
	
tomcat configuration (FtfactoryRestController)

	server.port=<PORT>


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
