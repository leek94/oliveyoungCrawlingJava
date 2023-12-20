package com.example.cosmeticCrawlingJava.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "cc_product_history")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductHistory {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_no", nullable = false, length = 30)
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_no", nullable = false)
    private Product productNo;

    @Column(name = "site_type", nullable = false, length = 2)
    private String siteType;

    @Column(name = "prod_code", length = 50)
    private String prodCode;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "sold_out", length = 50)
    private String soldOut;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;


    @PrePersist
    protected void onCreate() {
        regDate = LocalDateTime.now(); // Entity가 생성될 때 현재 시간 설정
    }

    public ProductHistory(String id, Product productNo, String siteType, String prodCode, int price) {
        this.id = id;
        this.productNo = productNo;
        this.siteType = siteType;
        this.prodCode = prodCode;
        this.price = price;

    }
}
