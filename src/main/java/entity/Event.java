package entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {
    private String title;
    private String content;
    private String imgPath;
    private String img;
    private String link;
    private String eventCode;
    private String siteType;
    private String start;
    private String end;

    public Event(String title, String content, String imgPath, String img, String link, String eventCode, String siteType, String start, String end) {
        this.title = title;
        this.content = content;
        this.imgPath = imgPath;
        this.img = img;
        this.link = link;
        this.eventCode = eventCode;
        this.siteType = siteType;
        this.start = start;
        this.end = end;
    }
}
