package com.example.cosmeticCrawlingJava.repository;

import com.example.cosmeticCrawlingJava.entity.SiteEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CcSiteEventRepository extends JpaRepository<SiteEvent, Long> {

    @Query(value = "SELECT * FROM cc_site_event WHERE site_type = :siteType", nativeQuery = true)
    List<SiteEvent> findBySiteType(@Param("siteType") String siteType);

    @Query(value = "SELECT * FROM cc_site_event WHERE site_type = :siteType AND event_recruit IS NOT NULL ", nativeQuery = true)
    List<SiteEvent> findBySiteTypeAndRecruitNotNull(@Param("siteType") String siteType);

}
