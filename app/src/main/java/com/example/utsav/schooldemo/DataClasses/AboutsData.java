package com.example.utsav.schooldemo.DataClasses;

/**
 * Created by utsav on 2/15/2016.
 */
public class AboutsData {
    private String name;
    private String email;
    private String contacts;
    private String abouts;
    private String website;
    private String longitude;
    private String latitude;

    public AboutsData(String name, String email, String contacts,
                      String abouts, String website, String longitude,
                      String latitude) {
        this.name = name;
        this.email = email;
        this.contacts = contacts;
        this.abouts = abouts;
        this.website = website;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContacts() {
        return contacts;
    }

    public String getAbouts() {
        return abouts;
    }

    public String getWebsite() {
        return website;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
