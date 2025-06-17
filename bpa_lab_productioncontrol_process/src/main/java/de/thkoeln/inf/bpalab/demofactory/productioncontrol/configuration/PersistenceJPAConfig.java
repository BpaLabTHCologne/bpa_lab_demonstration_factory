package de.thkoeln.inf.bpalab.demofactory.productioncontrol.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({"de.thkoeln.inf.bpalab.demofactory"})
@EntityScan({"de.thkoeln.inf.bpalab.demofactory"})
@ComponentScan({"de.thkoeln.inf.bpalab.demofactory"})
public class PersistenceJPAConfig {
}
