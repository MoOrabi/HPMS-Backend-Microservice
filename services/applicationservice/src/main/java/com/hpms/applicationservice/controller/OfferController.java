package com.hpms.applicationservice.controller;

import com.hpms.applicationservice.constants.OfferStatus;
import com.hpms.applicationservice.dto.OfferCommentDTO;
import com.hpms.applicationservice.dto.OfferDTO;
import com.hpms.applicationservice.service.impl.OfferService;
import com.hpms.commonlib.dto.ApiResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Log
@RestController
@RequestMapping("/api/apps")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @PostMapping("/offer")
    public ApiResponse<?> addOffer(@RequestHeader(name = "Authorization") String token,
                                   @RequestBody OfferDTO offerDTO) {
        return offerService.addOffer(token, offerDTO);
    }

    @PutMapping(value = "offer/employer-file",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> addOfferEmployerFile(@RequestHeader(name = "Authorization") String token,
                                               @RequestPart(value = "file") final MultipartFile offerFile,
                                               @RequestParam UUID offerId) throws Exception {
        return offerService.addOfferEmployerFile(token, offerFile, offerId);
    }

    @PutMapping(value = "offer/signed-file",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> addOfferSignedFile(@RequestHeader(name = "Authorization") String token,
                                             @RequestPart(value = "file") final MultipartFile signedFile,
                                             @RequestParam UUID offerId) throws Exception {
        return offerService.addOfferSignedFile(token, signedFile, offerId);
    }

    @PutMapping("offer")
    public ApiResponse<?> editOffer(@RequestHeader(name = "Authorization") String token,
                                    @RequestParam UUID offerId,
                                    @RequestBody OfferDTO offerDTO) {
        return offerService.editOffer(token, offerId, offerDTO);
    }

    @PutMapping("offer-status")
    public ApiResponse<?> editOfferStatus(@RequestHeader(name = "Authorization") String token,
                                          @RequestParam UUID offerId,
                                          @RequestParam OfferStatus offerStatus) {
        return offerService.editOfferStatus(token, offerId, offerStatus);
    }

    @DeleteMapping("offer")
    public ApiResponse<?> deleteOffer(@RequestHeader(name = "Authorization") String token,
                                      @RequestParam UUID offerId) {
        return offerService.deleteOffer(token, offerId);
    }

    @GetMapping("offer")
    public ApiResponse<?> getOfferForApplication(@RequestHeader(name = "Authorization") String token,
                                                 @RequestParam UUID applicationId) {
        return offerService.getOfferForApplication(token, applicationId);
    }

    @PostMapping("/offer-comment")
    public ApiResponse<?> addOfferComment(@RequestHeader(name = "Authorization") String token,
                                   @RequestBody OfferCommentDTO offerCommentDTO) {
        return offerService.addOfferComment(token, offerCommentDTO);
    }

    @PutMapping("/offer/mark-viewed")
    public ApiResponse<?> markEvaluationsViewed(@RequestHeader(name = "Authorization") String token,
                                                @RequestParam UUID appId) {
        return offerService.markOfferAndOfferCommentsViewed(token, appId);
    }
}
