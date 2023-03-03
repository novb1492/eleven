package com.www.eleven.Time.Controller;

import com.www.eleven.Time.Dto.TimeInsertDto;
import com.www.eleven.Time.Service.TimeService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Random;

@Service
@RequiredArgsConstructor
@RequestMapping(value = "/api/time")
public class TimeController {

    private final TimeService timeService;

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ResponseEntity<?>save(@Valid @RequestBody TimeInsertDto data){
        JSONObject response = timeService.save(data);
        response.put("name", "커피와자리");
        return ResponseEntity.ok().body(response);
    }
    @RequestMapping(value = "/{start}/{end}/time/{page}/list",method = RequestMethod.GET)
    public ResponseEntity<?>getList(@PathVariable String start, @PathVariable String end, @PathVariable Integer page){
        JSONObject response = new JSONObject();
        return ResponseEntity.ok().body(response);
    }
}
