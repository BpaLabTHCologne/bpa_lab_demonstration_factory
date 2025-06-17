package de.thkoeln.inf.bpalab.demofactory.common.repos;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BikeModelRepository extends JpaRepository<BikeModel, String> {

    boolean existsByTitleIgnoreCase(String title);

}
