package com.hpms.applicationservice.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCommentCreatorDTO {
    String name;
    String photoUrl;
}
