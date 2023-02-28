package com.www.eleven.Market.Time;

import com.www.eleven.Common.CommonColumn;
import com.www.eleven.Market.Seat.Model.SeatEntity;
import com.www.eleven.Member.Model.MemberEntity;
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
@Table(name = "TIME_TABLE")
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

    @Embedded
    private CommonColumn commonColumn;
}
