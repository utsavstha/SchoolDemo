package com.example.utsav.schooldemo;

/**
 * Created by utsav on 1/2/2016.
 */
public class NoticeData {
    private int id;
    private String title;
    private String message;
    private String day;
    private String month;
    private String year;

    public NoticeData(int id, String title, String message, String day, String month, String year) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
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
