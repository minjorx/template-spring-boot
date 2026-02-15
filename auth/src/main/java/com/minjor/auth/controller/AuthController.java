package com.minjor.auth.controller;

import com.minjor.auth.req.LoginReq;
import com.minjor.auth.resp.JwtResp;
import com.minjor.auth.service.AuthUserDetailsService;
import com.minjor.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public JwtResp authenticate(@RequestBody LoginReq loginReq) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginReq.getUsername(),
                            loginReq.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("错误: 用户名或密码不正确");
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginReq.getUsername());

        final String accessToken = jwtService.generateAccessToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new JwtResp(accessToken, refreshToken);
    }

    @PostMapping("/refresh")
    public JwtResp refreshToken(@RequestBody JwtResp jwtResp) {
        String refreshToken = jwtResp.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.validateRefreshToken(refreshToken, userDetails)) {
            String newAccessToken = jwtService.refreshToken(refreshToken, userDetails);
            // 通常refresh token不变，除非有特殊安全策略
            // 如果需要轮换refresh token，取消下面的注释
            // String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            return new JwtResp(newAccessToken, refreshToken);
        } else {
            throw new RuntimeException("无效的刷新令牌");
        }
    }

    @PostMapping("/logout")
    public String logout() {
        // TODO 需要redis 存储黑名单
        return "注销成功";
    }
}
