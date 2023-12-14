package com.example.cosmeticCrawlingJava.service;


import com.example.cosmeticCrawlingJava.dto.SiteEventDTO;
import com.example.cosmeticCrawlingJava.util.Common;
import com.example.cosmeticCrawlingJava.util.ReturnMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventCrawling {

    private final Common common;
    private final EventService eventService;

    public void startEventCrawling(){
        String siteType = "OL";
        List<SiteEventDTO> eventList = new ArrayList<>();

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
                String title = common.nullCheck(titleElement);

                Element contentElement = tagItem.selectFirst(" p.evt_desc");
                String content = common.nullCheck(contentElement);

                Element imgElement = tagItem.selectFirst("img");
                String img = Optional.ofNullable(imgElement)
                        .map(element -> element.attr("data-original")) // src로 했을 경우 값이 안넘어옴,,, data-original로 했을 경우 넘어와서 사용
                        .orElse("");
                System.out.println(img);

                //child 타입은 자식 중에 두번째에 있는걸 찾는 것임 1.a , 2, hidden 이여서 hidden을 찾음
                 Element linkElement = tagItem.selectFirst("input[type=hidden]:nth-child(2)");
                String link = Optional.ofNullable(linkElement)
                        .map(element -> "https://www.oliveyoung.co.kr/store/event/getEventDetail.do?evtNo=" + element.val())
                        .orElse("");


                String eventCode = Optional.ofNullable(link)
                        .map(l -> l.split("=")) // = 을 기준으로 값을 나누고
                        .filter(splitArray -> splitArray.length > 1) // 1개 이상이면 map 실행 1개 미만이면 ""으로 초기화
                        .map(splitArray -> splitArray[1])
                        .orElse("");

                System.out.println(link);
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
                String flag = common.nullCheck(flagElement);

                DateTimeFormatter inputFormatter =  DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");

                LocalDateTime parsedStartDate = LocalDateTime.parse(startDate + " 00:00:00", inputFormatter);
                LocalDateTime parsedEndDate = LocalDateTime.parse(endDate+ " 00:00:00", inputFormatter);


                SiteEventDTO siteEventDTO = SiteEventDTO.builder()
                        .imgPath(img)
                        .title(title)
                        .content(content)
                        .eventImg("/uploadc/contents/image/event/" + eventCode + ".png")
                        .eventLink(link)
                        .eventSeq(eventCode)
                        .siteType(siteType)
                        .startDate(parsedStartDate)
                        .endDate(parsedEndDate)
                        .build();

                eventList.add(siteEventDTO);

            }

           eventService.processEvent(eventList, siteType);


        }catch (IOException e){
            common.sendMail(ReturnMessage.CHANGE_CATEGORY.getMessage(), siteType);
            e.printStackTrace();
        }
    }
}
