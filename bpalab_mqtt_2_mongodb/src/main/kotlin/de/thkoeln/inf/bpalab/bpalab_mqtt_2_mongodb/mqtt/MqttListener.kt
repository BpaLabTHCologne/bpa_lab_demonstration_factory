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
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventVgr
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventVgrPayload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventRepository
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.StandardCharsets

class MqttStationListener(
    private val mqttEventRepository: MqttEventRepository
    ): IMqttMessageListener {
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val mqttEventStationPayload = ObjectMapper().readValue<MqttEventStationPayload>(msgPayload)
            val mqttEvent = MqttEventStation(topic = topic, payload = mqttEventStationPayload)
            val event = mqttEventRepository.save(mqttEvent)
            println("Stored event ${event.topic} : ${event.payload.station}")
        } catch (e: Exception) {
            println("Exception: $e")
        }
    }

}

class MqttOrderListener(
    private val mqttEventRepository: MqttEventRepository
    ): IMqttMessageListener {
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val mqttEvent = MqttEventOrder(
                topic = topic, payload =
                    ObjectMapper().readValue<MqttEventOrderPayload>(msgPayload)
            )
            val event = mqttEventRepository.save(mqttEvent)
            println("Stored event ${event.topic} : ${event.payload.type}")
        } catch (e: Exception) {
            println("Exception: $e")
        }
    }
}

class MqttLdrListener(
    private val mqttEventRepository: MqttEventRepository
): IMqttMessageListener {
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val mqttEvent = MqttEventLdr(
                topic = topic, payload =
                    ObjectMapper().readValue<MqttEventLdrPayload>(msgPayload)
            )
            val event = mqttEventRepository.save(mqttEvent)
            println("Stored event ${event.topic} : ${event.payload.ldr}")
        } catch (e: Exception) {
            println("Exception: $e")
        }
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
            println("Stored event ${event.topic} : ${event.payload.ts}")
        } catch (e: Exception) {
            println("Exception: $e")
        }
    }
}

class MqttVgrListener(
    private val mqttEventRepository: MqttEventRepository
): IMqttMessageListener {
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        println("MQTT message arrived: $topic")
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val payload = ObjectMapper().readValue<MqttEventVgrPayload>(msgPayload)
            val mqttEvent = MqttEventVgr(
                topic = topic, payload = payload
            )
            val event = mqttEventRepository.save(mqttEvent)
            println("Stored even ${event.topic} : ${event.payload.target}")
        } catch (e: Exception) {
            println("Exception: $e")
        }
    }
}
