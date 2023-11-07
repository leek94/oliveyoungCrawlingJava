package com.example.cosmeticCrawlingJava.Controller;

import com.example.cosmeticCrawlingJava.entity.Category;
import com.example.cosmeticCrawlingJava.entity.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.example.cosmeticCrawlingJava.util.common;

import java.io.IOException;
import java.util.*;

public class Crawling {
    public static void main(String[] args) {
        String siteType = "OL";
        //TODO : driver 변수 추가 필요
        String url = "https://www.oliveyoung.co.kr/store/main/main.do?oy=0";
        String siteDepth1 = "";
        String siteDepth2 = "";

        //TODO : 최대 3번 연결 시도 while문 작성 필요


        try {

            // URL을 직접 연결하여 HTML 문서를 가져옵니다.
            Document document = Jsoup.connect(url).get();
            // 이후, document 객체를 사용하여 파싱할 수 있습니다.

            Elements depth1Elements = document.select("#gnbAllMenu > ul > li:nth-child(1) > div > p > a");// 여러개 나옴
            Elements depth2Elements = document.select("#gnbAllMenu > ul > li:nth-child(1) > div > ul > li > a"); // 여러개 나옴

            List<Category> categories = new ArrayList<>();
            List<Product> productList = new ArrayList<>();

            System.out.println("depth1Elements: " + depth1Elements);
            System.out.println("depth2Elements: " + depth2Elements);

            // TODO : count문 데이터베이스 연결하는 식 작성 필요
//            int prodCount = count(siteType);

            for (Element depth1 : depth1Elements) {  // 여러번
                siteDepth1 = depth1.text();  //스킨케어
                String check1 = depth1.attr("data-attr"); //공통^드로우^스킨케어

                for (Element depth2 : depth2Elements) { // 여러번
                    siteDepth2 = depth2.text(); // 토너/로션/올인원
                    String check2 = depth2.attr("data-attr");//공통^드로우^스킨케어_토너/로션/올인원
                    String siteLink = depth2.attr("data-ref-dispcatno"); //100000100010008

                    if (check2.contains(check1)){
                        Category category = new Category(siteDepth1, siteDepth2, siteLink);
                        categories.add(category);
                    }
                }
            }



//            for (Category category : categories) {
//                System.out.println("siteDepth1: " + category.getSiteDepth1());
//                System.out.println("siteDepth2: " + category.getSiteDepth2());
//                System.out.println("siteLink: " + category.getSiteLink());
//            }
            for (Category category : categories) { //58개 들어옴
                String depth2Url = "https://www.oliveyoung.co.kr/store/display/getMCategoryList.do?dispCatNo=" + category.getSiteLink();
                Document document1 = Jsoup.connect(depth2Url).get();
                Elements cateCategory = document1.select("#Contents > ul.cate_list_box > li > a");
                cateCategory.remove(0); // 맨앞 전체라는 카테고리를 없애기 위해서 첫번째 인덱스 내용 삭제
                siteDepth1 = category.getSiteDepth1();
                siteDepth2 = category.getSiteDepth2();

                for(Element siteDepth3 : cateCategory) { // 4개 들어옴
                    int row = 0;
                    int page = 2;
                    String depth3 = siteDepth3.className(); //jsoup에서 classname 반환함
                    String depth3Url = "https://www.oliveyoung.co.kr/store/display/getMCategoryList.do?dispCatNo=" + depth3;
                    String siteDepth3Text = siteDepth3.text(); //스킨/토너, 로션/에멀젼, 올인원, 스킨케어 세트
                    Document document2 = Jsoup.connect(depth3Url).get();
                    String productCount = document2.select("p.cate_info_tx > span").text(); //등록 갯수 확인
                    Elements tag = document2.select("#Contents > ul > li[data-index]:not([data-index*='\\D'])"); //ul 이지만 인덱스가 있는 값만 찾아옴, 정규 표현식 0번이상 반복 숫자가아닌 값

                    for (int k = 0; k < Integer.parseInt(productCount); k++) {

                            if (row == 24) {
                                System.out.println(row);
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
                                    .orElse(""); //이미지의 절대 주소 저장

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
                            System.out.println("bePrice :" + bePrice);
//                        String bePrice = Optional.ofNullable(bePriceElement)
//                                .map(Element::text)
//                                .map(text -> text.replaceAll("[^0-9]", ""))
//                                .orElse("0");// 정규 표현식으로 숫자가 아닌 문자는 전부 삭제

                            Element priceElement = tagItem.selectFirst("span.tx_cur > span");
                            String price = common.nullCheckPrice(priceElement);
                            System.out.println("price :" + price);

                            Element soldOutElement = tagItem.selectFirst("span.soldout");
                            String soldOut = common.nullCheck(soldOutElement);
                            System.out.println(soldOut);
//                        String soldOut = Optional.ofNullable(soldOutElement)
//                                .map(Element::text)
//                                .orElse(null);


                            Element saleElement = tagItem.selectFirst("span.icon_flag.sale"); //span의 클래스 icon_flag와 클래스 sale 두개를 동시에 갖는 값을 뽑음
                            String sale = common.nullCheck(saleElement);
//                        String sale = Optional.ofNullable(saleElement)
//                                .map(Element::text)
//                                .orElse(null); // 세일 안할 경우 null 값이 반환되서 Optional로 잡아줌

                            Map<String, Object> ins = new HashMap<>();
                            ins.put("imgPath", img);
                            ins.put("img", "/uploadc/cpmtents/image/" + siteType + "/" + prodCode + ".png");
                            ins.put("img2", "");
                            ins.put("info", info);
                            ins.put("infoCoupang", "https://link.coupang.com/a/3IhPI");
                            ins.put("prodName", prodName);
                            ins.put("prodCode", prodCode);
                            ins.put("price", price); //String 값 들어가 있음
                            ins.put("bePrice", bePrice);
                            ins.put("sale", common.calculateDiscountPercent(bePrice, price)); // int로 변경
                            ins.put("soldOut", soldOut);
                            System.out.println(soldOut);
                            //TODO : for문이 돌아가서 저장된 리스트에서 첫번째 값부터 불러와지면서 값이 들어가짐
                            ins.put("siteDepth1", siteDepth1);
                            ins.put("siteDepth2", siteDepth2);
                            //TODO : for 문이 끝까지 돌아서 siteDepth1 : 남성, siteDepth2 : 바디케어 값이 들어가 있음
                            //TODO : 그래서 위에 처럼 저장된 List에서 객체 순서대로 불러와야 올바른 값이 들어감!!!
                            ins.put("siteDepth3", siteDepth3Text); // text가 나와야함
                            ins.put("siteType", siteType);
                            ins.put("brand", brand);

                            row++;

                            //Create a Product object and add it to the product list
                            Product product = new Product(null, img, "/uploadc/contents/image/" + siteType + "/" + prodCode + ".png", info,
                                    prodName, prodCode, Integer.parseInt(price), Integer.parseInt(bePrice), common.calculateDiscountPercent(bePrice, price),
                                    soldOut, siteDepth1, siteDepth2, siteDepth3Text, siteType, brand);
                            productList.add(product);
                            // TODO : 공통에 product 문 작성 필요
//                        product(productList, prodCount);
                            productList.clear();
                    }
                }

            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
