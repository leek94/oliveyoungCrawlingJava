package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.dto.SiteEventDTO;
import com.example.cosmeticCrawlingJava.entity.SiteEvent;
import com.example.cosmeticCrawlingJava.repository.CcSiteEventRepository;
import com.example.cosmeticCrawlingJava.util.Common;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final CcSiteEventRepository ccSiteEventRepository;
    private final Common common;
    public static final String FILE_DIRECTORY = "/uploadc/contents/image/event/";

    //TODO : Try catch문으로 감싸기

    @Transactional
    public String processEvent(List<SiteEventDTO> siteEventDTOList, String siteType) {
        List<SiteEvent> ccSiteEventlist = ccSiteEventRepository.findBySiteType(siteType);

        // Map에 들어가는 값 Event 인지 EventDTO인지 확인 필요
        Map<String, SiteEventDTO> titleMap = new HashMap<>(); // 새로운 내용
        Map<String, SiteEvent> check = new HashMap<>(); // DB 내용

        for(SiteEventDTO siteEventDTO : siteEventDTOList){ // 새로 크롤링 해온 내용
            titleMap.put(siteEventDTO.getTitle(), siteEventDTO);
        }

        for(SiteEvent item : ccSiteEventlist){ // DB에 있는 내용
            check.put(item.getEventTitle(), item);
        }

        for (Map.Entry<String, SiteEvent> entry : check.entrySet()) { // DB 내용
            String checkKey = entry.getKey(); // 맵의 키 가져오기
            SiteEvent checkValue = entry.getValue(); //맵의 벨류 가져오기
//            SiteEventDTO siteEventDTO = titleMap.get(checkKey);// key에 해당하는 밸류를 받아옴 -> siteEventDTO객체
            Long eventId = checkValue.getId();

            if(!titleMap.containsKey(checkKey)){ // 크롤링 해온 내용이 DB 내용이 없으면 --> 이미 끝났다는 의미
                Path filePath = Paths.get(FILE_DIRECTORY + checkValue.getEventSeq()+ ".png");
//                Files.delete(filePath);
//                Files.createDirectories(filePath);
                ccSiteEventRepository.deleteById(checkValue.getId()); // 아이디 값을 통해 삭제
            }
        }
        for( Map.Entry<String, SiteEventDTO> entry : titleMap.entrySet()){ // 새로운 내용
            String titleKey = entry.getKey();
            SiteEventDTO titleValue = entry.getValue();

            if(!check.containsKey(titleKey)){ // DB에 새로들어 온 내용이 없다면 --> 새로 시작했다는 의미
            Path filePath1 =  Paths.get( FILE_DIRECTORY +titleValue.getEventSeq()  + ".png");
                String filePath = common.downloadImageEvent(titleValue);
                ccSiteEventRepository.save(titleValue.toEntity()); // 새로운 값 저장
            }
            if(check.containsKey(titleKey)){ // DB에 있고 크롤링 값에도 있어서 업데이트
                SiteEvent DBEventValue = check.get(titleKey); // DB에 title 키값이 맞는 밸류를 불러옴
                DBEventValue.setStartDate(titleValue.getStartDate()); // StartDate 업데이트
                DBEventValue.setEndDate(titleValue.getEndDate()); // endDate 업데이트
            }
        }

        return "이벤트 끝남";
    }
}
