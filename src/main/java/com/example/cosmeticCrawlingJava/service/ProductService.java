package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.dto.ProductDTO;
import com.example.cosmeticCrawlingJava.entity.ProductHistory;
import com.example.cosmeticCrawlingJava.repository.CcProductHistoryRepository;
import com.example.cosmeticCrawlingJava.repository.CcProductRepository;
import com.example.cosmeticCrawlingJava.entity.Product;

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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final CcProductRepository ccproductRepository;
    private final CcProductHistoryRepository ccproductHistoryRepository;
    private final CcTempProductService ccTempProductService;
    private final Common common;


    @Transactional
    public String processProducts(List<ProductDTO> productList, int productCount) {
        for (ProductDTO productDTO : productList) {
            try {
                //System.out.println("1");
                ccTempProductService.insertIntoTempProduct(productDTO.getProdCode(), productDTO.getSiteType());
                // 조건에 맞는 값을 받아옴 TODO : Optional로 감싸야하는 거 아닌지? --> null일때 저장을 하고 있는데 Optional로 굳이 감싸야 하는 지>?
                //System.out.println("2");
                Optional<Product> foundProduct = ccproductRepository.findByComplexAttributes(
                        productDTO.getProdCode(),
                        productDTO.getSiteType(),
                        productDTO.getSiteDepth1(),
                        productDTO.getSiteDepth2(),
                        productDTO.getSiteDepth3()
                );

                //System.out.println("3");
                long randomNum = (long)(Math.random() * (10000000000L - 1000000000L) + 1000000000L);
                String randomNumber = Long.toString(randomNum);

                //현재 날짜 형식 변경
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
                String formattedDateTime = now.format(formatter);
                //System.out.println("4");

//                if(!foundProduct.isPresent()) {
//                    System.out.println("productDTO" + productDTO);
//                    System.out.println("종료");
//                    return null;
//                }

                if ((productCount == 0 || !foundProduct.isPresent())) { // siteType 확인, DB 저장된 값 확인
                    if (foundProduct.get().getProdName().trim().equals("피캄 베리어 사이클 락토P 토너200ml 기획(+화장솜 증정)")) {
                        System.out.println("productCount = " + productCount);
                        System.out.println("productDTO : " + productDTO);
                        System.out.println("foundProduct : " + foundProduct);
                        System.out.println("======================================= 찾음 ==================================");
                        return "";
                    }
                    //System.out.println("5");
                    if (Integer.parseInt(productDTO.getPrice()) > 0) {
                        //System.out.println("6");
                        String filePath = common.downloadImage(productDTO); // 이미지 다운로드
                        productDTO.setImg(filePath); // 이미지 경로 저장
                        //System.out.println("7");
                        Product savedProduct = ccproductRepository.save(productDTO.toEntity()); // 값 DB에 저장
                        //System.out.println("8");

                        ProductHistory productHistory = new ProductHistory();
                        productHistory.setId(productDTO.getSiteType() + formattedDateTime + randomNumber);
                        productHistory.setProductNo(savedProduct); // id가 있는 값을 넣어야 하므로 savedProduct 값 넣음
                        productHistory.setSiteType(productDTO.getSiteType());
                        productHistory.setProdCode(productDTO.getProdCode());
                        productHistory.setSoldOut(productDTO.getSoldOut());
                        productHistory.setPrice(Integer.parseInt(productDTO.getPrice()));
                        //제품 이력 저장
                        ccproductHistoryRepository.save(productHistory);
                        log.info("새 제품이 추가되었습니다");
                    }
                    //System.out.println("10");
                    continue;
                }
                // 변경시 @Transactional을 달아서 더티 체킹으로 업데이트

                //들어온 값과 DB의 Img값이 다를 경우
                if (!foundProduct.get().getImg().equals(productDTO.getImg())) {
                    //System.out.println("11");
                    String filePath = common.downloadImage(productDTO);
                    productDTO.setImg(filePath);
                }
                // 제품 이름 변경시 업데이트
                if (!foundProduct.get().getProdName().equals(productDTO.getProdName())) {
                    //System.out.println("12");
                    foundProduct.get().setProdName(productDTO.getProdName());
                }
                // soldOut 변경시 업데이트
                if(!foundProduct.get().getSoldOut().equals(productDTO.getSoldOut())){
                    //System.out.println("13");
                    foundProduct.get().setSoldOut(productDTO.getSoldOut());
                }
                // 브랜드 변경시 업데이트
                if(!foundProduct.get().getBrand().equals(productDTO.getBrand())){
                    //System.out.println("14");
                    foundProduct.get().setBrand(productDTO.getBrand());

                }
                // 가격 변동시 이력 저장 및 가격 업데이트
                if (foundProduct.get().getPrice() != Integer.parseInt(productDTO.getPrice())) {
                    //System.out.println("15");
                        System.out.println("foundProduct.getPrice() = " + foundProduct.get().getPrice());
                        System.out.println("Integer.parseInt(productDTO.getPrice()) = " + Integer.parseInt(productDTO.getPrice()));

                        ProductHistory productHistory = new ProductHistory();
                        productHistory.setId(productDTO.getSiteType() + formattedDateTime + randomNumber);
                        productHistory.setProductNo(foundProduct.get());
                        productHistory.setSiteType(productDTO.getSiteType());
                        productHistory.setProdCode(productDTO.getProdCode());
                        productHistory.setPrice(Integer.parseInt(productDTO.getPrice()));
                    //System.out.println("16");
                        if (productDTO.getSiteType().equals("CL") && productDTO.getSoldOut().equals("일시품절")) {
                            productDTO.setPrice(String.valueOf(foundProduct.get().getPrice())); // String 타입으로 변환
                            productHistory.setPrice(foundProduct.get().getPrice());
                            System.out.println("17");
                        }
                    //System.out.println("18");
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
        return "product 끝남";
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
