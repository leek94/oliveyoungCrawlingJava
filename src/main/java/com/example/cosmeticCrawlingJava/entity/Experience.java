package com.example.cosmeticCrawlingJava.entity;

public class Experience {
    String title;
    String content;
    String imgPath;
    String link;
    String eventCode;
    String siteType;
    String start;
    String end;
    String eventRecruit;

    public Experience(String title, String content, String imgPath, String link, String eventCode, String siteType, String start, String end, String eventRecruit) {
        this.title = title;
        this.content = content;
        this.imgPath = imgPath;
        this.link = link;
        this.eventCode = eventCode;
        this.siteType = siteType;
        this.start = start;
        this.end = end;
        this.eventRecruit = eventRecruit;
    }
}