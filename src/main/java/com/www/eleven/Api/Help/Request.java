package com.www.eleven.Api.Help;

import com.www.eleven.Common.UtilService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Request {

    public static <T>  JSONObject requestPost(T body, String url, HttpHeaders headers) {
        HttpEntity<T> entity=new HttpEntity<>(body,headers);
        RestTemplate restTemplate=new RestTemplate();
        UtilService.writeRequestLog(entity.toString(),"외부 api 통신","requestPost");
        return restTemplate.postForObject(url, entity, JSONObject.class);

    }
    public static <T>  ResponseEntity<JSONObject> requestGet(T body,String url,HttpHeaders headers) {
        RestTemplate restTemplate=new RestTemplate();
        HttpEntity<T>entity=new HttpEntity<>(body,headers);
        UtilService.writeRequestLog(entity.toString(),"외부 api 통신","requestGet");
        ResponseEntity<JSONObject> responseEntity=restTemplate.exchange(url, HttpMethod.GET,entity,JSONObject.class);
        return responseEntity;
    }
}
