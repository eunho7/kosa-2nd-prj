package com.example._team.config;

import com.example._team.service.UserDetailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    // 작성자 : 신은호, 내용 : 권한에 따른 로그인 다음 페이지 핸들러
    // 사용 방법 : CustomAuthenticationSuccessHandler.java(config 패키지)에 들어가서 url 수정.
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    // 작성자 : 신은호, 내용 : CustomAuthenticationSuccessHandler 객체 생성자
    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            response.sendRedirect("/login?error=true");
        }
    }

    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().requestMatchers("/static/**","/images/**");
    }

    //특정 Http 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/password/reset-request"),  // POST 요청 허용
                                new AntPathRequestMatcher("/password/reset"),  // 비밀번호 재설정 POST 요청 허용
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/signup"),
                                new AntPathRequestMatcher("/user"),
                                new AntPathRequestMatcher("/user/find-id"),
                                new AntPathRequestMatcher("/signup/send-code"),
                                new AntPathRequestMatcher("/signup/verify"),
                                new AntPathRequestMatcher("/signup/complete"),
                                new AntPathRequestMatcher("/api/**"),
                                new AntPathRequestMatcher("/api/travel/create"),
                                new AntPathRequestMatcher("/map/view"),
                                new AntPathRequestMatcher("/map")
//                                new AntPathRequestMatcher("/api/travel/likes/{travelIdx}")
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 작성자 : 신은호, 내용 : ADMIN 권한 추가
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        // 작성자 : 신은호, 내용 : 실제 로그인 후 권한에 따라 다른 페이지 실행
                        // 사용 방법 : CustomAuthenticationSuccessHandler.java(config 패키지)에 들어가서 url 수정.
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(new CustomAuthenticationFailureHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    //인증 관리자 관련  설정
    @Bean
    public AuthenticationManager authenticationManger(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder,
                                                      UserDetailService userService)
        throws Exception{
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
