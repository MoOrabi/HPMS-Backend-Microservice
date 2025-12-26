package com.hpms.applicationservice.dto;

import com.hpms.applicationservice.constants.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO {

    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deadlineAt;

    private String documentByCompany;

    private String documentByJobSeeker;

    private OfferStatus status = OfferStatus.Delivered;

    private UUID applicationId;

}
