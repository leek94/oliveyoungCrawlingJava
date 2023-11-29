package com.example.cosmeticCrawlingJava.service;

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
    public String processProducts(List<Product> productList, int productCount) {
        for (Product product : productList) {
            try {
                CcTempProduct ccTempProduct = new CcTempProduct();
                ccTempProduct.setProdCode(product.getProdCode());
                ccTempProduct.setSiteType(product.getSiteType());
                ccTempProductRepository.save(ccTempProduct); // cc 템플릿에 저장

                // 조건에 맞는 값을 받아옴 TODO : Optional로 감싸야하는 거 아닌지?
                Product foundProduct = ccproductRepository.findByComplexAttributes(
                        product.getProdCode(),
                        product.getSiteType(),
                        product.getSiteDepth1(),
                        product.getSiteDepth2(),
                        product.getSiteDepth3()
                );

//                if (foundProduct.size() >1){
//
//                    System.out.println(foundProduct.get(0).getProdName());
//                    System.out.println(foundProduct.get(1).getProdName());
//                    return "갯수 두개 발생";
//                }

                // 난수 발생 1000000000L ~ 10000000000L
                long randomNum = (long) (Math.random() * (10000000000L - 1000000000L) + 1000000000L);
                String randomNumber = Long.toString(randomNum);

                //현재 날짜 형식 변경
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
                String formattedDateTime = now.format(formatter);

                if (productCount == 0 || foundProduct == null) { // siteType 확인, DB 저장된 값 확인
                    if (product.getPrice() > 0) {
                        String filePath = Common.downloadImage(product); // 이미지 다운로드
                        product.setImg(filePath); // 이미지 경로 저장
                        ccproductRepository.save(product); // 값 DB에 저장

                        ProductHistory productHistory = new ProductHistory();
                        productHistory.setHistoryNo(product.getSiteType() + formattedDateTime + randomNumber);
                        productHistory.setProductNo(product);
                        productHistory.setSiteType(product.getSiteType());
                        productHistory.setProdCode(product.getProdCode());
                        productHistory.setPrice(product.getPrice());

                        //제품 이력 저장
                        ccproductHistoryRepository.save(productHistory);
                        log.info("새 제품이 추가되었습니다");
                    }
                    continue;
                }
                // 들어온 값과 DB의 Img값이 다를 경우
//                if (!foundProduct.getImg().equals(product.getImg())) {
//                    String filePath = Common.downloadImage(product);
//                    product.setImg(filePath);
//                    productRepository.save(product); //DB에 다시 저장
//                } else if (!foundProduct.getProdName().equals(product.getProdName()) ||
//                        !foundProduct.getSoldOut().equals(product.getSoldOut()) ||
//                        !foundProduct.getBrand().equals(product.getBrand())) {
//                    productRepository.save(product);
//                } else {
//                    if (foundProduct.getPrice() != (product.getPrice())) {
//                        ProductHistory productHistory = new ProductHistory();
//                        productHistory.setHistoryNo(product.getSiteType() + formattedDateTime + randomNumber);
//                        productHistory.setProductNo(product.getId());
//                        productHistory.setSiteType(product.getSiteType());
//                        productHistory.setProdCode(product.getProdCode());
//                        productHistory.setPrice(product.getPrice());
//
//                        if (product.getSiteType().equals("CL") && product.getSoldOut().equals("일시품절")) {
//                            product.setPrice(foundProduct.getPrice());
//                            productHistory.setPrice(foundProduct.getPrice());
//                        }
//
//                        //제품 이력 저장
//                        productHistoryRepository.save(productHistory);
//                        productRepository.save(product);
//                    }
//                }
            }catch (DataIntegrityViolationException e) {
                log.warn("예외가 발생했습니다");
            } catch (Exception e) {

            }
//            }catch (UnsupportedEncodingException e) {
//                common.sendMail(e.getMessage(), product.getSiteType());
//            }catch (IOException e) {
//                logger.warning(product.getProdCode() + "중복");
//            }
        }
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
