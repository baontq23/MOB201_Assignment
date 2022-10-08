package com.baontq.mob201.model;

public class RssChannel {
    private String name, link;

    public RssChannel(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
