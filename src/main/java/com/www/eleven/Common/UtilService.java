package com.www.eleven.Common;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.www.eleven.Member.Model.MemberEntity;
import com.www.eleven.Member.Model.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class UtilService {
    /**
     * 예외 로그 남기는 함수
     * @param value
     * @param message
     * @param exMessage
     */
    public static void writeFailLog(Object value,String message,String exMessage){
        try {
            log.error(message+Text.logLine);
            log.error("값:{} 메세지:{}",value,message);
            log.error(message+Text.logLine);
        }catch (Exception e){
            log.error("요청 로그 생성 실패");
        }
    }
    /**
     * 예외 발생시 StackTrace 로그 찍는 함수
     * @param e
     */
    public static void writeStackTrace(Exception e){
        StackTraceElement[] ste = e.getStackTrace();
        StringBuffer str = new StringBuffer();
        int lastIndex = ste.length - 1;
        int count = 1;
        for (int i = lastIndex; i>lastIndex-3; i--) {
            String className = ste[i].getClassName();
            String methodName = ste[i].getMethodName();
            int lineNumber = ste[i].getLineNumber();
            String fileName = ste[i].getFileName();
            str.append("\n").append("[" +count++ + "]")
                    .append("className :").append(className).append("\n")
                    .append("methodName :").append(methodName).append("\n")
                    .append("fileName :").append(fileName).append("\n")
                    .append("lineNumber :").append(lineNumber).append("\n")
                    .append("message :").append(e.getMessage()).append("\n")
                    .append("cause :").append(e.getCause()).append("\n");
        }
        log.error(str.toString());
    }
    /**
     * 로그 남기는 함수
     * @param value
     * @param message
     * @param methodName
     */
    public static void writeRequestLog(Object value,String message,String methodName){
        try {
            log.info("요청 행위:{} 값:{} 함수이름:{}",message,value,methodName);
        }catch (NullPointerException e){
            log.error("요청 행위:{} 값:{} 함수이름:{}",message,null,methodName);
        }catch (Exception e){
            log.error("요청 로그 생성 실패");
        }
    }
    /**
     * 문자열이 null or 공백인지 검사하는 함수
     * null or 공백=true
     * 정상=false
     * @param s
     * @return
     */
    public static boolean checkStringNull(String s){
        if(!StringUtils.hasText(s)){
            return true;
        }else if(s.isBlank()||s.equals("null")||s.equals("undefined")){
            return true;
        }
        return false;
    }

    /**
     * 지정 url로 이동시키는 함수
     * @param url
     * @param request
     * @param response
     */
    public static void goForward(String url,HttpServletRequest request,HttpServletResponse response){
        RequestDispatcher dp=request.getRequestDispatcher(url);
        try {
            dp.forward(request, response);
        } catch (ServletException | IOException e) {
            log.error(Text.logLine);
            log.error("goForward 실패 요청 url:{}",url);
            log.error(e.getMessage());
            log.error(Text.logLine);
        }
    }

    /**
     * 쿠키에서 인증 jwt 토큰 가져오는 함수
     * @param request
     * @param accessCookieName
     * @param refreshCookieName
     * @return
     */
    public static Map<String,String>getAuthorizationTokenAtCookie(HttpServletRequest request,String accessCookieName,String refreshCookieName){
        try {
            Cookie[] cookies = request.getCookies();
            Map<String, String> authorizationTokens = new HashMap<>();
            for(Cookie c:cookies){
                String cName = c.getName();
                if(cName.equals(accessCookieName)||cName.equals(refreshCookieName)){
                    authorizationTokens.put(cName, c.getValue());
                }
            }
            return authorizationTokens;
        }catch (Exception e){
            log.error("쿠키에서 인증토큰 발견 실패");
            throw new JWTVerificationException(Text.wrongAccessTokenOrRefreshTokenNum);
        }
    }

    /**
     * 아무데서나 HttpServletResponse가 필요하면 호출
     * @return
     */
    public static HttpServletResponse getHttpResponse(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getResponse();
    }

    /**
     * 시큐리티 세션에서 로그인 정보 가져오는 함수
     * @return
     */
    public static MemberEntity getLoginInfo(){
        try {
            PrincipalDetails principalDetails= (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return principalDetails.getMemberEntity();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("인증정보를 찾을 수 없습니다");
        }
    }

    /**
     * db 가격에서 ,제거하는 함수
     * @param price
     * @return
     */
    public static int getPriceToComma(String price){
        return Integer.parseInt(price.replace(",", ""));
    }

    /**
     * kg이니시스 결제 성공시 결제 내역 가져오는 함수
     * @param values
     * @return
     */
    public static  LinkedHashMap<String, Object> setPaymentInfo(String[] values){
        LinkedHashMap<String, Object> paymentInfo = new LinkedHashMap<>();
        for (String value : values) {
            if(value.contains("=")){
                String[] val = value.split("=");
                try {
                    paymentInfo.put(val[0], val[1]);
                }catch (ArrayIndexOutOfBoundsException e){
                    paymentInfo.put(val[0], null);
                }
            }
        }
        return paymentInfo;
    }

    public static String[] getKgValues(String P_REQ_URL){
        GetMethod method = new GetMethod(P_REQ_URL);
        HttpClient client = new HttpClient();
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));
        try {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                log.info("Method failed: {}", method.getStatusLine());

            }

// -------------------- 승인결과 수신 -------------------------------------------------

            byte[] responseBody = method.getResponseBody();
            String[] values = new String(responseBody).split("&");
            return values;

        } catch (Exception e) {
            log.info("Fatal protocol violation: {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("결제 성공 후 내역 추출중 에러가 발생했습니다");
        }finally {
            method.releaseConnection();
        }
    }
}
