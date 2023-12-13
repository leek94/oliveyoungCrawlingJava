package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.dto.EventDTO;
import com.example.cosmeticCrawlingJava.entity.Event;
import com.example.cosmeticCrawlingJava.util.Common;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final Common common;
    public static final String FILE_DIRECTORY = "/uploadc/contents/image/event/";


    @Transactional
    public String processEvent(List<EventDTO> eventDTOList, int productCount) {
       

        return null;
    }
}
