package com.hpms.applicationservice.updater;

import com.hpms.applicationservice.dto.OfferDTO;
import com.hpms.applicationservice.model.Offer;
import com.hpms.applicationservice.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class OfferUpdater implements BiFunction<Offer, OfferDTO, Offer> {

    private final OfferRepository offerRepository;

    @Override
    public Offer apply(Offer offer, OfferDTO offerDTO) {

        offer.setUpdatedAt(LocalDateTime.now());
        offer.setDeadlineAt(offerDTO.getDeadlineAt());

        return offerRepository.save(offer);
    }
}
