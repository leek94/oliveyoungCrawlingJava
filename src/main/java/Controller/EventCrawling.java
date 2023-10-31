package Controller;

import entity.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventCrawling {
    public static void main(String[] args) {
        List<Event> eventList = new ArrayList<Event>();
        String eventLink = "https://www.oliveyoung.co.kr/store/main/getEventList.do?evtType=100";


        try{
            Document doc = Jsoup.connect(eventLink).get();
            Elements eventElements = doc.select("div.event_tab_cont > ul > li");

            System.out.println("eventElements :"+ eventElements);
            System.out.println("eventElements :"+ eventElements);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
