package com.hpms.userservice.dto.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanySubscriptionChoices {

    private boolean receiveNewApplicantsEmails;
    private boolean receiveTalentSuggestionsEmails;

}
