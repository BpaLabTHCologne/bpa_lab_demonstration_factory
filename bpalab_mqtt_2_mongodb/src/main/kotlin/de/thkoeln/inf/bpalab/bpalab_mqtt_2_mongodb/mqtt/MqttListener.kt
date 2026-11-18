package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventBME680Payload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventBme680
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventLdr
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventLdrPayload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventOrder
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventOrderPayload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventRaw
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventStation
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventStationPayload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventVgr
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventVgrPayload
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventBme680Repository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventLdrRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventOrderRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventRawRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventStationRepository
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository.MqttEventVgrRepository
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class MqttStationListener(
    private val mqttEventRepository: MqttEventStationRepository,
    private val mqttEventRawRepository: MqttEventRawRepository,
    ): IMqttMessageListener, MqttCallback {
    private val log = LoggerFactory.getLogger(MqttStationListener::class.java)

    override fun connectionLost(cause: Throwable?) {
        log.info("connectionLost: {}", cause.toString())
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val mqttEventStationPayload = ObjectMapper().readValue<MqttEventStationPayload>(msgPayload)
            val mqttEvent = MqttEventStation(topic = topic, payload = mqttEventStationPayload)
            val event = mqttEventRepository.save(mqttEvent)
            log.info("Station $topic received message: ${event.payload.station}")
            val mqttEventRaw = MqttEventRaw(topic = topic, payload = msgPayload)
            mqttEventRawRepository.save(mqttEventRaw)
            log.info("Raw $topic received message: ${msgPayload}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        log.info("connectionLost: {}", token.toString())
    }

}

class MqttOrderListener(
    private val mqttEventRepository: MqttEventOrderRepository,
    private val mqttEventRawRepository: MqttEventRawRepository,
    ): IMqttMessageListener {
    private val log = LoggerFactory.getLogger(MqttOrderListener::class.java)
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) return
        val msgPayload = message.payload.toString(StandardCharsets.UTF_8)
        try {
            val mqttEventOrderPayload = ObjectMapper().readValue<MqttEventOrderPayload>(msgPayload)
            val mqttEvent = MqttEventOrder(
                topic = topic, payload = mqttEventOrderPayload
            )
            val event = mqttEventRepository.save(mqttEvent)
            log.info("Order $topic received message: ${event.payload.type}, ${event.payload.state}, ${event.payload.processOrderReference}")
            val mqttEventRaw = MqttEventRaw(topic = topic, payload = msgPayload)
            mqttEventRawRepository.save(mqttEventRaw)
            log.info("Raw $topic received message: ${msgPayload}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }
}

class MqttLdrListener(
    private val mqttEventRepository: MqttEventLdrRepository,
    private val mqttEventRawRepository: MqttEventRawRepository,
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
            log.info("LDR $topic received message: ${event.payload.ldr}")
            val mqttEventRaw = MqttEventRaw(topic = topic, payload = msgPayload)
            mqttEventRawRepository.save(mqttEventRaw)
            log.info("Raw $topic received message: ${msgPayload}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }
}

class MqttBme680Listener(
    private val mqttEventRepository: MqttEventBme680Repository,
    private val mqttEventRawRepository: MqttEventRawRepository,
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
            log.info("BME680 $topic received message: ${event.payload.t}")
            val mqttEventRaw = MqttEventRaw(topic = topic, payload = msgPayload)
            mqttEventRawRepository.save(mqttEventRaw)
            log.info("Raw $topic received message: ${msgPayload}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }
}

class MqttVgrListener(
    private val mqttEventRepository: MqttEventVgrRepository,
    private val mqttEventRawRepository: MqttEventRawRepository,
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
            log.info("VGR $topic received message: ${event.payload.target}")
            val mqttEventRaw = MqttEventRaw(topic = topic, payload = msgPayload)
            mqttEventRawRepository.save(mqttEventRaw)
            log.info("Raw $topic received message: ${msgPayload}")
        } catch (e: Exception) {
            log.info("Exception: $e")
        }
    }
}