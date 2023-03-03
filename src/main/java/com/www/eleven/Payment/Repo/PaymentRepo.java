package com.www.eleven.Payment.Repo;

import com.www.eleven.Payment.Model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<PaymentEntity,Long> {

    Optional<PaymentEntity> findById(Long aLong);

    @Modifying
    @Query("update PaymentEntity p set p.commonColumn.state=:st where p.id=:id")
    Integer updateStateById(@Param("st") int state, @Param("id") long id);
}
