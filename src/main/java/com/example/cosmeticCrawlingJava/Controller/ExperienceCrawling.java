package com.example.cosmeticCrawlingJava.Controller;

import entity.Experience;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExperienceCrawling {
    public static void main(String[] args) {
        String siteType = "OL";
        String experienceLink = "https://www.oliveyoung.co.kr/store/main/getEventList.do?evtType=100";
        int page = 2;
        List<Experience> experienceList = new ArrayList<>();
        try{
            //Elements는 select와 써야 여러개 나옴 Element는 selectFirst와 써야 한개만 나옴
            Document doc = Jsoup.connect(experienceLink).get();
            Elements expPage = doc.select("#Container > div > div >.event_tab_cont > div.pageging > a");
            System.out.println("expPage :" + expPage );
            Element eventDateElement = doc.selectFirst("p.txtdate");
            String eventDate = eventDateElement.text().split("\\| ")[1];
            String startDate = eventDate.split(" ~ ")[0];
            String endDate = eventDate.split(" ~ ")[1];
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
