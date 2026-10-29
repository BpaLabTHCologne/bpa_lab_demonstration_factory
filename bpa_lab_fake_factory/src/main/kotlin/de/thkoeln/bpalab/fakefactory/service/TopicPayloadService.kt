package de.thkoeln.bpalab.fakefactory.service

import de.thkoeln.bpalab.fakefactory.domain.TopicPayload
import de.thkoeln.bpalab.fakefactory.model.TopicPayloadDTO
import de.thkoeln.bpalab.fakefactory.repos.TopicPayloadRepository
import de.thkoeln.bpalab.fakefactory.util.NotFoundException
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ObjectNode
import java.time.LocalDateTime


@Service
class TopicPayloadService(
    private val topicPayloadRepository: TopicPayloadRepository,
    private val taskSchedulingService: TaskSchedulingService,
    private val mqttService: MqttService,
) {

    fun findAll(): List<TopicPayloadDTO> {
        val topicPayloads = topicPayloadRepository.findAll(Sort.by("id"))
        return topicPayloads.map { topicPayload -> mapToDTO(topicPayload, TopicPayloadDTO()) }
    }

    fun `get`(id: Long): TopicPayloadDTO = topicPayloadRepository.findById(id)
            .map { topicPayload -> mapToDTO(topicPayload, TopicPayloadDTO()) }
            .orElseThrow { NotFoundException() }

    fun create(topicPayloadDTO: TopicPayloadDTO): Long {
        val topicPayload = TopicPayload()
        mapToEntity(topicPayloadDTO, topicPayload)
        val topicPayloadId = topicPayloadRepository.save(topicPayload).id!!
        val clock: Long = topicPayload.clock?:0
        if (clock > 0) {
                val taskDefinition = TaskDefinition(
                    clock = clock,
                    mqttEventId = topicPayloadId,
                    topicPayloadService = this,
                    mqttService = mqttService
                )
                taskSchedulingService.scheduleATask(TaskDefinitionBean(taskDefinition))
            }
        return topicPayloadId
    }

    fun update(id: Long, topicPayloadDTO: TopicPayloadDTO) {
        var topicPayload = topicPayloadRepository.findById(id)
            .orElseThrow { NotFoundException() }
        mapToEntity(topicPayloadDTO, topicPayload)
        topicPayload = topicPayloadRepository.save(topicPayload)
        val topicPayloadId = topicPayload.id ?: 0
        val clock = topicPayload.clock ?: 0
        if (clock >= 0) {
            if (taskSchedulingService.jobsMap.contains(topicPayloadId))
                taskSchedulingService.removeScheduledTask(topicPayloadId)
            if (clock > 0) {
                val taskDefinition = TaskDefinition(
                    clock = clock,
                    mqttEventId = topicPayloadId,
                    topicPayloadService = this,
                    mqttService = mqttService
                )
                taskSchedulingService.scheduleATask(TaskDefinitionBean(taskDefinition))
            }
        }
    }

    fun delete(id: Long) {
        val topicPayload = topicPayloadRepository.findById(id)
                .orElseThrow { NotFoundException() }
        topicPayloadRepository.delete(topicPayload)
        if (taskSchedulingService.jobsMap.contains(topicPayload.id))
            taskSchedulingService.removeScheduledTask(topicPayload.id)
    }

    private fun mapToDTO(topicPayload: TopicPayload, topicPayloadDTO: TopicPayloadDTO):
            TopicPayloadDTO {
        topicPayloadDTO.id = topicPayload.id
        topicPayloadDTO.topic = topicPayload.topic
        topicPayloadDTO.clock = topicPayload.clock!!
        try {
            val payloadRoot = ObjectMapper().readTree(topicPayload.payload) as ObjectNode
            payloadRoot.put("ts", LocalDateTime.now().toString())
            topicPayload.payload = ObjectMapper().writeValueAsString(payloadRoot)
        } catch (e: Exception) {
            throw e
        }
        topicPayloadDTO.payload = refreshTimestamp(topicPayload.payload)
        return topicPayloadDTO
    }

    private fun mapToEntity(topicPayloadDTO: TopicPayloadDTO, topicPayload: TopicPayload):
            TopicPayload {
        topicPayload.topic = topicPayloadDTO.topic
        topicPayload.clock = topicPayloadDTO.clock
        try {
            val payloadRoot = ObjectMapper().readTree(topicPayloadDTO.payload) as ObjectNode
            payloadRoot.put("ts", LocalDateTime.now().toString())
            topicPayloadDTO.payload = ObjectMapper().writeValueAsString(payloadRoot)
        } catch (e: Exception) {
            throw e
        }
        topicPayload.payload = refreshTimestamp(topicPayloadDTO.payload)
        return topicPayload
    }

    fun isTopicPayload(topic: String?): Boolean {
        if (topic != null)
            return topicPayloadRepository.existsByTopic(topic)
        else
            return false
    }

    fun refreshTimestamp(payload: String?): String? {
        try {
            val payloadRoot = ObjectMapper().readTree(payload) as ObjectNode
            payloadRoot.put("ts", LocalDateTime.now().toString())
            return ObjectMapper().writeValueAsString(payloadRoot)
        } catch (e: Exception) {
            throw e
        }

    }
}
