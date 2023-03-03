package com.www.eleven.Time.Dto;

import com.querydsl.core.annotations.QueryProjection;
import com.www.eleven.Common.UtilService;
import org.springframework.data.domain.Page;

import java.util.List;

public class SelectForListDto {
    private long reservationId;
    private String seat;
    private String reservationDate;
    private String insertDate;
    private List<Integer> times;

    @QueryProjection
    public SelectForListDto(long reservationId, String seat, String reservationDate, String insertDate, List<Integer> times) {
        this.reservationId = reservationId;
        this.seat = seat;
        this.reservationDate = reservationDate;
        this.insertDate = insertDate;
        this.times = times;
    }
}
