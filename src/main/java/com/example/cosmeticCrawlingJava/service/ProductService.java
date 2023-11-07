package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.repository.CcTempProductRepository;
import com.example.cosmeticCrawlingJava.repository.ProductHistoryRepository;
import com.example.cosmeticCrawlingJava.repository.ProductRepository;
import com.example.cosmeticCrawlingJava.entity.CcTempProduct;
import com.example.cosmeticCrawlingJava.entity.Product;
import com.example.cosmeticCrawlingJava.entity.ProductHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.cosmeticCrawlingJava.util.common;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CcTempProductRepository ccTempProductRepository;
    @Autowired
    private ProductHistoryRepository productHistoryRepository;

    private Logger logger = Logger.getLogger(ProductService.class.getName());

    @Transactional
    public void processProducts(List<Product> productList, int productCount) {
        for (Product product : productList) {
            try {
                CcTempProduct ccTempProduct = new CcTempProduct();
                ccTempProduct.setProdCode(product.getProdCode());
                ccTempProduct.setSiteType(product.getSiteType());
                ccTempProductRepository.save(ccTempProduct);


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
                    if (foundProduct.getPrice() > 0) {
                        String filePath = common.downloadImage(foundProduct);
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
                        logger.info("새 제품이 추가되었습니다");
                    }
                    continue;
                }

                if (!foundProduct.getImg().equals(product.getImg())) {
                    String filePath = common.downloadImage(product);
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
                logger.warning("예외가 발생했습니다");
            }
//            }catch (UnsupportedEncodingException e) {
//                common.sendMail(e.getMessage(), product.getSiteType());
//            }catch (IOException e) {
//                logger.warning(product.getProdCode() + "중복");
//            }
        }
    }


}
