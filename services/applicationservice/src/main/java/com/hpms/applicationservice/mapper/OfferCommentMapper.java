package com.hpms.applicationservice.mapper;

import com.hpms.applicationservice.dto.OfferCommentInfo;
import com.hpms.applicationservice.model.OfferComment;
import com.hpms.applicationservice.service.client.UserServiceClient;
import com.hpms.commonlib.constants.RoleEnum;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferCommentMapper {
    private final UserServiceClient userServiceClient;

    public OfferCommentInfo offerCommentToOfferCommentInfo(OfferComment offerComment) {
        RoleEnum creatorRole = null;
        try {
            creatorRole = userServiceClient.getUserRole(offerComment.getCreatorId());
        } catch (ServiceCommunicationException ex) {
            log.error(ex.getMessage(), ex);
        }
        return OfferCommentInfo.builder()
                .id(offerComment.getId())
                .content(offerComment.getContent())
                .createdAt(offerComment.getCreatedAt())
                .updatedAt(offerComment.getUpdatedAt())
                .creatorId(offerComment.getCreatorId())
                .creatorRole(creatorRole)
                .build();
    }
}
