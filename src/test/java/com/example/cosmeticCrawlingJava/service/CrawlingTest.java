package com.example.cosmeticCrawlingJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CrawlingTest {

    @Autowired
    private Crawling crawling;

    void test() {
        crawling.startCrawling();
    }
}