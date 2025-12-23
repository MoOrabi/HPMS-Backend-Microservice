package com.hpms.userservice.service;

import com.hpms.commonlib.dto.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface AccountSettingService {


    public ApiResponse<?> deleteAccount(String token);

    public ApiResponse<?> undeleteAccount(String token);

    public ApiResponse<?> deleteAccountData(UUID id);


    public void commitUsersDeletion();
    public ApiResponse<?> setNotificationsAlertsStatus(String token, boolean value);

    public ApiResponse<?> getNotificationsAlertsStatus(String token);

}
