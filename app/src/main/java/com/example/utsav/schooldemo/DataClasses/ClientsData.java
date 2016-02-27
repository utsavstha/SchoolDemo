package com.example.utsav.schooldemo.DataClasses;

/**
 * Created by utsav on 2/6/2016.
 */
public class ClientsData {
    private String name;
    private String url;
    private String pass;
    private String cid;

    public ClientsData(String name, String url, String cid, String pass) {
        this.name = name;
        this.url = url;
        this.pass = pass;
        this.cid = cid;
    }
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getPass() {
        return pass;
    }

    public String getCid() {
        return cid;
    }
}
