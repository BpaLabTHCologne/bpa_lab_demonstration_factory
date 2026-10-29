package de.thkoeln.bpalab.fakefactory.service

import de.thkoeln.bpalab.fakefactory.domain.TopicPayload
import de.thkoeln.bpalab.fakefactory.model.TopicPayloadDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.PeriodicTrigger
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import java.util.concurrent.ScheduledFuture

class TaskDefinition(
    var clock: Long,
    var mqttEventId: Long,
    val topicPayloadService: TopicPayloadService,
    val mqttService: MqttService
){
    fun sendMqttEvent() {
        mqttService.publishMessage(getTopicPayload())
    }

    fun getTopicPayload(): TopicPayloadDTO {
        var topicPayloadDTO = topicPayloadService.get(mqttEventId)
        topicPayloadDTO.payload = topicPayloadService.refreshTimestamp(topicPayloadDTO.payload)
        return topicPayloadDTO
    }
}


class TaskDefinitionBean(val taskDefinition: TaskDefinition):  Runnable {
    private val log = LoggerFactory.getLogger(TaskDefinitionBean::class.java)

    override fun run() {
        val topicPayload = taskDefinition.getTopicPayload()
        log.info("sending: ${topicPayload.topic} with Payload: ${topicPayload.payload}")
        taskDefinition.sendMqttEvent()
    }
}

@Service
class TaskSchedulingService(
    @Qualifier("taskScheduler")
    val taskScheduler: TaskScheduler) {
    private val log = LoggerFactory.getLogger(TaskSchedulingService::class.java)

var jobsMap: MutableMap<Long?, ScheduledFuture<*>?> =
            HashMap<Long?, ScheduledFuture<*>?>()

    fun scheduleATask(taskDefinitionBean: TaskDefinitionBean) {
        log.info("Scheduling task with job id: "
                + taskDefinitionBean.taskDefinition.mqttEventId
                + " and clock: " + taskDefinitionBean.taskDefinition.clock)
        val scheduledTask: ScheduledFuture<*>? =
            taskScheduler.schedule(taskDefinitionBean,
                PeriodicTrigger(Duration.ofSeconds(
                    taskDefinitionBean.taskDefinition.clock))
        )
        jobsMap.put(taskDefinitionBean.taskDefinition.mqttEventId, scheduledTask)
    }

    fun removeScheduledTask(jobId: Long?) {
        val scheduledTask = jobsMap.get(jobId)
        if (scheduledTask != null) {
            log.info("Removing scheduled task with job id: ")
            scheduledTask.cancel(true)
            jobsMap.remove(jobId)
        }
    }
}