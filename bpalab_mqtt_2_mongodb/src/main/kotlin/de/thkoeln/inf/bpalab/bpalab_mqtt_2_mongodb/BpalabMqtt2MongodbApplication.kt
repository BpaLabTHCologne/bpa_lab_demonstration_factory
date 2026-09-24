package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BpalabMqtt2MongodbApplication: CommandLineRunner{
    private val log = LoggerFactory.getLogger(BpalabMqtt2MongodbApplication::class.java)

    override fun run(vararg args: String) {
        log.info("Starting BPALAB 2 MQTT application")
        args.forEach { log.info("Application args: $it") }
    }
}

fun main(args: Array<String>) {
    runApplication<BpalabMqtt2MongodbApplication>(*args)
}
