package com.www.eleven.Filter.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.www.eleven.Common.CommonColumn;
import com.www.eleven.Common.Text;
import com.www.eleven.Common.UtilService;
import com.www.eleven.Config.SecurityConfig;
import com.www.eleven.Jwt.JwtService;
import com.www.eleven.Member.Model.MemberEntity;
import com.www.eleven.Member.Repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginService {

    @Value("${oauth.pwd}")
    private String oauthPwd;
    @Value("${refresh.token.second}")
    private Integer refreshTokenExpireSecond;
    @Value("${access.token.cookie.name}")
    private String accessTokenCookieName;
    @Value("${refresh.token.cookie.name}")
    private String refreshTokenCookieName;
    @Value("${accesstoken.second}")
    private Integer accessTokenSecond;
    private final MemberRepo memberRepo;
    private final SecurityConfig securityConfig;
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 소셜 로그인 뒤 로그인 처리 함수
     * @param email
     */
    @Transactional(rollbackFor = Exception.class)
    public void loginPro(String email){
        UtilService.writeRequestLog(email,"로그인시도","loginPro");
        MemberEntity memberEntity = memberRepo.findByState(Text.trueState,email).orElseGet(() -> null);
        if(memberEntity==null){
            memberEntity = MemberEntity.builder().commonColumn(CommonColumn.set(Text.trueState)).pwd(securityConfig.pwdEncoder().encode(oauthPwd)).userId(email).build();
            memberRepo.save(memberEntity);
        }
        String access=jwtService.getAccessToken(memberEntity.getId().toString());
        String refresh = jwtService.getToken("refresh", 1000 * refreshTokenExpireSecond, null);
        setRedis(memberEntity,access,refresh);
        saveAuthenticationInCookie(access, refresh, UtilService.getHttpResponse());
    }

    /**
     * 토큰정보 redis에 저장
     * @param memberEntity
     * @param accessToken
     * @param refreshToken
     */
    private void setRedis(MemberEntity memberEntity,String accessToken,String refreshToken){
        long memberId = memberEntity.getId();
        redisTemplate.opsForHash().put(memberId + Text.redisLoginKey, Text.redisLoginKey, new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(memberEntity, LinkedHashMap.class));
        redisTemplate.expire(memberId + Text.redisLoginKey, accessTokenSecond, TimeUnit.SECONDS);
        LinkedHashMap<String, Object> refreshTokenDto = new LinkedHashMap<>();
        refreshTokenDto.put("id", memberEntity.getId());
        refreshTokenDto.put("access", accessToken);
        refreshTokenDto.put("created", LocalDateTime.now().toString());
        refreshTokenDto.put("count",0);
        redisTemplate.opsForHash().put(refreshToken, refreshToken, refreshTokenDto);
        redisTemplate.expire(refreshToken,refreshTokenExpireSecond,TimeUnit.SECONDS);
    }

    /**
     * 토큰 쿠키에 저장
     * @param accessToken
     * @param refreshToken
     * @param response
     */
    private void saveAuthenticationInCookie(String accessToken, String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(accessTokenCookieName, accessToken)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
        ResponseCookie cookie2 = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.addHeader("Set-Cookie", cookie2.toString());
    }

    /**
     * 인증 토큰 쿠키 삭제 및 redis삭제
     * @param cookies
     * @param response
     */
    public void deleteAuthCookie(Cookie[] cookies,HttpServletResponse response){
        for(Cookie c:cookies){
            if(c.getName().equals(accessTokenCookieName)||c.getName().equals(refreshTokenCookieName)){
                ResponseCookie cookie2 = ResponseCookie.from(c.getName(), null)
                        .path("/")
                        .secure(true)
                        .sameSite("None")
                        .httpOnly(true)
                        .maxAge(0)
                        .build();
                response.addHeader("Set-Cookie", cookie2.toString());
                if(c.getName().equals(accessTokenCookieName)) {
                    Long userId = UtilService.getLoginInfo().getId();
                    redisTemplate.expire(userId+Text.redisLoginKey,0,TimeUnit.MICROSECONDS);
                }else if(c.getName().equals(refreshTokenCookieName)){
                    redisTemplate.expire(c.getValue(),0,TimeUnit.MICROSECONDS);
                }
            }
        }

    }
}
