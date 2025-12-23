package com.hpms.userservice.dto;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostCreatorDTO {
    private UUID id;
    private String title;
    private String imageUrl;
    private String username;
    private String name;
}
