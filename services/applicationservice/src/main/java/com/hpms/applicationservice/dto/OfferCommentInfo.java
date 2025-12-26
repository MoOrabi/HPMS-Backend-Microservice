package com.hpms.applicationservice.dto;

import com.hpms.commonlib.constants.RoleEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferCommentInfo {

    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UUID creatorId;

    private String createdBy;

    private RoleEnum creatorRole;

    private String content;

}
