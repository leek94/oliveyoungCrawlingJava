package com.example.cosmeticCrawlingJava.Controller;

import com.example.cosmeticCrawlingJava.entity.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.cosmeticCrawlingJava.util.Common;

@Controller
public class CommonController {

    @GetMapping("/test/123")
    public String show(Model model) {
        Product product = new Product();
        product.setSiteType("OL");
        product.setProdCode("A000000166675");
        product.setImgPath("https://cdn.imweb.me/upload/94dc5a2f83cd5.jpg");
        String filePath = Common.downloadImage(product);
        model.addAttribute("filePath", filePath);

        return "new";
    }
}
