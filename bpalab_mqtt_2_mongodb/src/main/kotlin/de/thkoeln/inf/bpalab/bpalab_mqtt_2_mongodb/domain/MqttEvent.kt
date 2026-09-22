package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Unwrapped
import java.time.LocalDateTime

open class MqttEvent(
    var topic: String,
    var timestamp: String = LocalDateTime.now().toString(),
    @Id
    var id: String? = null
)

//------------ stations: mpo, vgr, sld, hbw ------------

class MqttEventStationPayload(
    var ts: String = "",
    var code: String = "",
    var active: String = "",
    var station: String = "",
    var target: String = ""
)

class MqttEventStation(
    topic: String,
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    val payload: MqttEventStationPayload,
    )
: MqttEvent(topic)

//------------ Order ------------------------------------

class MqttEventOrderPayload(
    var ts: String = "",
    var state: String = "",
    var type: String = "",
    var processOrderReference: String = ""
)

class MqttEventOrder(
    topic: String,
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    val payload: MqttEventOrderPayload,
    ) : MqttEvent(topic)

//------------ LDR ------------------------------------

class MqttEventLdrPayload(
    var ts: String = "",
    var br: Double = 0.0,
    var ldr: String = ""
)

class MqttEventLdr(
    topic: String,
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    val payload: MqttEventLdrPayload,
) : MqttEvent(topic)

//------------ bme680 ------------------------------------
class MqttEventBme680(
    topic: String,
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    val payload: MqttEventBME680Payload
    ) : MqttEvent(topic)

class MqttEventBME680Payload(
    var ts: String = "",
    var t: Double = 0.0,
    var rt: Double = 0.0,
    var h: Double = 0.0,
    var rh: Double = 0.0,
    var p: Double = 0.0,
    var iaq: String = "",
    var aq: String = "",
    var gr: String = ""
)