package com.hpms.userservice.model.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hpms.userservice.model.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AddRecruiterRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    private String recruiterEmail;

    private String jobTitle;

    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    private LocalDateTime usedAt;

    private boolean valid;

    public AddRecruiterRequest(Company company, String recruiterEmail, String jobTitle, String token, LocalDateTime expiredDate) {
        this.company = company;
        this.recruiterEmail = recruiterEmail;
        this.jobTitle = jobTitle;
        this.token = token;
        this.expirationDate = expiredDate;
        this.valid = true;
        this.createdAt = LocalDateTime.now();
    }
}
