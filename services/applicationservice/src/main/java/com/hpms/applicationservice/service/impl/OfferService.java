package com.hpms.applicationservice.service.impl;

import com.hpms.applicationservice.constants.OfferStatus;
import com.hpms.applicationservice.constants.TimelineEventType;
import com.hpms.applicationservice.dto.OfferCommentDTO;
import com.hpms.applicationservice.dto.OfferCommentInfo;
import com.hpms.applicationservice.dto.OfferDTO;
import com.hpms.applicationservice.dto.OfferResponse;
import com.hpms.applicationservice.mapper.OfferCommentMapper;
import com.hpms.applicationservice.model.JobApplication;
import com.hpms.applicationservice.model.Offer;
import com.hpms.applicationservice.model.OfferComment;
import com.hpms.applicationservice.model.TimelineEvent;
import com.hpms.applicationservice.repository.JobApplicationRepository;
import com.hpms.applicationservice.repository.OfferCommentRepository;
import com.hpms.applicationservice.repository.OfferRepository;
import com.hpms.applicationservice.repository.TimelineEventRepository;
import com.hpms.applicationservice.service.client.UserServiceClient;
import com.hpms.applicationservice.updater.OfferUpdater;
import com.hpms.applicationservice.util.ApplicationUtils;
import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import com.hpms.commonlib.util.PublicJwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ApplicationUtils applicationUtils;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private OfferCommentRepository offerCommentRepository;

    @Autowired
    private OfferUpdater offerUpdater;

    @Autowired
    private OfferCommentMapper offerCommentMapper;

    @Autowired
    private TimelineEventRepository timelineRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    public static HashSet<String> cvExtensions =
            new HashSet<>(Arrays.asList(MediaType.APPLICATION_PDF_VALUE));

    public ApiResponse<?> addOffer(String token, OfferDTO offerDTO) {

        ApiResponse<?> getJobApplicationResponse = applicationUtils.getJobApplication(offerDTO.getApplicationId());

        if(!getJobApplicationResponse.isOk()){
            return getJobApplicationResponse;
        }else {
            JobApplication jobApplication = (JobApplication) getJobApplicationResponse.getBody();
            UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                if(jobApplication.getOffer()!=null){
                    return ApiResponse.builder()
                            .ok(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("There is already an offer provided to this application")
                            .build();
                }
                Offer offer = Offer.builder()
                        .creatorId(userId)
                        .status(OfferStatus.Delivered)
                        .deadlineAt(offerDTO.getDeadlineAt())
                        .application(jobApplication)
                        .build();

                Offer savedOffer = offerRepository.save(offer);
                offerDTO.setId(savedOffer.getId());
                TimelineEvent timelineEvent = TimelineEvent
                        .builder()
                        .type(TimelineEventType.Offer)
                        .application(jobApplication)
                        .content(savedOffer)
                        .creatorId(userId)
                        .build();
                timelineRepository.save((timelineEvent));
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Offer added successfully")
                        .body(offerDTO)
                        .build();
            }
            
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to add offers to this application")
                    .build();
        }
    }

    public ApiResponse<?> addOfferEmployerFile(String token, MultipartFile offerFile, UUID offerId) throws Exception {
        return applicationUtils.addFileToOffer(token, offerFile, "emp-offer", 5*1024,
                "app-files/emp-offer", "Offer document Added Successfully",
                cvExtensions, offerId);

    }

    public ApiResponse<?> addOfferSignedFile(String token, MultipartFile signedFile, UUID offerId) throws Exception {
        return applicationUtils.addFileToOffer(token, signedFile, "js-offer", 5*1024,
                "app-files/js-offer", "Signed document Added Successfully",
                cvExtensions, offerId);

    }

    public ApiResponse<?> editOffer(String token, UUID offerId, OfferDTO offerDTO) {

        ApiResponse<?> getJobApplicationResponse = applicationUtils.getJobApplication(offerDTO.getApplicationId());

        if(!getJobApplicationResponse.isOk()){
            return getJobApplicationResponse;
        }else {
            JobApplication jobApplication = (JobApplication) getJobApplicationResponse.getBody();
            UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                Optional<Offer> optionalOffer = offerRepository.findById(offerId);

                if(optionalOffer.isEmpty()){
                    return ApiResponse.builder()
                            .ok(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("There is no offer with such an id")
                            .build();
                }else {
                    Offer offer = optionalOffer.get();
                    offerUpdater.apply(offer, offerDTO);
                    return ApiResponse.builder()
                            .ok(true)
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Offer updated successfully")
                            .body(offerDTO)
                            .build();
                }
            }
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to edit this offer")
                    .build();
        }
    }

    public ApiResponse<?> editOfferStatus(String token, UUID offerId, OfferStatus offerStatus) {


        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
Optional<Offer> optionalOffer = offerRepository.findById(offerId);
            if(optionalOffer.isEmpty()) {
                return ApiResponse.builder()
                        .ok(false)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("There is no offer with such an id")
                        .build();
            }else{
                Offer offer = optionalOffer.get();
                JobApplication jobApplication = offer.getApplication();

                if (applicationUtils.checkJobSeekerIsConcernedWithApplication(userId, jobApplication)){
                    offer.setStatus(offerStatus);
                    return ApiResponse.builder()
                            .ok(true)
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Offer status updated successfully")
                            .body(offerRepository.save(offer))
                            .build();

                }
            }


        return ApiResponse.builder()
                .ok(false)
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("You have no authority to edit this offer")
                .build();

    }

    public ApiResponse<?> deleteOffer(String token, UUID offerId) {


        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
        Optional<Offer> optionalOffer = offerRepository.findById(offerId);

        if(optionalOffer.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no offer with such an id")
                    .build();
        }else {
            Offer offer = optionalOffer.get();
            JobApplication jobApplication = offer.getApplication();
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                System.out.println(offer.getId()+" "+offer.getStatus());
                offerRepository.deleteById(offerId);
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Offer Deleted successfully")
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to delete this offer")
                    .build();

        }

    }

    public ApiResponse<?> getOfferForApplication(String token, UUID applicationId) {

        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
Optional<JobApplication> optionalApplication = jobApplicationRepository.findById(applicationId);

            if(optionalApplication.isEmpty()){
                return ApiResponse.builder()
                        .ok(false)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("There is no application with such an id")
                        .build();
            }else {
                JobApplication jobApplication = optionalApplication.get();
                if (applicationUtils.checkUserIsConcernedWithApplication(userId, jobApplication)){
                    Offer offer = jobApplication.getOffer();
                    if(offer == null){
                        return ApiResponse.builder()
                                .ok(true)
                                .status(HttpStatus.ACCEPTED.value())
                                .build();
                    }
                    Set<OfferComment> comments = offerCommentRepository.findByOfferId(offer.getId());
                    Set<OfferCommentInfo> commentsInfo = new HashSet<>();
                    for (OfferComment comment :
                            comments) {
                        commentsInfo.add(offerCommentMapper.offerCommentToOfferCommentInfo(comment));
                    }
                    String evaluationCreatorFirstAndLastName = null;
                    try {
                        evaluationCreatorFirstAndLastName = userServiceClient.getCreatorName(offer.getCreatorId());
                    } catch (ServiceCommunicationException e) {
                        log.error(e.getMessage(), e);
                    }
                    OfferResponse offerResponse = OfferResponse.builder()
                            .id(offer.getId())
                            .createdAt(offer.getCreatedAt())
                            .updatedAt(offer.getUpdatedAt())
                            .deadlineAt(offer.getDeadlineAt())
                            .applicationId(offer.getApplication().getId())
                            .documentByCompany(offer.getDocumentByCompany())
                            .sizeOfDocumentByCompany(offer.getSizeOfDocumentByCompany())
                            .documentByJobSeeker(offer.getDocumentByJobSeeker())
                            .sizeOfDocumentByJobSeeker(offer.getSizeOfDocumentByJobSeeker())
                            .status(offer.getStatus())
                            .comments(commentsInfo)
                            .createdBy(evaluationCreatorFirstAndLastName)
                            .build();
                    return ApiResponse.builder()
                            .ok(true)
                            .status(HttpStatus.ACCEPTED.value())
                            .body(offerResponse)
                            .build();
                }

                return ApiResponse.builder()
                        .ok(false)
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("You have no authority to access this offer")
                        .build();



        }
    }

    public ApiResponse<?> addOfferComment(String token, OfferCommentDTO offerCommentDTO) {

        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
        Optional<Offer> optionalOffer = offerRepository.findById(offerCommentDTO.getOfferId());
        if(optionalOffer.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("There is no offer with provided id")
                    .build();
        }else {
            Offer offer = optionalOffer.get();
            JobApplication jobApplication = offer.getApplication();
            if (applicationUtils.checkUserIsConcernedWithApplication(userId, jobApplication)){
                OfferComment offerComment = OfferComment.builder()
                                .content(offerCommentDTO.getContent())
                                .creatorId(userId)
                                .offer(offer)
                                .build();

                offerCommentRepository.save(offerComment);

                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Offer comment added successfully")
                        .body(offerCommentDTO)
                        .build();
            }
        }


        return ApiResponse.builder()
                .ok(false)
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("You have no authority to add offers to this application")
                .build();
    }

    public ApiResponse<?> markOfferAndOfferCommentsViewed(String token, UUID appId) {

        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
        Optional<JobApplication> optionalApplication = jobApplicationRepository.findById(appId);

        if(optionalApplication.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no application with such an id")
                    .build();
        }else {
            JobApplication jobApplication = optionalApplication.get();
            if (applicationUtils.checkJobSeekerIsConcernedWithApplication(userId, jobApplication)){
                Offer offer = jobApplication.getOffer();
                if(offer != null){
                    offer.setViewed(true);
                    offerRepository.save(offer);
                    Set<OfferComment> comments = offer.getComments();
                    if(comments != null) {
                        for (OfferComment comment :
                                comments) {
                            comment.setViewed(true);
                        }
                        offerCommentRepository.saveAll(comments);
                    }
                    return ApiResponse.builder()
                            .ok(true)
                            .status(HttpStatus.ACCEPTED.value())
                            .message("The offer and its comments updated successfully")
                            .build();
                }
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.OK.value())
                        .message("No offers yet")
                        .build();

            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to access evaluations of this application")
                    .build();

        }
    }

}
