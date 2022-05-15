package com.greedy.rotutee.config;

import com.greedy.rotutee.Authentication.service.AuthenticationService;
import com.greedy.rotutee.main.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;
import java.util.Map;

@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationService authenticationService;
    private final WebAccessDeniedHandler webAccessDeniedHandler;
    private final UserLoginSuccessHandler userLoginSuccessHandler;
    private final UserLogoutSuccessHandler userLogoutSuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final UserLoginFailHandler userLoginFailHandler;

    @Autowired
    public SpringSecurityConfiguration(AuthenticationService authenticationService, WebAccessDeniedHandler webAccessDeniedHandler,
                                       UserLoginSuccessHandler userLoginSuccessHandler, UserLogoutSuccessHandler userLogoutSuccessHandler,
                                       CustomAuthenticationEntryPoint customAuthenticationEntryPoint, UserLoginFailHandler userLoginFailHandler) {
        this.authenticationService = authenticationService;
        this.webAccessDeniedHandler = webAccessDeniedHandler;
        this.userLoginSuccessHandler = userLoginSuccessHandler;
        this.userLogoutSuccessHandler = userLogoutSuccessHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.userLoginFailHandler = userLoginFailHandler;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "images/**", "video/**");   // 이녀석들한테는 인증을 요구하지 않겠다
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        Map<String, List<String>> permitListMap = authenticationService.getPermitListMap();
        List<String> adminPermitList = permitListMap.get("adminPermitList");
        List<String> subAdminPermitList = permitListMap.get("subAdminPermitList");
        List<String> tuteePermitList = permitListMap.get("tuteePermitList");
        List<String> tutorPermitList = permitListMap.get("tutorPermitList");
        List<String> memberPermitList = permitListMap.get("memberPermitList");


        http.csrf().disable()
                .authorizeHttpRequests()
                .antMatchers(subAdminPermitList.toArray(new String[subAdminPermitList.size()])).hasAnyRole("SUBADMIN", "ADMIN")
                .antMatchers(tuteePermitList.toArray(new String[tuteePermitList.size()])).hasAnyRole("TUTEE", "SUBADMIN", "ADMIN")
                .antMatchers(tutorPermitList.toArray(new String[tutorPermitList.size()])).hasAnyRole("TUTOR", "SUBADMIN", "ADMIN")
                .antMatchers(memberPermitList.toArray(new String[memberPermitList.size()])).hasAnyRole("MEMBER", "TUTEE", "TUTOR", "SUBADMIN", "ADMIN")
                .antMatchers(adminPermitList.toArray(new String[adminPermitList.size()])).hasRole("ADMIN")
                .anyRequest().permitAll()  // 모든 요청에 접근 허용을 하겠다
                .and()
                .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .formLogin()  // form 이용한 로그인을 하겠다 활성화
                .loginPage("/member/login")// 이 url 로그인 form을 쓰겠다
                .successForwardUrl("/")  // 성공시 이곳으로 가겠다
//                .successHandler(userLoginSuccessHandler)
                .failureHandler(userLoginFailHandler)
//                .failureForwardUrl("/error/login")  // 실패시 이곳으로 가겠다
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))  // 이 요청이 들어오면 로그아웃을 하겠다
                .deleteCookies("JSESSIONID")  // 성공하게 되면 JSESSIONID 라는 쿠키를 삭제 시킬 것
                .invalidateHttpSession(true)  // 세션정보를 무효화시키겠다
                .logoutSuccessUrl("/")  // 성공시 이곳으로 가겠다
                .and()
                .exceptionHandling().accessDeniedHandler(webAccessDeniedHandler);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /* 인증을 할 때 이 클래스를 사용하겠다 */
        auth.userDetailsService(authenticationService).passwordEncoder(passwordEncoder());
    }


}
