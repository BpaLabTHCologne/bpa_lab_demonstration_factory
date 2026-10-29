package de.thkoeln.bpalab.fakefactory.repos

import de.thkoeln.bpalab.fakefactory.domain.TopicPayload
import org.springframework.data.jpa.repository.JpaRepository


interface TopicPayloadRepository: JpaRepository<TopicPayload, Long> {
    fun existsByTopic(topic: String): Boolean
}
