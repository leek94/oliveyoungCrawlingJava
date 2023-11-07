package com.example.cosmeticCrawlingJava.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String img;
    private String imgPath;
    private String info;
    private String prodName;
    private String prodCode;
    private int price;
    private int bePrice;
    private double sale;
    private String soldOut;
    private String siteDepth1;
    private String siteDepth2;
    private String siteDepth3;
    private String siteType;
    private String brand;

    public Product(Long id,String img, String imgPath, String info, String prodName, String prodCode, int price, int bePrice, double sale, String soldOut, String siteDepth1, String siteDepth2, String siteDepth3, String siteType, String brand) {
        this.id = null;
        this.img = img;
        this.imgPath = imgPath;
        this.info = info;
        this.prodName = prodName;
        this.prodCode = prodCode;
        this.price = price;
        this.bePrice = bePrice;
        this.sale = sale;
        this.soldOut = soldOut;
        this.siteDepth1 = siteDepth1;
        this.siteDepth2 = siteDepth2;
        this.siteDepth3 = siteDepth3;
        this.siteType = siteType;
        this.brand = brand;
    }
}
