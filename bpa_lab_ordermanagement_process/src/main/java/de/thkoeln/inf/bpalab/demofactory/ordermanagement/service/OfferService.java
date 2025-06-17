package de.thkoeln.inf.bpalab.demofactory.ordermanagement.service;

import de.thkoeln.inf.bpalab.demofactory.common.domain.BikeModel;
import de.thkoeln.inf.bpalab.demofactory.common.repos.BikeModelRepository;
import de.thkoeln.inf.bpalab.demofactory.common.dto.BikeModelDTO;
import de.thkoeln.inf.bpalab.demofactory.ordermanagement.dto.OfferDTO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class OfferService {

    private final BikeModelRepository bikeModelRepository;

    public OfferService(final BikeModelRepository bikeModelRepository) {
        this.bikeModelRepository = bikeModelRepository;
    }

    public List<BikeModelDTO> findAll() {
        final List<BikeModel> bikeModels = bikeModelRepository.findAll(Sort.by("title"));
        return bikeModels.stream()
                .map(bikeModel -> mapToDTO(bikeModel, new BikeModelDTO()))
                .toList();
    }

    public OfferDTO getOfferDTO() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.offerBikeModelList = new ArrayList<BikeModelDTO>(findAll());
        return offerDTO;
    }

    public BikeModelDTO get(final String title) throws IOException {
        return bikeModelRepository.findById(title)
                .map(bikeModel -> mapToDTO(bikeModel, new BikeModelDTO()))
                .orElseThrow(IOException::new);
    }

    private BikeModelDTO mapToDTO(final BikeModel bikeModel, final BikeModelDTO bikeModelDTO) {
        bikeModelDTO.title = bikeModel.getTitle();
        bikeModelDTO.weight = bikeModel.getWeight();
        bikeModelDTO.color = bikeModel.getColor();
        return bikeModelDTO;
    }

    private BikeModel mapToEntity(final BikeModelDTO bikeModelDTO, final BikeModel bikeModel) {
        bikeModelDTO.title = bikeModel.getTitle();
        bikeModel.setWeight(bikeModelDTO.weight);
        bikeModel.setColor(bikeModelDTO.color);
        return bikeModel;
    }

}
