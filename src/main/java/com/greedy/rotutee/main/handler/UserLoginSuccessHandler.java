package com.greedy.rotutee.main.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * packageName : com.greedy.rotutee.main.service
 * fileName : UserLoginSuccessHandler
 * author : 7sang
 * date : 2022-05-12
 * description :
 * ===========================================================
 * DATE AUTHOR NOTE
 * -----------------------------------------------------------
 * 2022-05-12 7sang 최초 생성
 */

@Component
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        System.out.println("request = " + request.getRequestURI());
        System.out.println("이전 " + request.getHeader("REFERER"));

        response.sendRedirect(request.getHeader("REFERER"));
    }
}
