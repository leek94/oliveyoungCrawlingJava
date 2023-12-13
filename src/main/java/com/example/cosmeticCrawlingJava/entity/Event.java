package com.example.cosmeticCrawlingJava.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String content;
    private String imgPath;
    private String img;
    private String link;
    private String eventCode;
    private String siteType;
    private String startDate;
    private String endDate;
    private String flag;

    public Event(String title, String content, String imgPath, String img, String link, String eventCode, String siteType, String startDate, String endDate, String flag) {
        this.title = title;
        this.content = content;
        this.imgPath = imgPath;
        this.img = img;
        this.link = link;
        this.eventCode = eventCode;
        this.siteType = siteType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flag = flag;
    }
}
