package com.hpms.applicationservice.mapper;

import com.hpms.applicationservice.dto.ApplicationCommentCreatorDTO;
import com.hpms.applicationservice.dto.ApplicationCommentResponse;
import com.hpms.applicationservice.model.ApplicationComment;
import com.hpms.commonlib.constants.RoleEnum;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public class ApplicationCommentMapper {


    public ApplicationCommentResponse ApplicationCommentToResponse(ApplicationComment applicationComment) {

        String creatorName = null, creatorPhoto = null;
        ApplicationCommentCreatorDTO applicationCommentCreatorDTO = ApplicationCommentCreatorDTO
                .builder().build();
        creatorName = applicationCommentCreatorDTO.getName();
        creatorPhoto = applicationCommentCreatorDTO.getPhotoUrl();

//        if(applicationComment.getCreator().getRole().equals(RoleEnum.ROLE_COMPANY)){
//            Company company = companyRepository.findById(applicationComment.getCreator().getId()).get();
//            creatorName = company.getManagerFirstName() + " " + company.getManagerLastName();
//            creatorPhoto = company.getLogo();
//        } else if (applicationComment.getCreator().getRole().equals(RoleEnum.ROLE_RECRUITER)) {
//
//            Recruiter recruiter = recruiterRepository.findById(applicationComment.getCreator().getId()).get();
//            creatorName = recruiter.getFirstName() + " " + recruiter.getLastName();
//            creatorPhoto = recruiter.getProfilePhoto();
//        }

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
