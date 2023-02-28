package com.www.eleven.Jwt;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("잘못된 형식토큰 테스트")
    public void test(){
        assertThrows(JWTDecodeException.class, () -> jwtService.openToken("fdsdf"));
    }
    @Test
    @DisplayName("일반 토큰발급")
    public void test2(){
        String fakeAccessToken = jwtService.getToken("fdsdf", 100000, null);
        assertEquals(jwtService.openAccessToken(fakeAccessToken),null);
    }
    @Test
    @DisplayName("인증토큰발급")
    public void test3(){
        String accessToken = jwtService.getAccessToken("1");
        System.out.println(accessToken);
        assertNotEquals(accessToken,null);
    }
    @Test
    @DisplayName("인증토큰열기")
    public void test4(){
        String accessToken = jwtService.openAccessToken(jwtService.getAccessToken("1"));
        assertNotEquals(accessToken,null);
    }


}