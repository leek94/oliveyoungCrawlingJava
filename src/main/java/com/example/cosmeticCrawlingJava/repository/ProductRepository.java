package com.example.cosmeticCrawlingJava.repository;

import entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.prodCode = : prodCode AND p.siteType = :siteType AND p.siteDepth1 = :siteDepth1 AND p.siteDepth2 = :siteDepth2 AND p.siteDepth3 = :siteDepth3")
    Product findByComplexAttributes(@Param("prodCode") String prodCode,
                                    @Param("siteType") String siteType,
                                    @Param("siteDepth1") String siteDepth1,
                                    @Param("siteDepth2") String siteDepth2,
                                    @Param("siteDepth3") String siteDepth3
    );
}
