package com.hpms.userservice.model.shared;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryCode {

    @Id
    private int id;
    private String iso;
    private String name;
    private String niceName;
    private String iso3;
    private String numCode;
    private String phoneCode;
}
