package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.dto.ProductDTO;
import com.example.cosmeticCrawlingJava.entity.Category;
import com.example.cosmeticCrawlingJava.entity.Product;
import com.example.cosmeticCrawlingJava.service.ProductService;
import com.example.cosmeticCrawlingJava.util.ReturnMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.example.cosmeticCrawlingJava.util.Common;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class Crawling {


    private final ProductService productService;
    private final Common common;

    public void startCrawling() {

        String siteType = "OL";
        WebDriver driver = common.startCrawling(siteType);
        String url = "https://www.oliveyoung.co.kr/store/main/main.do?oy=0";
        String siteDepth1 = "";
        String siteDepth2 = "";

        int maxRetries = 3;
        int retryCount = 0;

        HttpClient httpClient = HttpClientBuilder.create().build();
        // 사이트가 연결이 되는지 확인하는 구문 -> 없어도 무방함함
       while(retryCount < maxRetries)
            try{
                HttpGet request = new HttpGet(url); // url 페이지 요청
                HttpResponse response = httpClient.execute(request);

                if (response.getStatusLine().getStatusCode() == 200) {
                    break;
                }
                retryCount++;
            } catch(IOException e){
                log.info("서버와 연결이 불안정해 다시 시도합니다...." + retryCount);
                retryCount++;

                if (retryCount == maxRetries) {
                    common.sendMail(ReturnMessage.CONNECTION.getMessage(), siteType);
                    System.exit(0);
                }
            }

        try {

            // URL을 직접 연결하여 HTML 문서를 가져옵니다.
            Connection connetion = Jsoup.connect(url)
                    .timeout(30000); // readtime 10초 설정

            Document document = connetion.get();
            // 이후, document 객체를 사용하여 파싱할 수 있습니다.

            Elements depth1Elements = document.select("#gnbAllMenu > ul > li:nth-child(1) > div > p > a");
            Elements depth2Elements = document.select("#gnbAllMenu > ul > li:nth-child(1) > div > ul > li > a");

            List<Category> categories = new ArrayList<>();
            List<ProductDTO> productList = new ArrayList<>();

            // count문 데이터베이스 연결하는 코드
            int prodCount = productService.countBySiteType(siteType); //값이 있으면 1 없으면 0 반환

            for (Element depth1 : depth1Elements) {
                siteDepth1 = depth1.text();  //스킨케어
                String check1 = depth1.attr("data-attr"); //공통^드로우^스킨케어

                for (Element depth2 : depth2Elements) {
                    siteDepth2 = depth2.text(); // 토너/로션/올인원
                    String check2 = depth2.attr("data-attr");//공통^드로우^스킨케어_토너/로션/올인원
                    String siteLink = depth2.attr("data-ref-dispcatno"); //100000100010008

                    if (check2.contains(check1)) {
                        Category category = new Category(siteDepth1, siteDepth2, siteLink);
                        categories.add(category);
                    }
                }
            }

            for (Category category : categories) { //58개 들어옴
                String depth2Url = "https://www.oliveyoung.co.kr/store/display/getMCategoryList.do?dispCatNo=" + category.getSiteLink();

                Connection connetion1 = Jsoup.connect(depth2Url)
                        .timeout(30000); // readtime 10초 설정

                Document document1 = connetion1.get();

                Elements cateCategory = document1.select("#Contents > ul.cate_list_box > li > a");
                cateCategory.remove(0); // 맨앞 전체라는 카테고리를 없애기 위해서 첫번째 인덱스 내용 삭제
                siteDepth1 = category.getSiteDepth1();
                siteDepth2 = category.getSiteDepth2();

                for (Element siteDepth3 : cateCategory) { // 4개 들어옴
                    int row = 0;
                    int page = 2; // 1페이지는 처음에 진행됨 -> 다음 페이지가 2페이지부터 시작이므로 숫자 2로 초기화
                    String depth3 = siteDepth3.className(); //jsoup에서 classname 반환함
                    String depth3Url = "https://www.oliveyoung.co.kr/store/display/getMCategoryList.do?dispCatNo=" + depth3;
                    String siteDepth3Text = siteDepth3.text(); //스킨/토너, 로션/에멀젼, 올인원, 스킨케어 세트

                    Connection connetion2 = Jsoup.connect(depth3Url)
                            .timeout(30000); // readtime 10초 설정

                    Document document2 = connetion2.get();

                    String productCount = document2.select("p.cate_info_tx > span").text(); //등록 갯수 확인
                    Elements tag = document2.select("#Contents > ul > li[data-index]:not([data-index*='\\D'])"); //ul 이지만 인덱스가 있는 값만 찾아옴, 정규 표현식 0번이상 반복 숫자가아닌 값

                    for (int k = 0; k < Integer.parseInt(productCount); k++) {

                        if (row == 24) { //24개 확인 후 페이지 넘어감감
                           String nextLink = depth3Url + "&pageIdx=" + page;
                            Document document3 = Jsoup.connect(nextLink).get();
                            tag = document3.select("#Contents > ul > li[data-index]:not([data-index*='\\D'])");
                            page++;
                            row = 0;
                        }

                        Element tagItem = tag.get(row);

                        Element imgElement = tagItem.selectFirst("div > a > img"); // 앞의 이미지의 html을 저장
                        String img = Optional.ofNullable(imgElement)
                                .map(element -> element.attr("src"))
                                .orElse(""); //이미지의 절대 주소 저장 http://형태

                        Element infoElement = tagItem.selectFirst("a.prd_thumb"); //a태크 prd_thumb 클래스가 처음 나오는 것이 저장됨
                        String info = Optional.ofNullable(infoElement)
                                .map(element -> element.attr("href"))
                                .orElse("");

                        String prodCode = Optional.ofNullable(infoElement)
                                .map(element -> element.attr("data-ref-goodsno"))
                                .orElse("");

                        Element brandElement = tagItem.selectFirst("span.tx_brand");
                        String brand = common.nullCheck(brandElement);

                        Element prodNameElement = tagItem.selectFirst("p.tx_name");
                        String prodName = common.nullCheck(prodNameElement);

                        Element bePriceElement = tagItem.selectFirst("span.tx_org > span");
                        // Optional로 null 값을 스트링 값인 "0"으로 변경
                        String bePrice = common.nullCheckPrice(bePriceElement);

                        Element priceElement = tagItem.selectFirst("span.tx_cur > span");
                        String price = common.nullCheckPrice(priceElement);

                        Element soldOutElement = tagItem.selectFirst("span.soldout");
                        String soldOut = common.nullCheck(soldOutElement);

                        Element saleElement = tagItem.selectFirst("span.icon_flag.sale"); //span의 클래스 icon_flag와 클래스 sale 두개를 동시에 갖는 값을 뽑음
                        String sale = common.nullCheck(saleElement);

                        String infoCoupang = "https://link.coupang.com/a/3IhPI"; //여기서는 초기화하고 나중에 링크 변경해주는 배치가 따로 깃에 있음

                        row++;
                        ProductDTO productDTO = new ProductDTO(null, img, "/uploadc/contents/image/" + siteType + "/" + prodCode + ".png","", info
                                , infoCoupang, prodName, prodCode, price, bePrice, common.calculateDiscountPercent(bePrice, price)
                                ,soldOut, siteDepth1, siteDepth2, siteDepth3Text, siteType, brand);
                        //Create a Product object and add it to the product list
//                        Product product = new Product(null,"/uploadc/contents/image/" + siteType + "/" + prodCode + ".png", img, info,
//                                prodName, prodCode, Integer.parseInt(price), Integer.parseInt(bePrice), common.calculateDiscountPercent(bePrice, price),
//                                soldOut, siteDepth1, siteDepth2, siteDepth3Text, siteType, brand, infoCoupang);
                        productList.add(productDTO);
                        productService.processProducts(productList, prodCount); //prodCount 이미 있으면 1 없으면 0
                        productList.clear();
                    }
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}