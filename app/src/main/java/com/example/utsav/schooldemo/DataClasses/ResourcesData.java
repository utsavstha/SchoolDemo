package com.example.utsav.schooldemo.DataClasses;

/**
 * Created by utsav on 1/28/2016.
 */
public class ResourcesData {
    private String title;
    private String url;
    private String day;
    private String month;
    private String year;

    public ResourcesData(String title, String year, String url, String day, String month) {
        this.title = title;
        this.year = year;
        this.url = url;
        this.day = day;
        this.month = month;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }
}
