package com.hpms.userservice.dto.company;

import com.hpms.userservice.model.UserSocialLink;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyAboutInfoRequest {

    private String about;

    private String website;

    private Date foundingDate;

    private List<UserSocialLink> socialLinks;

}
