package com.hpms.userservice.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerLocationDTO {

    private long id;

    private String country;

    private String state;

    private String city;

    private BigDecimal latitude;

    private BigDecimal longitude;
}
