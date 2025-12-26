package com.hpms.applicationservice.dto;

import com.hpms.applicationservice.constants.CommentType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationCommentResponse {

    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CommentType type;

    private String content;

    private UUID creatorId;

    private String creatorPhoto;

    private String creatorName;

}
