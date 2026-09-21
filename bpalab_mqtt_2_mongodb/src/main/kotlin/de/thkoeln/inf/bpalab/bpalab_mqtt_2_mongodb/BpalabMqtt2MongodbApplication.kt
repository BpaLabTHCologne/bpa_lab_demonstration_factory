package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BpalabMqtt2MongodbApplication

fun main(args: Array<String>) {
    runApplication<BpalabMqtt2MongodbApplication>(*args)
}
