package de.thkoeln.inf.bpalab.demofactory.ordermanagement.repos;

import de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain.BikeModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BikeModelRepository extends JpaRepository<BikeModel, String> {

    boolean existsByTitleIgnoreCase(String title);

}
