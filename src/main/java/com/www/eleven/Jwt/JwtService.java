package com.www.eleven.Jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.sign}")
    private String jwtSign;
    @Value("${access.token.cookie.name}")
    private String accessTokenCookieName;
    /*
        개발/테스트 환경은 min아니라 10초
     */
    @Value("${accesstoken.second}")
    private Integer accesstokenSecond;

    public String getAccessToken(String id) {
        return JWT.create().withSubject(accessTokenCookieName).withClaim("id",id).withExpiresAt(new Date(System.currentTimeMillis()+1000*accesstokenSecond)).sign(Algorithm.HMAC512(jwtSign));
    }

    public String openAccessToken(String accessToken) {
        return JWT.require(Algorithm.HMAC512(jwtSign)).build().verify(accessToken).getClaim("id").asString();
    }

    public String getToken(String tokenName, int expireSecond, Map<String,Object>claim) {
        return JWT.create().withSubject(tokenName).withClaim("claim",claim).withExpiresAt(new Date(System.currentTimeMillis()+1000*expireSecond)).sign(Algorithm.HMAC512(jwtSign));
    }
    public Map<String,Object>openToken(String token){
        return JWT.require(Algorithm.HMAC512(jwtSign)).build().verify(token).getClaim("claim").asMap();
    }
}
