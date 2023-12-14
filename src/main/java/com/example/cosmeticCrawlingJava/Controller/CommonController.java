package com.example.cosmeticCrawlingJava.Controller;

import com.example.cosmeticCrawlingJava.dto.ProductDTO;
import com.example.cosmeticCrawlingJava.entity.Product;
import com.example.cosmeticCrawlingJava.service.Crawling;
import com.example.cosmeticCrawlingJava.service.EventCrawling;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.cosmeticCrawlingJava.util.Common;

@Controller
@RequiredArgsConstructor
public class CommonController {

    private final EventCrawling eventCrawling;
    private final Crawling crawling;
    private final Common common;

    @GetMapping("/test")
    public ResponseEntity test() {
        long start = System.currentTimeMillis();
        crawling.startCrawling();
        long end = System.currentTimeMillis();
        System.out.println("걸리는 시간 : " + (end - start));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/testEvent")
    public ResponseEntity testEvent() {
        long start = System.currentTimeMillis();
        eventCrawling.startEventCrawling();
        long end = System.currentTimeMillis();
        System.out.println("걸리는 시간 : " + (end - start));
        return new ResponseEntity(HttpStatus.OK);
    }



    @GetMapping("/test/123")
    public String show(Model model) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setSiteType("OL");
        productDTO.setProdCode("A000000166675");
        productDTO.setImg2("https://cdn.imweb.me/upload/94dc5a2f83cd5.jpg");
        String filePath = common.downloadImage(productDTO);
        model.addAttribute("filePath", filePath);

        return "new";
    }
}
