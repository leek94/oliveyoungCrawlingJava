package com.example.cosmeticCrawlingJava.Controller;

import com.example.cosmeticCrawlingJava.entity.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

import static com.example.cosmeticCrawlingJava.util.common.nullCheck;
import static com.example.cosmeticCrawlingJava.util.common.sendMail;

public class EventCrawling {
    public static void main(String[] args) {
        String siteType = "OL";
        List<Event> eventList = new ArrayList<>();

        try{
            String url = "https://www.oliveyoung.co.kr/store/main/main.do?oy=0";
            Document document = Jsoup.connect(url).get();
            Element eventLinkElement = document.selectFirst("#gnbWrap > ul > li:nth-child(8) > a");
            String eventLink = eventLinkElement.attr("href");

            System.out.println("eventLink : " + eventLink);
            Document doc = Jsoup.connect(eventLink).get();
            Elements tag = doc.select("div.event_tab_cont > ul > li");

            for(Element tagItem : tag){
                Element titleElement = tagItem.selectFirst(" p.evt_tit ");
                String title = nullCheck(titleElement);

                Element contentElement = tagItem.selectFirst(" p.evt_desc");
                String content = nullCheck(contentElement);

                Element imgElement = tagItem.selectFirst("img");
                String img = Optional.ofNullable(imgElement)
                        .map(element -> element.attr("src"))
                        .orElse("");

                //child 타입은 자식 중에 두번째에 있는걸 찾는 것임 1.a , 2, hidden 이여서 hidden을 찾음
                Element linkElement = tagItem.selectFirst("input[type=hidden]:nth-child(2)");
                String link = Optional.ofNullable(linkElement)
                        .map(element -> "https://www.oliveyoung.co.kr/store/event/getEventDetail.do?evtNo=" + element.val())
                        .orElse("");


                String eventCode = Optional.ofNullable(link)
                        .map(l -> l.split("="))
                        .filter(splitArray -> splitArray.length > 1)
                        .map(splitArray -> splitArray[1])
                        .orElse("");

                Element eventDateElement = tagItem.selectFirst("p.evt_date");
                String[] eventDate = null;
                if (eventDateElement != null) {
                    eventDate = eventDateElement.text().split("- ");
                }
                String startDate = null;
                if (eventDate != null) {
                    startDate = eventDate[0].replace(".", "-");
                }
                String endDate = null;
                if (eventDate != null) {
                    endDate = eventDate[1].replace(".", "-");
                }

                //span.evt_flag로 해야 온라인, 온&오프라인, 오프라인 다 나옴 span.evt_flag.online으로 하면 온라인만 나옴
//                Element flagElement = tagItem.selectFirst("span.evt_flag");
                Element flagElement = tagItem.selectFirst("span.evt_flag.online");
                String flag = nullCheck(flagElement);

//                Map<String, String> eventIns = new HashMap<>();
//                eventIns.put("title", title);
//                eventIns.put("content", content);
//                eventIns.put("imgPath", img);
//                eventIns.put("img","/uploadc/contents/image/event/" + eventCode + ".png");
//                eventIns.put("link", link);
//                eventIns.put("eventCode", eventCode);
//                eventIns.put("siteType", siteType);
//                eventIns.put("start", startDate);
//                eventIns.put("end", endDate);
//                eventIns.put("flag", flag);

                Event eventIns = new Event(title, content, img, "/uploadc/contents/image/event/" + eventCode + ".png", link, eventCode, siteType, startDate, endDate, flag);
                eventList.add(eventIns);

            }

            // TODO : event(eventList, siteType) 메서드 구현 필요


        }catch (IOException e){
            sendMail(e.getMessage(), siteType);
            e.printStackTrace();
        }
    }
}
