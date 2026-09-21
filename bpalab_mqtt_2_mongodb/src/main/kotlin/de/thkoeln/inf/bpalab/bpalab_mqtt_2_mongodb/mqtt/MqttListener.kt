package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventBME680Payload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventBme680
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventLdr
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventLdrPayload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventOrder
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventOrderPayload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventStation
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventStationPayload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventRepository
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.StandardCharsets

class MqttStationListener(
    private val mqttEventRepository: MqttEventRepository
    ): IMqttMessageListener {
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        val payload = ObjectMapper().readValue<MutableMap<String, Any>>(msgPayload)
        payload.forEach { key, value ->
            println("MQTT message payload: ${key}, value: $value")
        }
        val mqttEventStationPayload = ObjectMapper().readValue<MqttEventStationPayload>(msgPayload)
        val mqttEvent = MqttEventStation(topic = topic, payload = mqttEventStationPayload)
        val event = mqttEventRepository.save(mqttEvent)

        println("Received message: $event")
    }

}

class MqttOrderListener(
    private val mqttEventRepository: MqttEventRepository
    ): IMqttMessageListener {
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        val mqttEvent = MqttEventOrder(
            topic = topic, payload =
                ObjectMapper().readValue<MqttEventOrderPayload>(msgPayload)
        )
        val event = mqttEventRepository.save(mqttEvent)

        println("Received message: $event")
    }
}

class MqttLdrListener(
    private val mqttEventRepository: MqttEventRepository
): IMqttMessageListener {
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        val mqttEvent = MqttEventLdr(
            topic = topic, payload =
                ObjectMapper().readValue<MqttEventLdrPayload>(msgPayload)
        )
        val event = mqttEventRepository.save(mqttEvent)

        println("Received message: $event")
    }
}

class MqttBme680Listener(
    private val mqttEventRepository: MqttEventRepository
    ): IMqttMessageListener {
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        println("MQTT message arrived: $topic")
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        println("MQTT message payload: $msgPayload")
        try {
            val payload = ObjectMapper().readValue<MqttEventBME680Payload>(msgPayload)
            val mqttEvent = MqttEventBme680(
                topic = topic, payload = payload
            )
            val event = mqttEventRepository.save(mqttEvent)
            println("Received message: $event")
        } catch (e: Exception) {
            println("Exception: $e")
        }
    }
}
