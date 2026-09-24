package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.BpalabMqtt2MongodbApplication
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
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventBme680Repository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventLdrRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventOrderRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventStationRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventVgrRepository
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class MqttStationListener(
    private val mqttEventRepository: MqttEventStationRepository
    ): IMqttMessageListener {
    private val log = LoggerFactory.getLogger(MqttStationListener::class.java)

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val mqttEventStationPayload = ObjectMapper().readValue<MqttEventStationPayload>(msgPayload)
            val mqttEvent = MqttEventStation(topic = topic, payload = mqttEventStationPayload)
            val event = mqttEventRepository.save(mqttEvent)
            log.info("$topic received message: ${event.payload.station}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }

}

class MqttOrderListener(
    private val mqttEventRepository: MqttEventOrderRepository
    ): IMqttMessageListener {
    private val log = LoggerFactory.getLogger(MqttOrderListener::class.java)
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val mqttEvent = MqttEventOrder(
                topic = topic, payload =
                    ObjectMapper().readValue<MqttEventOrderPayload>(msgPayload)
            )
            val event = mqttEventRepository.save(mqttEvent)
            log.info("$topic received message: ${event.payload.type}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }
}

class MqttLdrListener(
    private val mqttEventRepository: MqttEventLdrRepository
): IMqttMessageListener {
    private val log = LoggerFactory.getLogger(MqttLdrListener::class.java)
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val mqttEvent = MqttEventLdr(
                topic = topic, payload =
                    ObjectMapper().readValue<MqttEventLdrPayload>(msgPayload)
            )
            val event = mqttEventRepository.save(mqttEvent)
            log.info("$topic received message: ${event.payload.ldr}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }
}

class MqttBme680Listener(
    private val mqttEventRepository: MqttEventBme680Repository
    ): IMqttMessageListener {
    private val log = LoggerFactory.getLogger(MqttBme680Listener::class.java)
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val payload = ObjectMapper().readValue<MqttEventBME680Payload>(msgPayload)
            val mqttEvent = MqttEventBme680(
                topic = topic, payload = payload
            )
            val event = mqttEventRepository.save(mqttEvent)
            log.info("$topic received message: ${event.payload.t}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }
}

class MqttVgrListener(
    private val mqttEventRepository: MqttEventVgrRepository
): IMqttMessageListener {
    private val log = LoggerFactory.getLogger(MqttVgrListener::class.java)
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val payload = ObjectMapper().readValue<MqttEventVgrPayload>(msgPayload)
            val mqttEvent = MqttEventVgr(
                topic = topic, payload = payload
            )
            val event = mqttEventRepository.save(mqttEvent)
            log.info("$topic received message: ${event.payload.target}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }
}
