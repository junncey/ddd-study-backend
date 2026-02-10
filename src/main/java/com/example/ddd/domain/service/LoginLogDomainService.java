package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.LoginLog;
import com.example.ddd.domain.repository.LoginLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录日志领域服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogDomainService extends DomainService {

    private final LoginLogRepository loginLogRepository;

    /**
     * 记录登录日志
     *
     * @param loginLog 登录日志
     */
    public void recordLoginLog(LoginLog loginLog) {
        log.debug("记录登录日志: {}", loginLog.getUsername());
        loginLogRepository.save(loginLog);
    }

    /**
     * 记录登录成功日志
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param loginIp 登录IP
     * @param browser 浏览器
     * @param os 操作系统
     */
    public void recordLoginSuccess(Long userId, String username, String loginIp, String browser, String os) {
        LoginLog loginLog = LoginLog.builder()
                .userId(userId)
                .username(username)
                .loginIp(loginIp)
                .browser(browser)
                .os(os)
                .status(1)
                .message("登录成功")
                .build();

        recordLoginLog(loginLog);
    }

    /**
     * 记录登录失败日志
     *
     * @param username 用户名
     * @param loginIp 登录IP
     * @param message 失败原因
     */
    public void recordLoginFailure(String username, String loginIp, String message) {
        LoginLog loginLog = LoginLog.builder()
                .username(username)
                .loginIp(loginIp)
                .status(0)
                .message(message)
                .build();

        recordLoginLog(loginLog);
    }
}
