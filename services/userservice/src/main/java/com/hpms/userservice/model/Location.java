package com.hpms.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String country;

    private String state;

    private String city;

    private String street;

    @Column(precision = 18, scale = 15)
    private BigDecimal latitude;

    @Column(precision = 18, scale = 15)
    private BigDecimal longitude;

    @JsonIgnore
    @ManyToOne
    private AboutCompany user;

    public Location(String country, String state, String city) {
        this.city = city;
        this.country = country;
        this.state = state;
    }

    public Location(String country, String state, String city, String street) {
        this.city = city;
        this.country = country;
        this.state = state;
        this.street = street;
    }


}
