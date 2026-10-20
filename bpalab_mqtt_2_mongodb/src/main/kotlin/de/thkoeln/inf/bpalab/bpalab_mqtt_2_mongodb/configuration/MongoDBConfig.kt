package de.thkoeln.inf.bpalab.bpalab_mqtt_2_mongodb.configuration

import com.mongodb.Block
import com.mongodb.MongoClientSettings
import com.mongodb.connection.ClusterSettings
import com.mongodb.connection.ConnectionPoolSettings
import com.mongodb.connection.SocketSettings
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit


@Configuration
class MongoDBConfig(
) {
    private val CONNECTION_POOL_SIZE: Int = 10
    private val CONNECTION_POOL_MIN_SIZE = 1
    private val CONNECTION_POOL_MAX_IDLE_TIME: Long = 3
    private val CONNECTION_POOL_MAX_WAIT_TIME: Long = 10
    private val CONNECTION_TIMEOUT: Long = 3

    @Value("\${spring.application.name}")
    private val applicationName: String? = null


    @Bean
    fun mongoClientSettingsBuilderCustomizer(): MongoClientSettingsBuilderCustomizer {
        return MongoClientSettingsBuilderCustomizer { custom: MongoClientSettings.Builder? ->
            custom!!.applyToSocketSettings(
                Block { builder: SocketSettings.Builder? ->
                    builder!!.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                })
                .applyToConnectionPoolSettings(Block { builder: ConnectionPoolSettings.Builder? ->
                    builder!!.maxSize(CONNECTION_POOL_SIZE)
                        .minSize(CONNECTION_POOL_MIN_SIZE)
                        .maxConnectionIdleTime(CONNECTION_POOL_MAX_IDLE_TIME, TimeUnit.SECONDS)
                        .maxWaitTime(CONNECTION_POOL_MAX_WAIT_TIME, TimeUnit.SECONDS)
                })
                .applyToClusterSettings(Block { builder: ClusterSettings.Builder? ->
                    builder!!.serverSelectionTimeout(
                        CONNECTION_TIMEOUT,
                        TimeUnit.SECONDS
                    )
                })
                .applicationName(applicationName)
        }
    }

}


