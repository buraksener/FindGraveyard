package com.sener35gmail.burak.myapplication.Model;

/**
 * Created by Admin on 07.04.2018.
 */

public class Deceased {
    private String deceased_name;
    private String deceased_surname;
    private String deceased_birth_date;
    private String deceased_death_date;

    private String imageUrl;

    public  Deceased()
    {

    }


    public String getImageUrl() {
        return imageUrl;
    }

    public  Deceased(String deceased_name, String deceased_surname, String deceased_birth_date, String deceased_death_date, String imageUrl )
    {
        this.deceased_name=deceased_name;
        this.deceased_surname=deceased_surname;

        this.deceased_birth_date=deceased_birth_date;
        this.deceased_death_date=deceased_death_date;
        this.imageUrl=imageUrl;


    }

    public String getDeceasedName() {
        return deceased_name;
    }

    public String getDeceasedSurname() {
        return deceased_surname;
    }


    public String getDeceasedBirthDate() {
        return deceased_birth_date;
    }

    public String getDeceasedDeathDate() {
        return deceased_death_date;
    }




}
