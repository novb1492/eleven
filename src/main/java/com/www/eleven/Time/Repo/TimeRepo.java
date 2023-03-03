package com.www.eleven.Time.Repo;

import com.www.eleven.Time.Model.TimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface TimeRepo extends JpaRepository<TimeEntity,Long>,TimeRepoCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TimeEntity t where t.seatEntity.id=:sid and  t.time=:hour and t.commonColumn.state=:ts")
    Optional<TimeEntity> findBySeatAndHourLock(@Param("sid") long sId, @Param("hour") Timestamp hour,@Param("ts")int ts);
    @Query("select t from TimeEntity t where t.seatEntity.id=:sid and  t.time between :ot and :ct")
    List<TimeEntity> findBySeat(@Param("sid") long sId, @Param("ot") Timestamp ot, @Param("ct") Timestamp ct);

    @Modifying
    @Query("update TimeEntity t set t.commonColumn.state=:ts where t.commonColumn.state<>:ts and t.time=:time and t.paymentEntity.id=:pid")
    Integer updateStateForReservation(@Param("ts") int ts,@Param("time")Timestamp time,@Param("pid")long pid);
}
