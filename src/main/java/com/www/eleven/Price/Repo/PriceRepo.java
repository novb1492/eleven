package com.www.eleven.Price.Repo;

import com.www.eleven.Price.Model.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PriceRepo extends JpaRepository<PriceEntity,Long> {

    @Query("select p from PriceEntity p where p.marketEntity.id=:id and p.hour <=:h")
    List<PriceEntity> findByMidAndHour(@Param("id") long mid, @Param("h") int hour);
}
