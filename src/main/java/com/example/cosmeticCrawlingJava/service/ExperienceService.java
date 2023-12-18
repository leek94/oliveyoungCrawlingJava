package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.dto.ExperienceDTO;
import com.example.cosmeticCrawlingJava.entity.SiteEvent;
import com.example.cosmeticCrawlingJava.repository.CcSiteEventRepository;
import com.example.cosmeticCrawlingJava.util.Common;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperienceService {
    private final CcSiteEventRepository ccSiteEventRepository;
    private final Common common;
    public static final String FILE_DIRECTIORY = "/uploadc/contents/image/event/";

    @Transactional
    public String processExperience(List<ExperienceDTO> experienceDTOList){
        try {

            List<SiteEvent> ccSiteEventList = ccSiteEventRepository.findBySiteTypeAndRecruitNotNull(experienceDTOList.get(0).getSiteType());

            Map<String, ExperienceDTO> titleMap = new HashMap<>(); // 세로운 내용
            Map<String, SiteEvent> check = new HashMap<>(); // DB 내용

            for (ExperienceDTO experienceDTO : experienceDTOList) {  // 새로 크롤링 해온 내용
                titleMap.put(experienceDTO.getTitle(), experienceDTO);
            }

            for (SiteEvent item : ccSiteEventList) { // DB에 있는 내용
                check.put(item.getEventTitle(), item);
            }

            for (Map.Entry<String, ExperienceDTO> entry : titleMap.entrySet()) {
                String titleKey = entry.getKey();
                ExperienceDTO titleValue = entry.getValue();
                if (!check.containsKey(titleKey)) {  //크롤링 해온 내용이 DB에 없으면 --> 추가
                    Path filePath1 = Paths.get(FILE_DIRECTIORY + titleValue.getEventCode() + ".png");
                    String filePath = common.downloadImageExperience(titleValue);
                    ccSiteEventRepository.save(titleValue.toEntity());
                }
            }
            for (Map.Entry<String, SiteEvent> entry : check.entrySet()) {
                String checkKey = entry.getKey();
                SiteEvent checkValue = entry.getValue();
                if (!titleMap.containsKey(checkKey)) {
                    Path filePath = Paths.get(FILE_DIRECTIORY + checkValue.getEventSeq() + ".png");
                    Files.delete(filePath);
                    Files.createDirectories(filePath);
                    ccSiteEventRepository.deleteById(checkValue.getId());
                }
            }
        }catch (IOException e){
            log.warn("예외 발생 " + e.getMessage());
        }catch (Exception e) {
            log.warn("예외 발생 " + e.getMessage());
        }



        return null;
    }
}
