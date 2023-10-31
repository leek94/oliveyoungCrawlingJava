package com.example.cosmeticCrawlingJava.Controller;

import entity.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventCrawling {
    public static void main(String[] args) {
        String siteType = "OL";
        List<Map<String, String>> eventList = new ArrayList<>();
        String eventLink = "https://www.oliveyoung.co.kr/store/main/getEventList.do?evtType=20";
        //이벤트 페이지 링크 주소 확인 필요!!


        try{
            Document doc = Jsoup.connect(eventLink).get();
            Elements tag = doc.select("div.event_tab_cont > ul > li");

            for(Element tagItem : tag){
                Element titleElement = tagItem.selectFirst(" p.evt_tit ");
                String title = null;
                if(titleElement != null){
                    title = titleElement.text();
                }

                Element contentElement = tagItem.selectFirst(" p.evt_desc");
                String content = null;
                if(contentElement != null){
                    content = contentElement.text();
                }

                Element imgElement = tagItem.selectFirst("img");
                String img = null;
                if (imgElement != null) {
                    img = imgElement.attr("data-original");
                }

                //child 타입은 자식 중에 두번째에 있는걸 찾는 것임 1.a , 2, hidden 이여서 hidden을 찾음
                Element linkElement = tagItem.selectFirst("input[type=hidden]:nth-child(2)");
                String link = null;
                if (linkElement != null) {
                    link = "https://www.oliveyoung.co.kr/store/event/getEventDetail.do?evtNo=" + linkElement.val();
                }

                String eventCode = null;
                if (link != null) {
                    eventCode = link.split("=")[1];
                }

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
                String flag = null;
                if (flagElement != null) {
                    flag = flagElement.text();
                    System.out.println(flag);
                }

                Map<String, String> eventIns = new HashMap<>();
                eventIns.put("title", title);
                eventIns.put("content", content);
                eventIns.put("imgPath", img);
                eventIns.put("img","/uploadc/contents/image/event/" + eventCode + ".png");
                eventIns.put("link", link);
                eventIns.put("eventCode", eventCode);
                eventIns.put("siteType", siteType);
                eventIns.put("start", startDate);
                eventIns.put("end", endDate);
                eventIns.put("flag", flag);

                eventList.add(eventIns);

            }

            // TODO : event(eventList, siteType) 메서드 구현 필요


        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
