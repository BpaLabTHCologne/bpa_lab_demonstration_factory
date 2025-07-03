package de.thkoeln.inf.bpalab.demofactory.common.repos;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeComponent;
import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BikeComponentRepository extends JpaRepository<BikeComponent, String> {
    List<BikeComponent> getAllByBikeModel(BikeModel bike);
}
