package com.minjor.auth.service;

import com.minjor.auth.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // 检查是否存在用户
//        if (userRepository.count() == 0) {
//            createUserIfNotExists();
//        }
    }

    private void createUserIfNotExists() {
        String defaultUsername = "datam";
        String defaultPassword = generateRandomPassword();

//        // 检查用户是否已存在
//        if (userRepository.findByUsername(defaultUsername).isEmpty()) {
//            User defaultUser = new User();
//            defaultUser.setUsername(defaultUsername);
//            defaultUser.setPassword(passwordEncoder.encode(defaultPassword));
//            defaultUser.setEmail("datam@example.com");
//            defaultUser.setFullName("Data Management User");
//            defaultUser.setStatus(DataStatus.ACTIVE);
//
//            userRepository.save(defaultUser);
//
//            System.out.println("=================================");
//            System.out.println("Initial user created:");
//            System.out.println("Username: " + defaultUsername);
//            System.out.println("Password: " + defaultPassword);
//            System.out.println("Please change password after first login!");
//            System.out.println("=================================");
//        }
    }

    private String generateRandomPassword() {
        // 生成随机密码
        return "Datam@" + System.currentTimeMillis();
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        // 查询用户
        User user = new User();
        user.setUsername("datam");
        user.setPassword("$2a$10$sfK9Xwm3v26rT3WSg3qkw.2YNEr.uaw.HvQgvXhKjH9NwdwYd9RU2");

        // 构建Spring Security用户对象
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities("ADMIN")
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .build();
    }
}
