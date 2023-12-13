package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.dto.ProductDTO;
import com.example.cosmeticCrawlingJava.repository.CcProductHistoryRepository;
import com.example.cosmeticCrawlingJava.repository.CcProductRepository;
import com.example.cosmeticCrawlingJava.entity.Product;
import com.example.cosmeticCrawlingJava.entity.ProductHistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import com.example.cosmeticCrawlingJava.util.Common;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final CcProductRepository ccproductRepository;
    private final CcProductHistoryRepository ccproductHistoryRepository;
    private final CcTempProductService ccTempProductService;

    @Transactional
    public String processProducts(List<ProductDTO> productList, int productCount) {
        for (ProductDTO productDTO : productList) {
            System.out.println("1");
            try {
                System.out.println("2");
                ccTempProductService.insertIntoTempProduct(productDTO.getProdCode(), productDTO.getSiteType());
                // 조건에 맞는 값을 받아옴 TODO : Optional로 감싸야하는 거 아닌지?
                Product foundProduct = ccproductRepository.findByComplexAttributes(
                        productDTO.getProdCode(),
                        productDTO.getSiteType(),
                        productDTO.getSiteDepth1(),
                        productDTO.getSiteDepth2(),
                        productDTO.getSiteDepth3()
                );
                System.out.println("3");
                long randomNum = (long)(Math.random() * (10000000000L - 1000000000L) + 1000000000L);
                String randomNumber = Long.toString(randomNum);

                //현재 날짜 형식 변경
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
                String formattedDateTime = now.format(formatter);
                System.out.println("4");
                if (productCount == 0 || foundProduct == null) { // siteType 확인, DB 저장된 값 확인
                    System.out.println("5");
                    if (Integer.parseInt(productDTO.getPrice()) > 0) {
                        System.out.println("6");
                        String filePath = Common.downloadImage(productDTO); // 이미지 다운로드
                        productDTO.setImg(filePath); // 이미지 경로 저장

                        Product savedProduct = ccproductRepository.save(productDTO.toEntity()); // 값 DB에 저장

                        ProductHistory productHistory = new ProductHistory();
                        productHistory.setHistoryNo(productDTO.getSiteType() + formattedDateTime + randomNumber);
                        productHistory.setProductNo(savedProduct); // id가 있는 값을 넣어야 하므로 savedProduct 값 넣음
                        productHistory.setSiteType(productDTO.getSiteType());
                        productHistory.setProdCode(productDTO.getProdCode());
                        productHistory.setPrice(Integer.parseInt(productDTO.getPrice()));
                        System.out.println("7");
                        //제품 이력 저장
                        ccproductHistoryRepository.save(productHistory);
                        log.info("새 제품이 추가되었습니다");
                    }
                    continue;
                }
                // 변경시 @Transactional을 달아서 더티 체킹으로 업데이트

                //들어온 값과 DB의 Img값이 다를 경우
                if (!foundProduct.getImg().equals(productDTO.getImg())) {
                    System.out.println("이미지 변경");
                    String filePath = Common.downloadImage(productDTO);
                    productDTO.setImg(filePath);
                }
                // 제품 이름 변경시 업데이트
                if (!foundProduct.getProdName().equals(productDTO.getProdName())) {
                    System.out.println("이름 변경");
                    foundProduct.setProdName(productDTO.getProdName());
                }
                // soldOut 변경시 업데이트
                if(!foundProduct.getSoldOut().equals(productDTO.getSoldOut())){
                    System.out.println("sold out 변경");
                    foundProduct.setSoldOut(productDTO.getSoldOut());
                }
                // 브랜드 변경시 업데이트
                if(!foundProduct.getBrand().equals(productDTO.getBrand())){
                    System.out.println("브랜드 변경");
                    foundProduct.setBrand(productDTO.getBrand());

                }
                // 가격 변동시 이력 저장 및 가격 업데이트
                if (foundProduct.getPrice() != Integer.parseInt(productDTO.getPrice())) {
                    System.out.println("이름 변경");
                        System.out.println("10");
                        System.out.println("foundProduct.getPrice() = " + foundProduct.getPrice());
                        System.out.println("Integer.parseInt(productDTO.getPrice()) = " + Integer.parseInt(productDTO.getPrice()));

                        ProductHistory productHistory = new ProductHistory();
                        productHistory.setHistoryNo(productDTO.getSiteType() + formattedDateTime + randomNumber);
                        productHistory.setProductNo(foundProduct);
                        productHistory.setSiteType(productDTO.getSiteType());
                        productHistory.setProdCode(productDTO.getProdCode());
                        productHistory.setPrice(Integer.parseInt(productDTO.getPrice()));

                        if (productDTO.getSiteType().equals("CL") && productDTO.getSoldOut().equals("일시품절")) {
                            productDTO.setPrice(String.valueOf(foundProduct.getPrice())); // String 타입으로 변환
                            productHistory.setPrice(foundProduct.getPrice());
                            System.out.println("11");
                        }
                        System.out.println("12");
                        //제품 이력 저장
                        ccproductHistoryRepository.save(productHistory);
//                        ccproductRepository.save(productDTO.toEntity());
                    }

            }catch (DataIntegrityViolationException e) {
                log.warn(" 예외가 발생했습니다 : " + e.getMessage());
            }catch (IncorrectResultSizeDataAccessException e){
                log.warn(" 중복 예외 발생 : " + e.getMessage());
            } catch (Exception e) {
                log.warn(" 예외 발생 : " + e.getMessage());
                e.printStackTrace();
            }
        } // for문 안에서 돌고 있는 try catch문이여서 에러가 잡혀도 에러 메세지 띄우고 다음 내용 진행됨
        return "끝남";
    }

    @Transactional
    public int countBySiteType(String siteType) {
        try {
            int count = ccproductRepository.countBySiteType(siteType);
            return (count > 0) ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }



}
