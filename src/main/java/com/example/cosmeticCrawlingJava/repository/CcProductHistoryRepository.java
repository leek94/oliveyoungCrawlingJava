package com.example.cosmeticCrawlingJava.repository;

import com.example.cosmeticCrawlingJava.entity.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CcProductHistoryRepository extends JpaRepository<ProductHistory, Long> {
}
