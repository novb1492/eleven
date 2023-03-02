package com.www.eleven.Payment.Controller;

import com.www.eleven.Common.UtilService;
import com.www.eleven.Payment.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/payment")
@Slf4j
public class PaymentAuthController {

    private final PaymentService paymentService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void KgCallBack(HttpServletRequest request, HttpServletResponse response) {
        String P_STATUS = request.getParameter("P_STATUS");       // 인증 상태
        String P_RMESG1 = request.getParameter("P_RMESG1");      // 인증 결과 메시지
        String P_TID = request.getParameter("P_TID");                   // 인증 거래번호
        String P_REQ_URL = request.getParameter("P_REQ_URL");    // 결제요청 URL
        String P_NOTI = request.getParameter("P_NOTI");              // 기타주문정보
        String P_MID = "INIpayTest";
        log.info("status:{}", P_STATUS);
        // 승인요청을 위한 P_MID, P_TID 세팅


        if (P_STATUS.equals("01")) { // 인증결과가 실패일 경우
            log.info("Auth Fail");
            log.info("<br>");
            log.info(P_RMESG1);
        }// STEP2 에 이어 인증결과가 성공일(P_STATUS=00) 경우 STEP2 에서 받은 인증결과로 아래 승인요청 진행
        else {
            // 승인요청할 데이터
            P_REQ_URL = P_REQ_URL + "?P_TID=" + P_TID + "&P_MID=" + P_MID;
            String[] values = UtilService.getKgValues(P_REQ_URL);
            LinkedHashMap<String, Object> paymentInfo = UtilService.setPaymentInfo(values);
            paymentService.changeStateAtDb(paymentInfo);

        }
    }
}
