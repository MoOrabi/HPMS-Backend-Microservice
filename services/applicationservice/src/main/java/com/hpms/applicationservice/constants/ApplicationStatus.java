package com.hpms.applicationservice.constants;

import lombok.Getter;

@Getter
public enum ApplicationStatus {

    APPLIED("Applied"),
    DISQUALIFIED("Disqualified"),
    PHONE_SCREEN("Phone screen"),
    INTERVIEW("Interview"),
    ASSESSMENT("Assessment"),
    OFFER("Offer"),
    HIRED("Hired");

    private final String value;

    ApplicationStatus(String value) {
        this.value = value;
    }

}
