package com.hpms.userservice.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDTO {
    private long id;

    private String languageName;

    private String languageLevel;
}
