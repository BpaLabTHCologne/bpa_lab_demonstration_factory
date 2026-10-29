package de.thkoeln.bpalab.fakefactory.service

import de.thkoeln.bpalab.fakefactory.config.MqttProperties
import de.thkoeln.bpalab.fakefactory.model.TopicPayloadDTO
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.nio.charset.StandardCharsets
import java.util.concurrent.CopyOnWriteArrayList


data class MqttMessagePayload(
    val topic: String,
    val payload: String
)

@Service
class MqttService(
    private val mqttClient: MqttClient,
    private val mqttProperties: MqttProperties
) {
    private val emitters = CopyOnWriteArrayList<SseEmitter>()
    init {
        addSubscription("#")

        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                println("MQTT connection lost: ${cause?.message}")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (topic == null || message == null) return
                val payload = message.payload.toString(StandardCharsets.UTF_8)
                val event = MqttMessagePayload(topic, payload)
                emitters.forEach { emitter ->
                    try {
                        emitter.send(SseEmitter.event()
                            .name("mqtt-message")
                            .data(event))
                    } catch (error: Exception) {
                        emitter.completeWithError(error)
                        emitters.remove(emitter)
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }
        })

        if (!mqttClient.isConnected) {
            mqttClient.connect()
        }
    }


    fun publishMessage(topicPayloadDTO: TopicPayloadDTO) {
        val message = MqttMessage(topicPayloadDTO.payload?.toByteArray(StandardCharsets.UTF_8))
        message.qos = 0
        mqttClient.publish(topicPayloadDTO.topic, message)
    }

    fun createEmitter(): SseEmitter {
        val emitter = SseEmitter(Long.MAX_VALUE)
        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitter.complete() }
        emitters.add(emitter)
        return emitter
    }

    fun addSubscription(topic: String?) {
        mqttClient.subscribe(topic)
    }
}
