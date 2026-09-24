package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Unwrapped
import java.time.LocalDateTime

//open class MqttEvent(
//    var topic: String,
//    var timestamp: String = LocalDateTime.now().toString(),
//    @Id
//    var id: String? = null
//)

//------------ stations: mpo, sld, hbw ------------

class MqttEventStationPayload(
    @Transient
    var ts: String = "",
    var code: String = "",
    var active: Boolean = false,
    var station: String = ""
)

class MqttEventStation(
    var topic: String,
    var timestamp: String = LocalDateTime.now().toString(),
    @Id
    var id: String? = null,
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    val payload: MqttEventStationPayload,
    )

//------------ Vgr ------------------------------------

class MqttEventVgrPayload(
    @Transient
    var ts: String = "",
    var code: String = "",
    var active: Boolean = false,
    var station: String = "",
    var target: String = ""
)

class MqttEventVgr(
    var topic: String,
    var timestamp: String = LocalDateTime.now().toString(),
    @Id
    var id: String? = null,
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    val payload: MqttEventVgrPayload,
)

//------------ Order ------------------------------------

class MqttEventOrderPayload(
    @Transient
    var ts: String = "",
    var state: String = "",
    var type: String = "",
    var processOrderReference: String = ""
)

class MqttEventOrder(
    var topic: String,
    var timestamp: String = LocalDateTime.now().toString(),
    @Id
    var id: String? = null,
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    val payload: MqttEventOrderPayload,
    )

//------------ LDR ------------------------------------

class MqttEventLdrPayload(
    @Transient
    var ts: String = "",
    var br: Double = 0.0,
    var ldr: String = ""
)

class MqttEventLdr(
    var topic: String,
    var timestamp: String = LocalDateTime.now().toString(),
    @Id
    var id: String? = null,
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    val payload: MqttEventLdrPayload,
)

//------------ bme680 ------------------------------------

class MqttEventBME680Payload(
    @Transient
    var ts: String = "",
    var t: Double = 0.0,
    var rt: Double = 0.0,
    var h: Double = 0.0,
    var rh: Double = 0.0,
    var p: Double = 0.0,
    var iaq: Double = 0.0,
    var aq: Double = 0.0,
    var gr: Double = 0.0
)

class MqttEventBme680(
    var topic: String,
    var timestamp: String = LocalDateTime.now().toString(),
    @Id
    var id: String? = null,
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    val payload: MqttEventBME680Payload
)
