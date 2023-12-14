package com.example.cosmeticCrawlingJava.entity;

import com.example.cosmeticCrawlingJava.enumfile.TopYn;
import com.example.cosmeticCrawlingJava.enumfile.UseYn;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "cc_site_event")
@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SiteEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "event_title")
    private String eventTitle;

    @Column(name = "event_content")
    private String eventContent;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "use_yn", nullable = false)
    private UseYn useYn = UseYn.Y;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "top_yn", nullable = false)
    private TopYn topYn = TopYn.N;

    @Column(name = "event_link", nullable = false)
    private String eventLink;

    @Column(name = "event_seq", nullable = false, length = 100)
    private String eventSeq;

    @Column(name = "site_type", nullable = false, length = 10)
    private String siteType;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "event_recruit", length = 50)
    private String eventRecruit;

    @Column(name = "event_img", nullable = false)
    private String eventImg;

    @Column(name = "reg_id")
    private String regId;

    @PrePersist
    protected void onCreate() {
//        useYn = UseYn.Y;
//        topYn = TopYn.N;
        regDate = LocalDateTime.now(); // Entity가 생성될 때 현재 시간 설정
    }
}