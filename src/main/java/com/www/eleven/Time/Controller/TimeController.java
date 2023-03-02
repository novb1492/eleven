package com.www.eleven.Time.Controller;

import com.www.eleven.Time.Dto.TimeInsertDto;
import com.www.eleven.Time.Service.TimeService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Random;

@Service
@RequiredArgsConstructor
@RequestMapping(value = "/api/reservation")
public class TimeController {

    private final TimeService timeService;

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ResponseEntity<?>save(@Valid @RequestBody TimeInsertDto data){
        JSONObject response = new JSONObject();
        timeService.save(data);
        response.put("name", "커피와자리");
        response.put("price",1000);
        response.put("paymentid",new Random().nextInt(10));
        return ResponseEntity.ok().body(response);
    }
}
