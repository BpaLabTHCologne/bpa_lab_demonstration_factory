package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository

import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEvent
import org.springframework.data.repository.CrudRepository

interface MqttEventRepository: CrudRepository<MqttEvent, String> {
    fun findAllByOrderByTimestampAsc(): Iterable<MqttEvent>
}
