package com.hpms.jobservice.constants;

public enum EmploymentType {
    PART_TIME("Part Time"),
    FULL_TIME("Full Time"),
    COMMISSION("Commission"),
    SELF_EMPLOYED("Self Employed"),
    TEMPORARY("Temporary"),
    CASUAL("Casual"),
    CONTRACT("Contract"),
    FREELANCE("Freelance"),
    INTERNSHIP("Internship");

    private final String value;

    EmploymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}