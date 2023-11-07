package com.example.cosmeticCrawlingJava.util;

import com.example.cosmeticCrawlingJava.entity.Product;
import org.jsoup.nodes.Element;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Properties;

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
//        String fileDirectory = IMAGE_DIRECTORY + product.getSiteType() + "/";
        //"/uploadc/contents/image/OL/"
        String fileDirectory = "C:\\Users\\Focus\\image\\OL\\";
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

    public static void sendMail(String message, String siteType) {
        try{
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            String title = siteType + " " + formattedDateTime + "오류 발생";
            String content = message;

            // SMTP 서버 설정
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.naver.com");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "587");

            // 계정 정보 설정
            String username = "solutionfocus@naver.com";
            String password = "!solfocus0515";

            // 메일 수신자 설정
            String receiver = "jh@solutionfocus.co.kr";

            //메일 세션 생성
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            //메시지 생성
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress((receiver)));
            msg.setSubject(title);
            msg.setText(content);

            //메일 전송
            Transport.send(msg);

        } catch (MessagingException e){
            e.printStackTrace();
        }
    }
}
