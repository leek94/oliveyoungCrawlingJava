package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.repository.CcTempProductRepository;
import com.example.cosmeticCrawlingJava.entity.CcTempProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
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
