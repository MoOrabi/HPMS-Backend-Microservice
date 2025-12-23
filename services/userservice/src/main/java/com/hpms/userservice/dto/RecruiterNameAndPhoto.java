package com.hpms.userservice.dto;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterNameAndPhoto {
    private UUID id;

    private String title;

    private String name;

    private String photo;
}
