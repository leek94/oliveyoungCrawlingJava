package com.example.cosmeticCrawlingJava.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class Experience {
    String title;
    String content;
    String img;
    String link;
    String eventCode;
    String siteType;
    String startDate;
    String endDate;
    String eventRecruit;

    public Experience(String title, String content, String img, String link, String eventCode, String siteType, String startDate, String endDate, String eventRecruit) {
        this.title = title;
        this.content = content;
        this.img = img;
        this.link = link;
        this.eventCode = eventCode;
        this.siteType = siteType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventRecruit = eventRecruit;
    }
}