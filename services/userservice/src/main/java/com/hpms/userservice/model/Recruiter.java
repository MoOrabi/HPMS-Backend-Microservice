package com.hpms.userservice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hpms.userservice.constants.AuthProviders;
import com.hpms.commonlib.constants.RoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static com.hpms.userservice.utils.AppConstants.LOCAL_PROVIDER_ID;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Recruiter extends User {

    private String firstName;

    private String lastName;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    private String phoneNumberCountryCode;

    private String phoneNumber;

    private String jobTitle;

    @CreatedDate
    private LocalDateTime profileCreatedAt;

    // Every Recruiter has just one photo for profile and one for cover named recruiter_id+(logo | cover)
    private String profilePhoto;

    private boolean receiveNewApplicantsEmails = true;

    private boolean receiveTalentSuggestionsEmails = true;

//    @ManyToMany(mappedBy = "recruitersTeam")
//    private List<JobPost> jobPosts;
//
//    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
//    private List<JobPost> createdJobPosts;

    public Recruiter(String firstName, String lastName, Company company, String phoneNumberCountryCode,
                     String phoneNumber, String jobTitle, LocalDateTime profileCreatedAt,
                     String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.phoneNumberCountryCode = phoneNumberCountryCode;
        this.phoneNumber = phoneNumber;
        this.jobTitle = jobTitle;
        this.profileCreatedAt = profileCreatedAt;
        this.setPassword(password);
        this.setLocked(false);
        this.setEnabled(true);
        this.setUsername(username);
        this.setRole(RoleEnum.ROLE_RECRUITER);
        this.setProvider(AuthProviders.local);
        this.setProviderId(LOCAL_PROVIDER_ID);
    }

}
