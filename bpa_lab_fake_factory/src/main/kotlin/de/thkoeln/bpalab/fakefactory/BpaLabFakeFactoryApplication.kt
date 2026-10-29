package de.thkoeln.bpalab.fakefactory

import de.thkoeln.bpalab.fakefactory.domain.TopicPayloadPreset
import de.thkoeln.bpalab.fakefactory.model.TopicPayloadDTO
import de.thkoeln.bpalab.fakefactory.service.MqttService
import de.thkoeln.bpalab.fakefactory.service.TaskDefinition
import de.thkoeln.bpalab.fakefactory.service.TaskDefinitionBean
import de.thkoeln.bpalab.fakefactory.service.TaskSchedulingService
import de.thkoeln.bpalab.fakefactory.service.TopicPayloadService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class BpaLabFakeFactoryApplication(
    val taskSchedulingService: TaskSchedulingService,
    val topicPayloadService: TopicPayloadService,
    val mqttService: MqttService
): CommandLineRunner {
    private val log = LoggerFactory.getLogger(BpaLabFakeFactoryApplication::class.java)

    override fun run(vararg args: String) {
        log.info("Starting Fake Factory application")
        TopicPayloadPreset(
            taskSchedulingService = taskSchedulingService,
            topicPayloadService = topicPayloadService,
            mqttService = mqttService
        )
    }
}

fun main(args: Array<String>) {
    runApplication<BpaLabFakeFactoryApplication>(*args)
}

