package com.hpms.userservice.model.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "social_media")
@Getter
@Setter
public class SocialIconInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String platform;

    private String icon;

}
