package com.www.eleven.Member.Controller;

import com.www.eleven.Filter.Service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 인증이 필요한 api
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MemberController {
    private final LoginService loginService;

    @RequestMapping(value = "/out/log",method = RequestMethod.POST)
    public ResponseEntity<?>logOut(HttpServletRequest request, HttpServletResponse response){
        loginService.deleteAuthCookie(request.getCookies(),response);
        return ResponseEntity.ok().body(null);
    }
}
