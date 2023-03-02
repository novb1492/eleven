package com.www.eleven.Time.Model;

import com.www.eleven.Common.CommonColumn;
import com.www.eleven.Market.Model.MarketEntity;
import com.www.eleven.Market.Seat.Model.SeatEntity;
import com.www.eleven.Member.Model.MemberEntity;
import com.www.eleven.Payment.Model.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "TIME"
        ,indexes = {@Index(name = "SEAT_ID_INDEX", columnList = "SEAT_ID")
        ,@Index(name = "TIME_INDEX", columnList = "TIME")
        ,@Index(name = "MID_INDEX", columnList = "MID")
        ,@Index(name = "PID_INDEX", columnList = "PID")})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIME_ID",unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSERT_USER",referencedColumnName = "MEMBER_ID",nullable = false)
    private MemberEntity insertUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEAT_ID",referencedColumnName = "SEAT_ID",nullable = false)
    private SeatEntity seatEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MID",referencedColumnName = "MARKET_ID",nullable = false)
    private MarketEntity marketEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PID",referencedColumnName = "PAYMENT_ID",nullable = false)
    private PaymentEntity paymentEntity;

    @Column(name = "TIME",nullable = false)
    private Timestamp time;

    @Column(name = "PRODUCTS",nullable = false,length = 100)
    private String products;

    @Embedded
    private CommonColumn commonColumn;
}
