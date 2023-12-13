package com.example.cosmeticCrawlingJava.dto;


import com.example.cosmeticCrawlingJava.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EventDTO {

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

    public Event toEntity() {
        return Event.builder()
                .title(title)
                .content(content)
                .imgPath(imgPath)
                .img(img)
                .link(link)
                .eventCode(eventCode)
                .siteType(siteType)
                .startDate(startDate)
                .endDate(endDate)
                .flag(flag)
                .build();

    }
}
