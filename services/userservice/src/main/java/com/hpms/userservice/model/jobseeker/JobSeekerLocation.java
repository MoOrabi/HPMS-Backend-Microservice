package com.hpms.userservice.model.jobseeker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String country;

    private String state;

    private String city;

    @Column(precision = 18, scale = 15)
    private BigDecimal latitude;

    @Column(precision = 18, scale = 15)
    private BigDecimal longitude;


    @JsonIgnore
    @OneToMany(mappedBy = "livesIn")
    private Set<JobSeeker> jobSeeker;

    public JobSeekerLocation(String country, String state, String city) {
        this.city = city;
        this.country = country;
        this.state = state;
    }


}
