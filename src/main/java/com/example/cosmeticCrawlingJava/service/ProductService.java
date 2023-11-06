package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.repository.ProductRepository;
import entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    private Logger logger = Logger.getLogger(ProductService.class.getName());

    @Transactional
    public void processProducts(List<Product> productList, int product_count) {
        for (Product product : productList) {
            try{

            }catch (DataIntegrityViolationException e){
                if(e.getMessage().contains("Duplicate entry")){
                    logger.warning("Integrity error occurred: Duplicate entry");
                }
            }
        }
    }
}
