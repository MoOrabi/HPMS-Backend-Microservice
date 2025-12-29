package com.hpms.userservice.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {
    private UUID id;

    private String title;

    private String organization;

    private String month;

    private String year;
}
