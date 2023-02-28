package com.www.eleven.Market.Dto;

import com.www.eleven.Market.Seat.Model.SeatEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@NoArgsConstructor
@Data
public class SelectSeatDto {
    private Long id;
    private String url;
    private String name;
    private Integer floor;
    private String kind;
    private Integer people;
    private String left;
    private String top;
    private Integer soldOut;
    private Integer maxFloor;

    public SelectSeatDto(Long id, String url, String name, Integer floor, String kind, Integer people, String left, String top, Integer soldOut,Integer maxFloor) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.floor = floor;
        this.kind = kind;
        this.people = people;
        this.left = left;
        this.top = top;
        this.soldOut=soldOut;
        this.maxFloor = maxFloor;

    }
}
