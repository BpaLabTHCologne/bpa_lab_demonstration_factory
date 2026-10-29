package de.thkoeln.bpalab.fakefactory.model

import jakarta.validation.constraints.Size


class TopicPayloadDTO {

    var id: Long? = null

    @Size(max = 255)
    var topic: String? = null

    @Size(max = 255)
    var payload: String? = null

    var clock: Long = 0

}
