package com.hpms.userservice.model.jobseeker;


import com.hpms.userservice.constants.CareerLevel;
import com.hpms.userservice.model.Education;
import com.hpms.userservice.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;


@Entity
@DynamicUpdate
@DynamicInsert
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeeker extends User {

    private String firstName;

    private String lastName;

    private String mobileNumberCountryCode;

    private String mobileNumber;

    private String jobTitle;

    @CreatedDate
    private LocalDate createdDate;

    private Date birthDate;

    private String gender;

    private String profilePhoto;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private JobSeekerLocation livesIn;

    private boolean readyToRelocate;

    private String Nationality;

    private String yearsOfExperience;

    @Enumerated(EnumType.STRING)
    private CareerLevel careerLevel;

    private String minimumSalaryValue;

    private String minimumSalaryCurrency;

    private Boolean showMinimumSalary = true;

    private Set<String> jobsTypesUserInterestedIn;

    private Set<String> jobsUserInterestedIn;

    private String jobStatus;

    private Boolean searchable = true;

    private Boolean openToSuggest = true;

    private boolean receiveJobAlerts = true;

    private boolean privateAccount = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "jobSeeker")
    private Set<Certificate> certificates;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL)
    private Set<JobExperience> jobExperiences;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL)
    private Set<Education> educations;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL)
    private Set<WorkSample> workSamples;

    @Lob
    private Set<String> skills;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "jobseeker_languages",
            joinColumns = @JoinColumn(name = "job_seeker_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<Language> languages;

    @Lob
    private String about;

    private String facebookLink;

    private String linkedinLink;

    private String githubLink;

    private String CV;

    private int complete ;

//    @ManyToMany(fetch = FetchType.LAZY, cascade = {
//            CascadeType.DETACH,CascadeType.MERGE,
//            CascadeType.PERSIST,CascadeType.REFRESH
//    })
    @JoinTable(name = "jobseeker_saved_jobs",
            joinColumns = @JoinColumn(name = "job_seeker_id"),
            inverseJoinColumns = @JoinColumn(name = "job_post_id"))
    private Set<Long> savedJobsIds;

//    @OneToMany(mappedBy = "jobSeeker" , cascade = {
//            CascadeType.DETACH,CascadeType.MERGE,
//            CascadeType.PERSIST,CascadeType.REFRESH
//    })
//    private List<JobApplication> jobApplications;

    public JobSeeker(String firstName, String lastName, Date date, User user, String jobTitle) {
        super(user.getUsername(), user.getPassword(), true, false, user.getProvider(), user.getProviderId(), user.getRole(), user.getAttributes());
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdDate = LocalDate.now();
        this.jobTitle = jobTitle;
    }

    public JobSeeker(String firstName, String lastName, Date date, User user) {
        super(user.getUsername(), user.getPassword(), true, false, user.getProvider(), user.getProviderId(), user.getRole(), user.getAttributes());
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdDate = LocalDate.now();
        this.jobTitle = "Not Specified";
    }




}
