package com.example.cosmeticCrawlingJava.repository;

import com.example.cosmeticCrawlingJava.entity.CcTempProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CcTempProductRepository extends JpaRepository<CcTempProduct, Long> {
}
