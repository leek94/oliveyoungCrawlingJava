package com.example.cosmeticCrawlingJava.entity;

import com.example.cosmeticCrawlingJava.enumfile.TopYn;
import com.example.cosmeticCrawlingJava.enumfile.UseYn;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "cc_site_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CcSiteEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition = "INT")
    private Long id;

    private String eventTitle;

    private String eventContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "use_yn")
    private UseYn useYn;

    @Enumerated(EnumType.STRING)
    @Column(name = "top_yn")
    private TopYn topYn;


}