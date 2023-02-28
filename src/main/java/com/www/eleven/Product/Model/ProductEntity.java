package com.www.eleven.Product.Model;

import com.www.eleven.Common.CommonColumn;
import com.www.eleven.Market.Model.MarketEntity;
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
@Table(name = "PRODUCT")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID",unique = true)
    private Long id;

    @Column(name = "PRODUCT_IMG_URL",nullable = false,length = 250)
    private String imgUrl;

    @Column(name = "PRICE",nullable = false,length = 10)
    private String price;

    @Column(name = "PRODUCT_NAME",nullable = false,length = 20)
    private String name;

    @Column(name = "KIND",nullable = false,columnDefinition = "TINYINT")
    private Integer kind;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MID",referencedColumnName = "MARKET_ID")
    private MarketEntity marketEntity;

    @Embedded
    private CommonColumn commonColumn;
}
