package com.hpms.userservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.service.impl.AccountSettingServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/settings")
public class AccountSettingController {

    @Autowired
    private AccountSettingServiceImp settingService;

    @PostMapping("/delete-account")
    public ApiResponse<?> deleteAccount(@RequestHeader(name = "Authorization") String token) {
        return settingService.deleteAccount(token);
    }

    @PostMapping("/undelete-account")
    public ApiResponse<?> unDeleteAccount(@RequestHeader(name = "Authorization") String token) {
        return settingService.undeleteAccount(token);
    }

//    @PostMapping("/delete-account-data")
//    public ApiResponse<?> deleteAccountData(@RequestHeader(name="Authorization") String token){
//        return settingService.deleteAccountData(token);
//    }

    @PostMapping(value = "/receive-notifications-status")
    public ApiResponse<?> setNotificationsAlertsStatus(@RequestHeader(name = "Authorization") String token,
                                                       @RequestParam(name = "value") boolean value) {
        return settingService.setNotificationsAlertsStatus(token, value);
    }

    @GetMapping(value = "/receive-notifications-status")
    public ApiResponse<?> getNotificationsAlertsStatus(@RequestHeader(name = "Authorization") String token) {
        return settingService.getNotificationsAlertsStatus(token);
    }
}
