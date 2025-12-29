package com.hpms.applicationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreatorNameAndPhoto {
    private String name;
    private String photoUrl;
}
