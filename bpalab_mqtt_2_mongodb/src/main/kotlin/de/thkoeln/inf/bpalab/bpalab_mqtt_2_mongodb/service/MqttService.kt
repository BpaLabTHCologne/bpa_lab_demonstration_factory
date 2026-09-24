package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.service

import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt.MqttProperties
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
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt.MqttVgrListener
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventBme680Repository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventLdrRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventOrderRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventStationRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventVgrRepository

@Service
class MqttService(
    private val mqttClient: MqttClient,
    private val mqttProperties: MqttProperties,
    private val mqttEventStationRepository: MqttEventStationRepository,
    private val mqttEventBme680Repository: MqttEventBme680Repository,
    private val mqttEventOrderRepository: MqttEventOrderRepository,
    private val mqttEventLdrRepository: MqttEventLdrRepository,
    private val mqttEventVgrRepository: MqttEventVgrRepository,
) {
    val stationSubscriptions = arrayOf(
        "bpalab/ftfactory/f/i/state/hbw",
        "bpalab/ftfactory/f/i/state/mpo",
        "bpalab/ftfactory/f/i/state/sld",
        "bpalab/ftfactory/f/i/state/dsi",
        "bpalab/ftfactory/f/i/state/dso",
    )

    val vgrSubscription = "bpalab/ftfactory/f/i/state/vgr"
    val orderSubscription = "bpalab/ftfactory/f/i/order"
    val ldrSubscription = "bpalab/ftfactory/i/ldr"
    val bme680Subscription = "bpalab/ftfactory/i/bme680"


    init {
        stationSubscriptions.forEach {s ->
            mqttClient.subscribe(s, MqttStationListener(mqttEventStationRepository))
        }

        mqttClient.subscribe(vgrSubscription, MqttVgrListener(mqttEventVgrRepository))
        mqttClient.subscribe(orderSubscription, MqttOrderListener(mqttEventOrderRepository))
        mqttClient.subscribe(ldrSubscription, MqttLdrListener(mqttEventLdrRepository))
        mqttClient.subscribe(bme680Subscription, MqttBme680Listener(mqttEventBme680Repository))

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
