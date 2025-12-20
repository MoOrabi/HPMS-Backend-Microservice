package com.hpms.userservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "benefits")
@Getter
@Setter
public class Benefit {

    @Id
    private long id;

    @Column(name = "benefit")
    private String benefit;

    private String icon;

}
