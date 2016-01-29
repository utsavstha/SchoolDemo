package com.example.utsav.schooldemo.DataClasses;

/**
 * Created by utsav on 1/28/2016.
 */
public class ContactsData {
    private String name_contacts;
    private String designation;
    private String email;
    private String phone;

    public ContactsData(String name, String designation, String email, String phone) {
        this.name_contacts = name;
        this.designation = designation;
        this.email = email;
        this.phone = phone;
    }

    public String getNameContacts() {
        return name_contacts;
    }

    public String getDesignation() {
        return designation;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
