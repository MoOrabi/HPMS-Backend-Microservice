package com.hpms.applicationservice.dto;

import com.hpms.applicationservice.constants.CommentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationCommentDTO {

    private UUID id;

    private CommentType type;

    private String content;

    private UUID applicationId;

}
