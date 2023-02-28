package com.www.eleven.Price.Model;

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
@Table(name = "PRICE")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class PriceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRICE_ID",unique = true)
    private Long id;

    @Column(name = "HOUR",nullable = false,columnDefinition = "TINYINT")
    private Integer hour;

    @Column(name = "PRICE",nullable = false,length = 10)
    private String price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MID",referencedColumnName = "MARKET_ID",nullable = false)
    private MarketEntity marketEntity;

}
