package com.www.eleven.Payment.Repo;

import com.www.eleven.Payment.Model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<PaymentEntity,Long> {

    Optional<PaymentEntity> findById(Long aLong);
}
