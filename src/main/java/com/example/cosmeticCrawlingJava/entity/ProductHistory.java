package com.example.cosmeticCrawlingJava.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "product_history")
@NoArgsConstructor
@Getter
@Setter
public class ProductHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String historyNo;
    private Long productNo;
    private String siteType;
    private String prodCode;
    private int price;

}
