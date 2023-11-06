package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.repository.CcTempProductRepository;
import entity.CcTempProduct;
import entity.Product;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

public class CcTempProductService {
    @Autowired
    private CcTempProductRepository ccTempProductRepository;

    @Transactional
    public void insertIntoTempProduct(String prodCode, String siteType) {
        CcTempProduct tempProduct = new CcTempProduct();
        tempProduct.setProdCode(prodCode);
        tempProduct.setSiteType(siteType);

        ccTempProductRepository.save(tempProduct);
    }
}
