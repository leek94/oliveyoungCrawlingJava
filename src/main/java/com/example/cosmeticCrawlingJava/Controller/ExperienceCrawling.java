package com.example.cosmeticCrawlingJava.Controller;

import com.example.cosmeticCrawlingJava.entity.Experience;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.example.cosmeticCrawlingJava.util.Common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExperienceCrawling {
    public static void main(String[] args) {
        String siteType = "OL";
        String experience = "https://www.oliveyoung.co.kr/store/main/getEventList.do?evtType=100";
        int page = 2;
        List<Experience> experienceList = new ArrayList<>();
        try{
            //Elements는 select와 써야 여러개 나옴 Element는 selectFirst와 써야 한개만 나옴
            Document doc = Jsoup.connect(experience).get();
            Elements expPage = doc.select("#Container > div > div.event_tab_cont > div.pageing > a");
            System.out.println("expPage :" + expPage.size() );

            Element eventDateElement = doc.selectFirst("p.txtdate");
            String eventDate = eventDateElement.text().split("\\| ")[1];
            String startDate = eventDate.split(" ~ ")[0];
            String endDate = eventDate.split(" ~ ")[1];

            for(int i = 0; i < expPage.size() + 1; i++){
                if(i > 0){
                    experience = experience + "&pageIdx=" + page;
                    doc = Jsoup.connect(experience).get();
                    page++;
                }

                Elements tag = doc.select("div.event_apply_list > ul.inner > li");
                System.out.println(tag);
                System.out.println(experience);

                for(Element tagItem : tag){
                    Element titleElement = tagItem.selectFirst("p.dec1");
                    String title = Common.nullCheck(titleElement);

                    Element contentElement = tagItem.selectFirst("p.dec2");
                    String content = Common.nullCheck(tagItem);

                    Element imgElement = tagItem.selectFirst("img");
                    String img = Optional.ofNullable(imgElement)
                            .map(element -> element.attr("src"))
                            .orElse("");

                    Element eventRecruitElement =tagItem.selectFirst("ul.area");
                    String eventRecruit = Optional.ofNullable(eventRecruitElement)
                            .map(element -> element.text().replace("\n", ""))
                            .orElse("");

                    String link = experience;

                    String eventCode = Optional.ofNullable(imgElement)
                            .map(element -> element.attr("src"))
                            .map(src -> src.split("/"))
                            .map(splitted -> splitted[splitted.length - 1].split("ko")[0])
                            .orElse("");

                    Experience experienceIns = new Experience(title, content, img, link, eventCode, siteType, startDate, endDate, eventRecruit);
                    experienceList.add(experienceIns);

                }
                // TODO : 체험 이벤트 데이터 저장하는 쿼리 작성
                // TODO : 삭제된 이벤트 삭제하는 식 작성
                // TODO : 크롤링 종료하는 식 작성
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}