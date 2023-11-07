package com.example.cosmeticCrawlingJava.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "cc_temp_product")
@Getter
@Setter
public class CcTempProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prodCode;
    private String siteType;
}
