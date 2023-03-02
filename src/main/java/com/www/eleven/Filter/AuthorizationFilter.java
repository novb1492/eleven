package com.www.eleven.Filter;

import com.www.eleven.Filter.Service.AuthorizationService;
import com.www.eleven.Member.Model.MemberEntity;
import com.www.eleven.Member.Model.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {
    private AuthorizationService authorizationService;

    public AuthorizationFilter(AuthenticationManager authenticationManager, AuthorizationService authorizationService) {
        super(authenticationManager);
        this.authorizationService = authorizationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String uri = request.getRequestURI();
        log.info("인증필터 입장 요청 uri:{} ",  uri);
        /*
            /login /api/auth는 인증 정보가 없어도 접근가능한
            api로 분류
         */
        if(!uri.startsWith("/login")&&!uri.startsWith("/api/auth/")){
//            authorizationService.pro(request);
            PrincipalDetails principalDetails=new PrincipalDetails(MemberEntity.builder().id(1L).build());
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principalDetails,null,principalDetails.getAuthorities()));
        }
        log.info("인증필터 통과");
        chain.doFilter(request, response);
    }
}
