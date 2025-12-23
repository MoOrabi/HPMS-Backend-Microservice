package com.hpms.jobservice.constants;

public enum JobType {
    ON_SITE("On Site"),
    HYBRID("Hybrid"),
    REMOTE("Remote");
    private final String value;

     JobType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
