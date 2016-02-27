package com.example.utsav.schooldemo.DataClasses;

/**
 * Created by utsav on 2/27/2016.
 */
public class NewsData {
    private String id;
    private String title;
    private String message;
    private String day;
    private String weekday;
    private String month;
    private String year;
    private String imageUrl;
    private String imagePath;

    public NewsData(String id, String title, String message,
                    String day, String weekday, String month,
                    String year, String imageUrl, String imagePath) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.day = day;
        this.weekday = weekday;
        this.month = month;
        this.year = year;
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;
    }

    public String getId() {
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

    public String getWeekday() {
        return weekday;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImagePath() {
        return imagePath;
    }
}
