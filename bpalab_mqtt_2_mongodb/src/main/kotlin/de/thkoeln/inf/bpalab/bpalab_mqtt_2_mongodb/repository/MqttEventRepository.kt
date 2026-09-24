package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.repository

import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventBme680
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventLdr
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventOrder
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventStation
import de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain.MqttEventVgr
import org.springframework.data.repository.CrudRepository

interface MqttEventStationRepository: CrudRepository<MqttEventStation, String> {
    fun findAllByOrderByTimestampAsc(): Iterable<MqttEventStation>
}

interface MqttEventVgrRepository: CrudRepository<MqttEventVgr, String> {
    fun findAllByOrderByTimestampAsc(): Iterable<MqttEventVgr>
}

interface MqttEventBme680Repository: CrudRepository<MqttEventBme680, String> {
    fun findAllByOrderByTimestampAsc(): Iterable<MqttEventBme680>
}

interface MqttEventOrderRepository: CrudRepository<MqttEventOrder, String> {
    fun findAllByOrderByTimestampAsc(): Iterable<MqttEventOrder>
}

interface MqttEventLdrRepository: CrudRepository<MqttEventLdr, String> {
    fun findAllByOrderByTimestampAsc(): Iterable<MqttEventLdr>
}
