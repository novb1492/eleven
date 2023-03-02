package com.www.eleven.Payment.Service;

import com.www.eleven.Common.UtilService;
import com.www.eleven.Payment.Repo.PaymentRepo;
import com.www.eleven.Time.Repo.TimeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final TimeRepo timeRepo;
    private final PaymentRepo paymentRepo;

    @Transactional(rollbackFor = Exception.class)
    public void changeStateAtDb(LinkedHashMap<String, Object> paymentInfo){
        System.out.println(paymentInfo);
//        String oid=paymentInfo.get("")
//        redisTemplate.opsForHash().get()
    }


}
