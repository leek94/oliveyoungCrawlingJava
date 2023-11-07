package com.example.cosmeticCrawlingJava.Controller;

import com.example.cosmeticCrawlingJava.entity.Product;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.cosmeticCrawlingJava.util.common;

@Controller
public class CommonController {
    @GetMapping("/test/123")
    public String show() {
        Product product = new Product();
        product.setSiteType("OL");
        product.setProdCode("A000000166675");
        common.downloadImage(product);
        return "";
    }
}
