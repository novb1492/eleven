package com.www.eleven.Time.Repo;

import com.www.eleven.Time.Model.TimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface TimeRepo extends JpaRepository<TimeEntity,Long> {

    @Query("select t from TimeEntity t where t.seatEntity.id=:sid and  t.time between :ot and :ct")
    List<TimeEntity> findBySeat(@Param("sid") long sId, @Param("ot") Timestamp ot, @Param("ct") Timestamp ct);
}
