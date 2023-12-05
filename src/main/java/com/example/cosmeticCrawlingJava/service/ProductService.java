package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.dto.ProductDTO;
import com.example.cosmeticCrawlingJava.repository.CcProductHistoryRepository;
import com.example.cosmeticCrawlingJava.repository.CcProductRepository;
import com.example.cosmeticCrawlingJava.repository.CcTempProductRepository;
import com.example.cosmeticCrawlingJava.repository.CcProductHistoryRepository;
import com.example.cosmeticCrawlingJava.repository.CcProductRepository;
import com.example.cosmeticCrawlingJava.entity.CcTempProduct;
import com.example.cosmeticCrawlingJava.entity.Product;
import com.example.cosmeticCrawlingJava.entity.ProductHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.cosmeticCrawlingJava.util.Common;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final CcProductRepository ccproductRepository;
    private final CcTempProductRepository ccTempProductRepository;
    private final CcProductHistoryRepository ccproductHistoryRepository;

    @Transactional
    public String processProducts(List<ProductDTO> productList, int productCount) {
        for (ProductDTO productDTO : productList) {
            try {
                CcTempProduct ccTempProduct = new CcTempProduct();
                ccTempProduct.setProdCode(productDTO.getProdCode());
                ccTempProduct.setSiteType(productDTO.getSiteType());
                ccTempProductRepository.save(ccTempProduct); // cc 템플릿에 저장

                // 조건에 맞는 값을 받아옴 TODO : Optional로 감싸야하는 거 아닌지?
                Product foundProduct = ccproductRepository.findByComplexAttributes(
                        productDTO.getProdCode(),
                        productDTO.getSiteType(),
                        productDTO.getSiteDepth1(),
                        productDTO.getSiteDepth2(),
                        productDTO.getSiteDepth3()
                );

//                if (foundProduct.size() >1){
//
//                    System.out.println(foundProduct.get(0).getProdName());
//                    System.out.println(foundProduct.get(1).getProdName());
//                    return "갯수 두개 발생";
//                }

                // 난수 발생 1000000000L ~ 10000000000L
                long randomNum = (long)(Math.random() * (10000000000L - 1000000000L) + 1000000000L);
                String randomNumber = Long.toString(randomNum);

                //현재 날짜 형식 변경
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
                String formattedDateTime = now.format(formatter);

                if (productCount == 0 || foundProduct == null) { // siteType 확인, DB 저장된 값 확인
                    if (Integer.parseInt(productDTO.getPrice()) > 0) {
                        String filePath = Common.downloadImage(productDTO); // 이미지 다운로드
                        productDTO.setImg(filePath); // 이미지 경로 저장
                        ccproductRepository.save(productDTO.toEntity()); // 값 DB에 저장

                        ProductHistory productHistory = new ProductHistory();
                        productHistory.setHistoryNo(productDTO.getSiteType() + formattedDateTime + randomNumber);
                        productHistory.setProductNo(foundProduct);
                        productHistory.setSiteType(productDTO.getSiteType());
                        productHistory.setProdCode(productDTO.getProdCode());
                        productHistory.setPrice(Integer.parseInt(productDTO.getPrice()));

                        //제품 이력 저장
                        ccproductHistoryRepository.save(productHistory);
                        log.info("새 제품이 추가되었습니다");
                    }
                    continue;
                }
//                 들어온 값과 DB의 Img값이 다를 경우
                if (!foundProduct.getImg().equals(productDTO.getImg())) {
                    String filePath = Common.downloadImage(productDTO);
                    productDTO.setImg(filePath);
                    ccproductRepository.save(productDTO.toEntity()); //DB에 다시 저장
                } else if (!foundProduct.getProdName().equals(productDTO.getProdName()) ||
                        !foundProduct.getSoldOut().equals(productDTO.getSoldOut()) ||
                        !foundProduct.getBrand().equals(productDTO.getBrand())) {
                    ccproductRepository.save(productDTO.toEntity());
                } else {
                    if (foundProduct.getPrice() != Integer.parseInt(productDTO.getPrice())) {
                        ProductHistory productHistory = new ProductHistory();
                        productHistory.setHistoryNo(productDTO.getSiteType() + formattedDateTime + randomNumber);
                        productHistory.setProductNo(foundProduct);
                        productHistory.setSiteType(productDTO.getSiteType());
                        productHistory.setProdCode(productDTO.getProdCode());
                        productHistory.setPrice(Integer.parseInt(productDTO.getPrice()));

                        if (productDTO.getSiteType().equals("CL") && productDTO.getSoldOut().equals("일시품절")) {
                            productDTO.setPrice(String.valueOf(foundProduct.getPrice())); // String 타입으로 변환
                            productHistory.setPrice(foundProduct.getPrice());
                        }

                        //제품 이력 저장
                        ccproductHistoryRepository.save(productHistory);
                        ccproductRepository.save(productDTO.toEntity());
                    }
                }
            }catch (DataIntegrityViolationException e) {
                log.warn("예외가 발생했습니다 : " + e.getMessage());
            } catch (Exception e) {
                log.warn(" 예외 발생 : " + e.getMessage());
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
