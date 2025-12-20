package com.hpms.userservice.dto;


import com.hpms.userservice.model.Company;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.model.User;
import lombok.*;

import java.util.Date;

import static com.hpms.userservice.utils.AppConstants.ACCESS_EXPATRIATION_TIME;


@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
public class SuccessLoginRes {

    private String username;
    private String firstName;
    private String userId;
    private String userRole;
    private String accessToken;
    private String refreshToken;
    private Date expirationDate;
    private boolean isDeleted;


    public static SuccessLoginRes getSuccessfulResponse(User user, String accessToken, String refreshToken) {
        return SuccessLoginRes.builder()
                .username(user.getUsername())
                .userId(user.getId().toString())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expirationDate(new Date(System.currentTimeMillis() + ACCESS_EXPATRIATION_TIME))
                .isDeleted(user.isDeleted())
                .userRole(user.getRole().name())
                .build();
    }

    public static SuccessLoginRes getSuccessfulResponse(Company user, String accessToken, String refreshToken) {
        return SuccessLoginRes.builder()
                .username(user.getUsername())
                .userId(user.getId().toString())
                .firstName(user.getManagerFirstName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expirationDate(new Date(System.currentTimeMillis() + ACCESS_EXPATRIATION_TIME))
                .isDeleted(user.isDeleted())
                .build();
    }

    public static SuccessLoginRes getSuccessfulResponse(JobSeeker user, String accessToken, String refreshToken) {
        return SuccessLoginRes.builder()
                .username(user.getUsername())
                .userId(user.getId().toString())
                .firstName(user.getFirstName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expirationDate(new Date(System.currentTimeMillis() + ACCESS_EXPATRIATION_TIME))
                .isDeleted(user.isDeleted())
                .build();
    }

    public static SuccessLoginRes SuccessRegisterResponse(User user) {
        SuccessLoginRes sucResponse = SuccessLoginRes.builder()
                .username(user.getUsername())
                .userId(null)
                .firstName(null)
                .accessToken(null)
                .refreshToken(null)
                .expirationDate(null)
                .build();
        return sucResponse;
    }

}
