package com.www.eleven.Filter.Service;

import com.auth0.jwt.exceptions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.www.eleven.Common.Text;
import com.www.eleven.Common.UtilService;
import com.www.eleven.Jwt.JwtService;
import com.www.eleven.Member.Model.MemberEntity;
import com.www.eleven.Member.Model.PrincipalDetails;
import com.www.eleven.Member.Repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    @Value("${access.token.cookie.name}")
    private String accessTokenCookieName;
    @Value("${refresh.token.cookie.name}")
    private String refreshTokenCookieName;
    @Value("${accesstoken.second}")
    private Integer accessTokenSecond;

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtService jwtService;
    private final MemberRepo memberRepo;


    /**
     * 인증 필터 처리 함수
     *
     * @param request
     */
    public void pro(HttpServletRequest request) {
        Map<String, String> tokens = UtilService.getAuthorizationTokenAtCookie(request, accessTokenCookieName, refreshTokenCookieName);
        String accessToken = getAccessToken(tokens);
        log.info("검증 엑세스토큰:{}", accessToken);
        String memberId = getMemberId(accessToken);
        confirmMemberId(memberId);
        log.info("redis 조회 아이디:{}", memberId);
        MemberEntity memberEntity = getLoginInfoAtRedis(memberId);
        log.info("로그인 정보:{}", memberEntity);
        PrincipalDetails principalDetails=new PrincipalDetails(memberEntity);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principalDetails,null,principalDetails.getAuthorities()));
    }

    /**
     * redis에서 해당 id로 로그인 정보 조회
     * 만약 만료되어서 없다면 db에서 가져와서
     * redis에 저장 및 객체 리턴
     *
     * @param memberId
     * @return
     */
    @Transactional(readOnly = true)
    public MemberEntity getLoginInfoAtRedis(String memberId) {
        Map<Object, Object> loginInfo = redisTemplate.opsForHash().entries(memberId + Text.redisLoginKey);
        if (loginInfo.isEmpty()) {
            log.info("redis에서 만료되었으므로 유저정보 다시 조회"+Text.logLine);
             /*
                db에서 유저정보 조회해서 다시 redis에 넣기
             */
            MemberEntity memberEntity = memberRepo.findById(Long.parseLong(memberId))
                    .orElseThrow(() -> new IllegalArgumentException(Text.cantFindInDBNum));
            redisTemplate.opsForHash().put(memberId + Text.redisLoginKey, Text.redisLoginKey, new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(memberEntity, LinkedHashMap.class));
            redisTemplate.expire(memberId + Text.redisLoginKey, accessTokenSecond, TimeUnit.SECONDS);
            return memberEntity;
        }
        LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) loginInfo.get(Text.redisLoginKey);
        return new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(linkedHashMap,MemberEntity.class);
    }

    /**
     * memberId 가 null인지 검사 하는 함수
     *
     * @param memberId
     */
    public void confirmMemberId(String memberId) {
        UtilService.writeRequestLog(memberId, "멤버 아이디값 검사", "confirmMemberId");
        if (memberId == null) {
            throw new JWTVerificationException(Text.wrongAccessTokenOrRefreshTokenNum);
        }
    }

    /**
     * accessToken 안 id 값 가져오는 함수
     * 키가 id로 된 값이 없을 경우 null 반환
     *
     * @param accessToken
     * @return
     */
    public String getMemberId(String accessToken) {
        UtilService.writeRequestLog(accessToken, "토큰에서 id추출", "getMemberId");
        try {
            return jwtService.openAccessToken(accessToken);
        } catch (TokenExpiredException e) {
            log.error("만료 토큰 요청:{}", accessToken);
            throw new TokenExpiredException(Text.tokenExpireNum);
        } catch (JWTDecodeException | AlgorithmMismatchException | SignatureVerificationException e) {
            log.error("잘못된 토큰 요청:{}", accessToken);
            throw new JWTDecodeException(Text.wrongAccessTokenOrRefreshTokenNum);
        }
    }

    /**
     * accesstoken 가져오는 함수
     *
     * @param tokens
     * @return
     */
    public String getAccessToken(Map<String, String> tokens) {
        UtilService.writeRequestLog(tokens, "accesstoken 값 오픈", "getAccessToken");
        return Optional.ofNullable(tokens.get(accessTokenCookieName)).orElseThrow(() -> new NullPointerException(Text.cantFindAccessTokenOrRefreshTokenNum));
    }

}
