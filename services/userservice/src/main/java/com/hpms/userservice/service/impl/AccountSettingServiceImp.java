package com.hpms.userservice.service.impl;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.dto.DeleteUserRelatedEvent;
import com.hpms.userservice.model.DeletionRequest;
import com.hpms.userservice.model.User;
import com.hpms.userservice.repository.DeletionRequestRepository;
import com.hpms.userservice.repository.UserRepository;
import com.hpms.userservice.service.AccountSettingService;
import com.hpms.userservice.service.DeleteUserRelatedDataProducerService;
import com.hpms.userservice.utils.FrequentlyUsed;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log
@Service
@RequiredArgsConstructor
public class AccountSettingServiceImp implements AccountSettingService {


    private final UserRepository userRepository;

    private final DeletionRequestRepository deletionRequestRepository;

    private final FrequentlyUsed frequentlyUsed;

    private final DeleteUserRelatedDataProducerService deleteUserRelatedDataProducerService;

    public ApiResponse<?> deleteAccount(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        User user = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            user = (User) isThereUserFromToken.getBody();
        }

        if (user.isDeleted()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("User is not found.")
                    .build();
        } else {
            user.setDeleted(true);
            if (user.getDeletionRequest() == null) {
                user.setDeletionRequest(new DeletionRequest(user));
            }
            user.getDeletionRequest().setModificationTime(LocalDate.now());
            user.getDeletionRequest().setValid(true);
            userRepository.save(user);
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .message("User successfully deleted.")
                    .build();
        }
    }

    public ApiResponse<?> undeleteAccount(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        User user = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            user = (User) isThereUserFromToken.getBody();
        }

        if (user.isDeleted()) {
            user.setDeleted(false);
            user.getDeletionRequest().setModificationTime(LocalDate.now());
            user.getDeletionRequest().setValid(false);
            userRepository.save(user);
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .message("User successfully undeleted.")
                    .build();
        } else {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("User is not deleted.")
                    .build();
        }
    }

    public ApiResponse<?> deleteAccountData(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no user for the entered id")
                    .build();
        }
        User user = optionalUser.get();
        if (user.isDeleted()) {
            userRepository.delete(user);
            deleteUserRelatedDataProducerService.sendDeleteEvent(
                    new DeleteUserRelatedEvent(user.getId(), user.getRole())
            );
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .message("User data successfully deleted.")
                    .build();
        } else {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("User is not deleted.")
                    .build();
        }
    }

    public ApiResponse<?> deleteAccountData(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        User user = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            user = (User) isThereUserFromToken.getBody();
        }

        if (user.isDeleted()) {
            userRepository.delete(user);
            deleteUserRelatedDataProducerService.sendDeleteEvent(
                    new DeleteUserRelatedEvent(user.getId(), user.getRole())
            );
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .message("User data successfully deleted.")
                    .build();
        } else {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("User is not deleted.")
                    .build();
        }
    }


//    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void commitUsersDeletion() {
        List<User> usersToDelete = deletionRequestRepository.getDeletedUserFor30Days(LocalDate.now());
        System.out.println(LocalDate.now());
        for (User user :
                usersToDelete) {
            System.out.println(user.getId());
            deleteAccountData(user.getId());
        }
    }

    public ApiResponse<?> setNotificationsAlertsStatus(String token, boolean value) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        User user = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            user = (User) isThereUserFromToken.getBody();
        }

        user.setReceiveNotifications(value);
        userRepository.save(user);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Receive Notifications Status changed successfully")
                .build();
    }

    public ApiResponse<?> getNotificationsAlertsStatus(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        User user = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            user = (User) isThereUserFromToken.getBody();
        }

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(user.isReceiveNotifications())
                .build();
    }
}
