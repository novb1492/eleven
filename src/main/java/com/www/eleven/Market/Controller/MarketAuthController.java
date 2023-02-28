package com.www.eleven.Market.Controller;

import com.www.eleven.Market.Dto.SelectSeatDto;
import com.www.eleven.Market.Seat.Service.SeatSelectService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MarketAuthController {
    private final SeatSelectService seatSelectService;

    @RequestMapping(value = "/api/auth/{floor}/seat/{mid}",method = RequestMethod.GET)
    public ResponseEntity<?>getSeatByFloor(@PathVariable Integer floor,@PathVariable Long mid){
        JSONObject response=seatSelectService.selectByFloor(floor,mid);
        return ResponseEntity.ok().body(response);
    }
}
