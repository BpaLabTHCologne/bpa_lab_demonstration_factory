package de.thkoeln.bpalab.fakefactory.domain

import de.thkoeln.bpalab.fakefactory.model.TopicPayloadDTO
import de.thkoeln.bpalab.fakefactory.service.MqttService
import de.thkoeln.bpalab.fakefactory.service.TaskDefinition
import de.thkoeln.bpalab.fakefactory.service.TaskDefinitionBean
import de.thkoeln.bpalab.fakefactory.service.TaskSchedulingService
import de.thkoeln.bpalab.fakefactory.service.TopicPayloadService

class TopicPayloadPreset(
    val taskSchedulingService: TaskSchedulingService,
    val topicPayloadService: TopicPayloadService,
    val mqttService: MqttService
) {
    init {
        // Order
        var topicPayloadDTO = TopicPayloadDTO()
        topicPayloadDTO.topic = "bpalab/ftfactory/f/i/order"
        topicPayloadDTO.clock = 0
        topicPayloadDTO.payload = "{\"ts\":\"\", \"state\":\"ORDERED\",\"type\":\"RED\",\"processOrderReference\":\"00000099-99\"}"
        var isTopicPayload = topicPayloadService.isTopicPayload(topicPayloadDTO.topic)
        if (!isTopicPayload)
            topicPayloadService.create(topicPayloadDTO)

        // BME680
        topicPayloadDTO = TopicPayloadDTO()
        topicPayloadDTO.topic = "bpalab/ftfactory/i/bme680"
        topicPayloadDTO.clock = 10
        topicPayloadDTO.payload = "{\"ts\": \"\"," +
                "\"t\": 24.6," +
                "\"rt\": 0," +
                "\"h\": 39," +
                "\"rh\": 0," +
                "\"p\": 1000.7," +
                "\"iaq\": 3," +
                "\"aq\": 3," +
                "\"gr\": 0" +
                "}"
        isTopicPayload = topicPayloadService.isTopicPayload(topicPayloadDTO.topic)
        if (!isTopicPayload) {
            var id = topicPayloadService.create(topicPayloadDTO)
            TaskDefinitionBean(
                TaskDefinition(topicPayloadDTO.clock, id, topicPayloadService, mqttService)
            )
        }

        // DSI
        topicPayloadDTO = TopicPayloadDTO()
        topicPayloadDTO.topic = "bpalab/ftfactory/f/i/state/sld"
        topicPayloadDTO.clock = 10
        topicPayloadDTO.payload = "{\"ts\": \"\"," +
                "\"station\": \"sld\"," +
                "\"code\": 0," +
                "\"active\": 0" +
                "}"
        isTopicPayload = topicPayloadService.isTopicPayload(topicPayloadDTO.topic)
        if (!isTopicPayload) {
            var id = topicPayloadService.create(topicPayloadDTO)
            TaskDefinitionBean(
                TaskDefinition(topicPayloadDTO.clock, id, topicPayloadService, mqttService)
            )
        }
    }
}