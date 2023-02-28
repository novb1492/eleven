package com.www.eleven.Time.Controller;

import com.www.eleven.Time.Service.SelectTimeService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Service
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth/time")
public class TimeAuthController {
    private final SelectTimeService selectTimeService;

    @RequestMapping(value = "/{size}/{mid}",method = RequestMethod.GET)
    public ResponseEntity<?>plusTime(@PathVariable Integer size,@PathVariable Integer mid){
        JSONObject response = new JSONObject();
        response.put("minPrice",selectTimeService.getPriceByHour(size,mid));
        return ResponseEntity.ok().body(response);
    }
}
