package com.hpms.userservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ForgetPasswordRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    private String token;

    private LocalDateTime expiredDate;

    private boolean valid;

    public ForgetPasswordRequest(String email, String token, LocalDateTime expiredDate) {
        this.email = email;
        this.token = token;
        this.expiredDate = expiredDate;
        this.valid = true;
    }

}

