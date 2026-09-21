package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.mqtt

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

data class MqttProperties(
    val brokerUrl: String,
    val clientId: String,
)

@Configuration
class MqttConfig(
    @Value("${'$'}{mqtt.broker-url}") private val brokerUrl: String,
    @Value("${'$'}{mqtt.client-id}") private val clientId: String,
) {

    @Bean
    fun mqttProperties(): MqttProperties = MqttProperties(
        brokerUrl,
        clientId
    )

    @Bean
    fun mqttClient(mqttProperties: MqttProperties): MqttClient {
        val persistence = MemoryPersistence()
        val client = MqttClient(mqttProperties.brokerUrl, mqttProperties.clientId, persistence)
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = true
        }
        client.connect(options)
        return client
    }
}
