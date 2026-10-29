package de.thkoeln.bpalab.fakefactory.config

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement


@Configuration
@EntityScan("de.thkoeln.bpalab.fakefactory.domain")
@EnableJpaRepositories("de.thkoeln.bpalab.fakefactory.repos")
@EnableTransactionManagement
class DomainConfig
