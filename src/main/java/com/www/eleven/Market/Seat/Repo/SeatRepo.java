package com.www.eleven.Market.Seat.Repo;

import com.www.eleven.Market.Dto.SelectSeatDto;
import com.www.eleven.Market.Seat.Model.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepo  extends JpaRepository<SeatEntity,Long> {

    @Query("select new com.www.eleven.Market.Dto.SelectSeatDto(s.id,s.url,s.name,s.floor,s.kind,s.people,s.left,s.top,s.soldOut,(select max(s.floor) from SeatEntity s where s.commonColumn.state=:state and s.floor=:floor and s.marketEntity.id=:mid)) " +
            "from SeatEntity s where s.commonColumn.state=:state and s.floor=:floor and s.marketEntity.id=:mid")
    List<SelectSeatDto> findByStateAndFloor(@Param("state") int state, @Param("floor") int floor,@Param("mid") long mid);
}
