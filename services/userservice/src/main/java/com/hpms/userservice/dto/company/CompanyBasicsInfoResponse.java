package com.hpms.userservice.dto.company;

import com.hpms.userservice.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyBasicsInfoResponse {

    private UUID id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String tagline;

    @NotNull
    @NotBlank
    private Location mainBranchLocation;

    @NotNull
    @NotBlank
    private String industry;

    private long rank;

    private long subscribersNumber;

    private boolean isSubscribe;

    private String companySize;

    private String profileImage ;

    private String CoverImage ;
}
