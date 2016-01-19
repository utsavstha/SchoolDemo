package com.example.utsav.schooldemo;

/**
 * Created by utsav on 1/19/2016.
 */
public class DownloadData {
    private String title;
    private String link;
    private String size;
    private String day;
    private String month;
    private String year;

    public DownloadData(String title, String link, String size, String day, String month, String year) {
        this.title = title;
        this.link = link;
        this.size = size;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getSize() {
        return size;
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
