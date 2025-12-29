package com.hpms.applicationservice.mapper;

import com.hpms.applicationservice.dto.ApplicationCommentResponse;
import com.hpms.applicationservice.dto.CreatorNameAndPhoto;
import com.hpms.applicationservice.model.ApplicationComment;
import com.hpms.applicationservice.service.client.UserServiceClient;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationCommentMapper {
    private final UserServiceClient userServiceClient;


    public ApplicationCommentResponse ApplicationCommentToResponse(ApplicationComment applicationComment) {

        String creatorName, creatorPhoto;
        CreatorNameAndPhoto applicationCommentCreatorDTO = CreatorNameAndPhoto
                .builder().build();
        try {
            applicationCommentCreatorDTO = userServiceClient.getCreatorNameAndPhoto(applicationComment.getCreatorId());
        } catch (ServiceCommunicationException e) {
            log.error(e.getMessage(), e);
        }
        creatorName = applicationCommentCreatorDTO.getName();
        creatorPhoto = applicationCommentCreatorDTO.getPhotoUrl();

        return ApplicationCommentResponse.builder()
                .id(applicationComment.getId())
                .content(applicationComment.getContent())
                .createdAt(applicationComment.getCreatedAt())
                .updatedAt(applicationComment.getUpdatedAt())
                .creatorId(applicationComment.getCreatorId())
                .creatorName(creatorName)
                .creatorPhoto(creatorPhoto)
                .type(applicationComment.getType())
                .build();
    }
}
