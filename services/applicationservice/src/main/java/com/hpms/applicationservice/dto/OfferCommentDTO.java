package com.hpms.applicationservice.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferCommentDTO {

    private UUID offerId;

    private String content;

}
