package com.hpms.userservice.constants;

public enum CompanySize {

    E1to10("1-10 Employees"),
    E11to50("11-50 Employees"),
    E51to100("51-100 Employees"),
    E101to250("101-250 Employees"),
    E251to500("251-500 Employees"),
    E501to750("501-750 Employees"),
    E751to1000("751-1k Employees"),
    E1001to2000("1k-2k Employees"),
    E2001to5000("2k-5k Employees"),
    E5001to10000("5k-10k Employees"),
    Egt10000("More than 10k Employees");


    public final String size;

    CompanySize(String s) {
        this.size = s;
    }

    public String getSize() {
        return size;
    }



}
