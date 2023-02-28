package com.www.eleven.Market.Model;

import com.www.eleven.Common.CommonColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "MARKET",indexes = {@Index(name = "ADDRESS_INDEX", columnList = "ADDRESS")})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class MarketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MARKET_ID",unique = true)
    private Long id;

    @Column(name = "IMG_URL",nullable = false,length = 300)
    private String imgUrl;

    @Column(name = "OT",nullable = false,length = 20)
    private String ot;

    @Column(name = "CT",nullable = false,length = 20)
    private String ct;

    @Column(name = "ADDRESS",nullable = false,length = 100)
    private String address;

    @Column(name = "POST",nullable = false,length = 10)
    private String post;

    @Column(name = "DE_ADDRESS",nullable = false,length = 10)
    private String deAddress;

    @Embedded
    private CommonColumn commonColumn;
}
