package com.example.cosmeticCrawlingJava.dto;

import com.example.cosmeticCrawlingJava.entity.SiteEvent;
import com.example.cosmeticCrawlingJava.enumfile.TopYn;
import com.example.cosmeticCrawlingJava.enumfile.UseYn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class SiteEventDTO {

    private Long id;
    private String imgPath;
    private String title;
    private String content;
    private UseYn useYn;
    private TopYn topYn;
    private String eventRecruit;
    private String eventImg;
    private String eventLink;
    private String eventSeq;
    private String siteType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime regDate;
    private String regId;


    public SiteEvent toEntity() {
        return SiteEvent.builder()
                .id(null)
                .eventTitle(title)
                .eventContent(content)
                .eventRecruit(eventRecruit)
                .eventImg(eventImg)
                .eventLink(eventLink)
                .eventSeq(eventSeq)
                .siteType(siteType)
                .startDate(startDate)
                .endDate(endDate)
                .regDate(regDate)
                .regId(regId)
                .build();
    }

}
