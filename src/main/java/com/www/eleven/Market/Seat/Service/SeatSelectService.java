package com.www.eleven.Market.Seat.Service;

import com.www.eleven.Common.Text;
import com.www.eleven.Market.Dto.SelectSeatDto;
import com.www.eleven.Market.Seat.Model.SeatEntity;
import com.www.eleven.Market.Seat.Repo.SeatRepo;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatSelectService {
    private final SeatRepo seatRepo;

    public JSONObject selectByFloor(int floor,long mid){
        JSONObject response = new JSONObject();
        List<SelectSeatDto> selectSeatDtos = seatRepo.findByStateAndFloor(Text.trueState, floor,mid);
        List<SelectSeatDto> seats = new ArrayList<>();
        for(SelectSeatDto s: selectSeatDtos){
            if(s.getKind().equals("drawing")){
                response.put("drawing", s.getUrl());
            }else if(s.getKind().equals("counter")){
                response.put("counter", s);
            }else{
                seats.add(s);
            }
        }
        response.put("floor", selectSeatDtos.get(0).getMaxFloor());
        response.put("seats", seats);
        return response;
    }
}
