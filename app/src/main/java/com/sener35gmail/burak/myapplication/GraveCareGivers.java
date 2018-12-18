package com.sener35gmail.burak.myapplication;

/**
 * Created by Admin on 11.11.2018.
 */

public class GraveCareGivers {
    private  String name;
    private String Surname;
    private String phoneNumber;

    public GraveCareGivers() {
    }

    public GraveCareGivers(String name, String surname, String phoneNumber) {
        this.name = name;
        this.Surname = surname;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {

        return name;
    }

    public String getSurname() {
        return Surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
