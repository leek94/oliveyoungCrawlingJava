package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.repository.CcTempProductRepository;
import com.example.cosmeticCrawlingJava.repository.ProductHistoryRepository;
import com.example.cosmeticCrawlingJava.repository.ProductRepository;
import com.example.cosmeticCrawlingJava.entity.CcTempProduct;
import com.example.cosmeticCrawlingJava.entity.Product;
import com.example.cosmeticCrawlingJava.entity.ProductHistory;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final CcTempProductRepository ccTempProductRepository;
    private final ProductHistoryRepository productHistoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          CcTempProductRepository ccTempProductRepository,
                          ProductHistoryRepository productHistoryRepository) {
        this.productRepository = productRepository;
        this.ccTempProductRepository = ccTempProductRepository;
        this.productHistoryRepository = productHistoryRepository;
    }

    @Transactional
    public void processProducts(List<Product> productList, int productCount) {
        for (Product product : productList) {
            try {
                CcTempProduct ccTempProduct = new CcTempProduct();
                ccTempProduct.setProdCode(product.getProdCode());
                ccTempProduct.setSiteType(product.getSiteType());
                ccTempProductRepository.save(ccTempProduct); // cc 템플릿에 저장

                // 저장되어있는 값을 받아옴 TODO : Optional로 감싸야하는 거 아닌지?
                Product foundProduct = productRepository.findByComplexAttributes(
                        product.getProdCode(),
                        product.getSiteType(),
                        product.getSiteDepth1(),
                        product.getSiteDepth2(),
                        product.getSiteDepth3()
                );

                //난수 발생
                long randomNum = (long) (Math.random() * (10000000000L - 1000000000L) + 1000000000L);
                String randomNumber = Long.toString(randomNum);

                //현재 날짜 형식 변경
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
                String formattedDateTime = now.format(formatter);

                if (productCount == 0 || foundProduct == null) {
                    if (product.getPrice() > 0) {
                        String filePath = Common.downloadImage(product);
                        product.setImg(filePath);
                        productRepository.save(product);

                        ProductHistory productHistory = new ProductHistory();
                        productHistory.setHistoryNo(product.getSiteType() + formattedDateTime + randomNumber);
                        productHistory.setProductNo(product.getId());
                        productHistory.setSiteType(product.getSiteType());
                        productHistory.setProdCode(product.getProdCode());
                        productHistory.setPrice(product.getPrice());

                        //제품 이력 저장
                        productHistoryRepository.save(productHistory);
                        log.info("새 제품이 추가되었습니다");
                    }
                    continue;
                }

                if (!foundProduct.getImg().equals(product.getImg())) {
                    String filePath = Common.downloadImage(product);
                    product.setImg(filePath);
                    productRepository.save(product);
                } else if (!foundProduct.getProdName().equals(product.getProdName()) ||
                        !foundProduct.getSoldOut().equals(product.getSoldOut()) ||
                        !foundProduct.getBrand().equals(product.getBrand())) {
                    productRepository.save(product);
                } else {
                    if (foundProduct.getPrice() != (product.getPrice())) {
                        ProductHistory productHistory = new ProductHistory();
                        productHistory.setHistoryNo(product.getSiteType() + formattedDateTime + randomNumber);
                        productHistory.setProductNo(product.getId());
                        productHistory.setSiteType(product.getSiteType());
                        productHistory.setProdCode(product.getProdCode());
                        productHistory.setPrice(product.getPrice());

                        if (product.getSiteType().equals("CL") && product.getSoldOut().equals("일시품절")) {
                            product.setPrice(foundProduct.getPrice());
                            productHistory.setPrice(foundProduct.getPrice());
                        }

                        //제품 이력 저장
                        productHistoryRepository.save(productHistory);
                        productRepository.save(product);
                    }
                }
            }catch (DataIntegrityViolationException e) {
                log.warn("예외가 발생했습니다");
            }
//            }catch (UnsupportedEncodingException e) {
//                common.sendMail(e.getMessage(), product.getSiteType());
//            }catch (IOException e) {
//                logger.warning(product.getProdCode() + "중복");
//            }
        }
    }

    @Transactional
    public int countBySiteType(String siteType) {
        try {
            int count = productRepository.countBySiteType(siteType);
            return (count > 0) ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }



}
