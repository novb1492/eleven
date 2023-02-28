package com.www.eleven.Member.Controller;

import com.www.eleven.Api.Kakao.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증이 필요없는 요청
 */
@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
public class MemberAuthController {
    private final  KakaoLoginService kakaoLoginService;

    @RequestMapping(value = "/kakao/{code}/login" ,method = RequestMethod.POST)
    public ResponseEntity<?>kakaoLoginPro(@PathVariable String code){
        kakaoLoginService.kLoginPro(code);
        return ResponseEntity.ok().body(null);
    }
}
