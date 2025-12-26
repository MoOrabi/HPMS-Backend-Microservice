package com.hpms.applicationservice.dto;

import com.hpms.applicationservice.constants.OfferStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferResponse {

    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private LocalDateTime deadlineAt;

    private String documentByCompany;

    private String documentByJobSeeker;

    private double sizeOfDocumentByCompany;

    private double sizeOfDocumentByJobSeeker;

    private OfferStatus status;

    private UUID applicationId;

    private Set<OfferCommentInfo> comments;

}
