package com.www.eleven.Market.Seat.Model;

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
@Table(name = "SEAT",indexes = {@Index(name = "SEAT_ID_INDEX", columnList = "SEAT_ID"),@Index(name = "FLOOR_INDEX", columnList = "FLOOR")})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_ID",unique = true)
    private Long id;

    @Column(name = "IMG_URL",nullable = false,length = 300)
    private String url;

    @Column(name = "SEAT_NAME",nullable = false,length = 20)
    private String name;

    @Column(name = "FLOOR",columnDefinition = "TINYINT")
    private Integer floor;

    @Column(name = "KIND",nullable = false,length = 10)
    private String kind;

    @Column(name = "PEOPLE",columnDefinition = "TINYINT")
    private Integer people;

    @Column(name = "LEFT_CSS",nullable = false,length = 10)
    private String left;

    @Column(name = "TOP_CSS",nullable = false,length = 10)
    private String top;

    @Column(name = "SOLD_OUT",columnDefinition = "TINYINT")
    private Integer soldOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "M_ID",referencedColumnName = "MARKET_ID" ,nullable = false)
    private MarketEntity marketEntity;

    @Embedded
    private CommonColumn commonColumn;

    @Override
    public String toString() {
        return "SeatEntity{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", floor=" + floor +
                ", kind='" + kind + '\'' +
                ", people=" + people +
                ", left='" + left + '\'' +
                ", top='" + top + '\'' +
                ", soldOut=" + soldOut +
                ", commonColumn=" + commonColumn +
                '}';
    }
}
