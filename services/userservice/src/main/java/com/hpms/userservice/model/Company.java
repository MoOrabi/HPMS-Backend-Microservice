package com.hpms.userservice.model;

import com.hpms.userservice.constants.CompanySize;
import com.hpms.userservice.model.company.AddRecruiterRequest;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
import java.util.Set;


@Entity
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Company extends User {

    // basic info
    private String name;

    private String tagline;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "main_location_location")
    private Location mainBranchLocation;

    private String industry;

    private long companyRank;

    // Every company has just one photo for logo and one for cover named company_id+(logo | cover)
    private String logo;

    private String coverPhoto;

    private String mobileNumberCountryCode;

    private String mobileNumber;

    private String managerFirstName;

    private String managerLastName;

    private boolean receiveNewApplicantsEmails = true;

    private boolean receiveTalentSuggestionsEmails = true;

    // about section
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private AboutCompany aboutCompany;

    // for notification
    @OneToMany
    private Set<JobSeeker> subscribers;

    // management
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Recruiter> recruiters;

    @Enumerated(EnumType.STRING)
    private CompanySize companySize;

//    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
//    private List<JobPost> jobPost;
//
//    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
//    private List<JobPost> createdJobPosts;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<AddRecruiterRequest> invitations;

    public Company(User user, String name, Location mainBranch, Set<Recruiter> recruiters) {
        super(user.getUsername(), user.getPassword(), true, false, user.getProvider(),
                user.getProviderId(), user.getRole(), user.getAttributes());
        this.name = name;
        this.recruiters = recruiters;
        this.mainBranchLocation = mainBranch;
    }

    public Company(User user, String name, String industry, Set<Recruiter> recruiters) {
        super(user.getUsername(), user.getPassword(), true, false, user.getProvider(),
                user.getProviderId(), user.getRole(), user.getAttributes());
        this.name = name;
        this.recruiters = recruiters;
        this.industry = industry;
    }



}
