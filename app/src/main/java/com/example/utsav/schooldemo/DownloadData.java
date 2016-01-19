package com.example.utsav.schooldemo;

/**
 * Created by utsav on 1/19/2016.
 */
public class DownloadData {
    private int id;
    private String title;
    private String link;
    private String size;
    private String path;
    private String day;
    private String month;
    private String year;

    public DownloadData(int id, String title, String link, String size,String path, String day, String month, String year) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.size = size;
        this.path = path;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
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
