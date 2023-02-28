package com.www.eleven.Product.Controller;

import com.www.eleven.Product.Service.SelectProductService;
import com.www.eleven.Time.Service.SelectTimeService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/product")
public class ProductAuthController {
    private final SelectProductService selectService;
    private final SelectTimeService selectTimeService;

    @RequestMapping("/{kindId}/list/{seatId}/time/{mid}")
    public ResponseEntity<?>getProducts(@PathVariable Integer kindId,@PathVariable Long seatId ,@PathVariable Long mid){
        JSONObject response = new JSONObject();
        response.put("products",selectService.selectByKindAndMid(mid,kindId));
        response.put("times",selectTimeService.selectBySeatId(seatId,mid));
        return ResponseEntity.ok().body(response);
    }
}
