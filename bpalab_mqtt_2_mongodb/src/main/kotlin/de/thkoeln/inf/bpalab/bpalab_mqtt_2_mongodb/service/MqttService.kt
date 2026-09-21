package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.service

import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEvent
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt.MqttProperties
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventRepository
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventStation
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventStationPayload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt.MqttBme680Listener
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt.MqttLdrListener
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt.MqttOrderListener
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt.MqttStationListener

@Service
class MqttService(
    private val mqttClient: MqttClient,
    private val mqttProperties: MqttProperties,
    private val mqttEventRepository: MqttEventRepository
) {
    val stationSubscriptions = arrayOf(
        "bpalab/ftfactory/f/i/state/hbw",
        "bpalab/ftfactory/f/i/state/vgr",
        "bpalab/ftfactory/f/i/state/mpo",
        "bpalab/ftfactory/f/i/state/sld",
    )
    val orderSubscription = "bpalab/ftfactory/f/i/order"
    val ldrSubscription = "bpalab/ftfactory/f/i/ldr"
    val bme680Subscription = "bpalab/ftfactory/f/i/bme680"


    init {
        stationSubscriptions.forEach {s ->
            mqttClient.subscribe(s, MqttStationListener(mqttEventRepository))
        }

        mqttClient.subscribe(orderSubscription, MqttOrderListener(mqttEventRepository))
        mqttClient.subscribe(ldrSubscription, MqttLdrListener(mqttEventRepository))
        mqttClient.subscribe(bme680Subscription, MqttBme680Listener(mqttEventRepository))

        if (!mqttClient.isConnected) {
            mqttClient.connect()
        }
    }


    fun publishMessage(topic: String, payload: String): String {
        val message = MqttMessage(payload.toByteArray(StandardCharsets.UTF_8))
        message.qos = 1
        mqttClient.publish(topic, message)
        return topic
    }

}
