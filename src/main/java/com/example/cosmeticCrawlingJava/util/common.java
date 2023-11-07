package com.example.cosmeticCrawlingJava.util;

import com.example.cosmeticCrawlingJava.entity.Product;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class common {

    private static final String IMAGE_DIRECTORY = "/uploadc/contents/image/";


    public static double calculateDiscountPercent(String bePrice, String price) {
        if(bePrice.equals("0")){
            return 0.0;

        }

        int bePriceInt = Integer.parseInt(bePrice);
        int priceInt = Integer.parseInt(price);
        return ((bePriceInt - priceInt) / (double) bePriceInt * 100);
    }

    public static String nullCheck(Element element) {

        return Optional.ofNullable(element)
                .map(Element::text)
                .orElse("");
    }

    public static String nullCheckPrice(Element element) {

        return Optional.ofNullable(element)
                .map(Element::text)
                .map(text -> text.replaceAll("[^0-9]", ""))
                .orElse("0");
    }

    public static String downloadImage(Product product) {
        String fileDirectory = IMAGE_DIRECTORY + product.getSiteType() + "/";
        //"/uploadc/contents/image/OL/"
        String filePath = fileDirectory + product.getProdCode() + ".png";
        //"/uploadc/contents/image/OL/A000000166675.png"

        try{
            //TODO : 이미지 파일 넣어서 실행해 보자
            URL url = new URL(product.getImgPath());
            InputStream inputStream = url.openStream();

            Path path = Paths.get(fileDirectory);
            Files.createDirectories(path);

            if (!Files.exists(Paths.get(filePath))) {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.close();
            }

            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();

        }
        System.out.println("filePath : " + filePath);
        return filePath;
    }

    public static String sendMail(String message, String siteType) {
        return "";
    }
}
