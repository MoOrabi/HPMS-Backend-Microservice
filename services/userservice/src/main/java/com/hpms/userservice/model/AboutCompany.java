package com.hpms.userservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AboutCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Lob
    private String about;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Location> subLocations;

    private String website;

    private Date foundingDate;

    private String speciality;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UserSocialLink> socialLinks;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Benefit> companyBenefits;

    public AboutCompany(String about,String speciality, String website) {
        this.about = about;
        this.website = website;
        this.speciality = speciality;
    }

}
