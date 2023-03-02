package com.www.eleven.Payment.Model;

import com.www.eleven.Common.CommonColumn;
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
@Table(name = "PAYMENT")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID",unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MID",referencedColumnName = "MEMBER_ID",nullable = false)
    private MemberEntity buyer;

    @Column(name = "PRICE")
    private Integer price;

    @Embedded
    private CommonColumn commonColumn;


}
