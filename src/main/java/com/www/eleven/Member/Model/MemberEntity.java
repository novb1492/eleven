package com.www.eleven.Member.Model;

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
@Table(name = "MEMBER",indexes = {@Index(name = "USER_ID_INDEX", columnList = "USER_ID")})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID",unique = true)
    private Long id;

    @Column(name = "USER_ID",nullable = false,length = 50)
    private String userId;

    @Column(name = "PWD",nullable = false,length = 1000)
    private String pwd;

    @Embedded
    private CommonColumn commonColumn;

    @Override
    public String toString() {
        return "MemberEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", pwd='" + pwd + '\'' +
                ", commonColumn=" + commonColumn +
                '}';
    }
}
