package com.example.cosmeticCrawlingJava.entity;

public class Category {
    private String siteDepth1;
    private String siteDepth2;
    private String siteLink;

    public Category(String siteDepth1, String siteDepth2, String siteLink) {
        this.siteDepth1 = siteDepth1;
        this.siteDepth2 = siteDepth2;
        this.siteLink = siteLink;
    }

    public String getSiteDepth1() {
        return siteDepth1;
    }

    public String getSiteDepth2() {
        return siteDepth2;
    }

    public String getSiteLink() {
        return siteLink;
    }

    @Override
    public String toString() {
        return "siteDepth1: " + siteDepth1 + ", siteDepth2: " + siteDepth2 + ", siteLink: " + siteLink;
    }
}