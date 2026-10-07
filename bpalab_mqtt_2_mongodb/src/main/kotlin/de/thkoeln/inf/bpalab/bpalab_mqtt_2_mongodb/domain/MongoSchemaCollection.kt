package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.domain

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Unwrapped
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

data class FieldsEntry(
    val name: String,
    val type: String,
    val hidden: Boolean = false
)

@Document(collection = "trino_schema_collection")
class TrinoSchemaCollection(
//    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    var fields: List<FieldsEntry>,
    var table: String,
    @Id
    var id: String? = null
)

@Service
class TrinoSchemaService(
    val trinoSchemaCollectionRepository: TrinoSchemaCollectionRepository
){
    val log = LoggerFactory.getLogger(TrinoSchemaService::class.java)

    fun insert(trinoSchemaCollection: TrinoSchemaCollection): TrinoSchemaCollection {
        val inf = ObjectMapper().writeValueAsString(trinoSchemaCollection)
        log.info("insert: $inf")
            return trinoSchemaCollectionRepository.save(trinoSchemaCollection)
        }

    init {
        trinoSchemaCollectionRepository.deleteAll()
        insert(mqttEventStation)
        insert(mqttEventVgr)
        insert(mqttEventOrder)
        insert(mqttEventBme680)
        insert(mqttEventLdr)
    }
}

@Repository
interface TrinoSchemaCollectionRepository: CrudRepository<TrinoSchemaCollection, String> {
}

val mqttEventStation = TrinoSchemaCollection(
    listOf<FieldsEntry>(
        FieldsEntry("_id", "ObjectId", hidden = true),
        FieldsEntry(name = "_class", type = "varchar", hidden = true),
        FieldsEntry(name = "topic", type = "varchar", hidden = false),
        FieldsEntry("timestamp", "timestamp", hidden = false),
        FieldsEntry(name = "code", type = "varchar"),
        FieldsEntry(name = "active", type = "boolean"),
        FieldsEntry(name = "station", type = "varchar")
    ),
    "mqttEventStation"
)

val mqttEventVgr = TrinoSchemaCollection(
    listOf<FieldsEntry>(
        FieldsEntry("_id", "ObjectId", hidden = true),
        FieldsEntry(name = "_class", type = "varchar", hidden = true),
        FieldsEntry(name = "topic", type = "varchar", hidden = false),
        FieldsEntry("timestamp", "timestamp"),
        FieldsEntry(name = "code", type = "varchar"),
        FieldsEntry(name = "active", type = "boolean"),
        FieldsEntry(name = "station", type = "varchar"),
        FieldsEntry(name = "target", type = "varchar")
    ),
    "mqttEventVgr"
)

val mqttEventOrder = TrinoSchemaCollection(
    listOf<FieldsEntry>(
        FieldsEntry("_id", "ObjectId", hidden = true),
        FieldsEntry(name = "_class", type = "varchar", hidden = true),
        FieldsEntry(name = "topic", type = "varchar", hidden = false),
        FieldsEntry("timestamp", "timestamp"),
        FieldsEntry(name = "type", type = "varchar"),
        FieldsEntry(name = "state", type = "varchar"),
        FieldsEntry(name = "processOrderReference", type = "varchar"),
    ),
    "mqttEventOrder"
)

val mqttEventLdr = TrinoSchemaCollection(
    listOf<FieldsEntry>(
        FieldsEntry("_id", "ObjectId", hidden = true),
        FieldsEntry(name = "_class", type = "varchar", hidden = true),
        FieldsEntry(name = "topic", type = "varchar", hidden = false),
        FieldsEntry("timestamp", "timestamp"),
        FieldsEntry(name = "br", type = "double"),
        FieldsEntry(name = "ldr", type = "varchar"),
    ),
    "mqttEventLdr"
)

val mqttEventBme680 = TrinoSchemaCollection(
    listOf<FieldsEntry>(
        FieldsEntry("_id", "ObjectId", hidden = true),
        FieldsEntry(name = "_class", type = "varchar", hidden = true),
        FieldsEntry(name = "topic", type = "varchar", hidden = false),
        FieldsEntry("timestamp", "timestamp"),
        FieldsEntry(name = "aq", type = "double"),
        FieldsEntry(name = "gr", type = "double"),
        FieldsEntry(name = "p", type = "double"),
        FieldsEntry(name = "rh", type = "double"),
        FieldsEntry(name = "t", type = "double"),
        FieldsEntry(name = "h", type = "double"),
        FieldsEntry(name = "rt", type = "double"),
        FieldsEntry(name = "iaq", type = "double"),
    ),
    "mqttEventBme680"
)
