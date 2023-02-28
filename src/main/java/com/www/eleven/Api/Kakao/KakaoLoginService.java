package com.www.eleven.Api.Kakao;

import com.www.eleven.Api.Help.Request;
import com.www.eleven.Common.CommonColumn;
import com.www.eleven.Common.Text;
import com.www.eleven.Common.UtilService;
import com.www.eleven.Config.SecurityConfig;
import com.www.eleven.Filter.Service.LoginService;
import com.www.eleven.Jwt.JwtService;
import com.www.eleven.Member.Model.MemberEntity;
import com.www.eleven.Member.Repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${kakao.rest.api.key}")
    private String apiKey;
    @Value("${kakao.login.redirect.url}")
    private String reRrl;
    private final LoginService loginService;

    /**
     * 카카오로그인 처리함수
     * @param code
     */
    public void kLoginPro(String code){
        JSONObject response = getToken(code);
        JSONObject userInfo = getUserInfo(response.get("access_token").toString());
        LinkedHashMap<String, Object> kUserInfo = (LinkedHashMap<String, Object>) userInfo.get("kakao_account");
        String email = kUserInfo.get("email").toString();
        loginService.loginPro(email);
    }

    /**
     * 카카오에 엑세스토큰 요청
     * @param code
     * @return
     */
    public JSONObject getToken(String code){
        HttpHeaders headers = new HttpHeaders(); //토큰얻어오기
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> multiValueBody = new LinkedMultiValueMap<>();
        multiValueBody.add("grant_type", "authorization_code"); //카카오에서  요청하는 고정값
        multiValueBody.add("client_id", apiKey);
        multiValueBody.add("redirect_uri", reRrl);
        multiValueBody.add("code",code);
        JSONObject response = Request.requestPost(multiValueBody, "https://kauth.kakao.com/oauth/token", headers);
        return response;
    }

    /**
     * 받은 엑세스토큰으로 카카오에 해당 유저 정보 요청
     * @param accessToken
     * @return
     */
    public JSONObject getUserInfo(String accessToken){
        HttpHeaders userInfoHttpHeaders= new HttpHeaders();
        userInfoHttpHeaders.add("Authorization", "Bearer "+accessToken);
        return Request.requestGet(null, "https://kapi.kakao.com/v2/user/me", userInfoHttpHeaders).getBody();
    }
}
