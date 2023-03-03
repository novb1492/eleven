package com.www.eleven.Payment.Service;

import com.www.eleven.Common.CommonColumn;
import com.www.eleven.Common.Text;
import com.www.eleven.Common.UtilService;
import com.www.eleven.Member.Model.MemberEntity;
import com.www.eleven.Payment.Model.PaymentEntity;
import com.www.eleven.Payment.Repo.PaymentRepo;
import com.www.eleven.Time.Repo.TimeRepo;
import com.www.eleven.Time.Service.TimeService;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PaymentRepo paymentRepo;
    private final TimeService timeService;
    @Transactional(rollbackFor = Exception.class)
    public void changeStateAtDb(LinkedHashMap<String, Object> paymentInfo){
        System.out.println(paymentInfo);
        String oid=paymentInfo.get("P_OID").toString();
        String saveKey = oid + "paymentInfo";
        LinkedHashMap<String, Object> reservationInfo =   (LinkedHashMap<String, Object>)redisTemplate.opsForHash().entries(oid).get(oid);
        System.out.println(reservationInfo);
        checkPrice(Integer.parseInt(reservationInfo.get("totalPrice").toString()),Integer.parseInt(paymentInfo.get("P_AMT").toString()));
        List<Integer> hourArr = (List<Integer>) reservationInfo.get("hourArr");
        long sid = Long.parseLong(reservationInfo.get("seatId").toString());
        long pid = Long.parseLong(oid);
        timeService.okPayment(hourArr, sid, pid);
        UtilService.checkUpdateDone(paymentRepo.updateStateById(Text.trueState, pid), 1);
        redisTemplate.opsForHash().put(saveKey, saveKey, paymentInfo);
        redisTemplate.expire(oid, 1, TimeUnit.MICROSECONDS);
    }
    public void checkPrice(int price,int payPrice){
        if(price!=payPrice){
            throw new RuntimeException(Text.notEqualsPrice);
        }

    }




}
