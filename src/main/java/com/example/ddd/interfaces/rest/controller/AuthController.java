package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.application.service.AuthApplicationService;
import com.example.ddd.application.service.CaptchaApplicationService;
import com.example.ddd.domain.model.entity.User;
import com.example.ddd.domain.model.valueobject.Email;
import com.example.ddd.domain.model.valueobject.PhoneNumber;
import com.example.ddd.domain.model.valueobject.Status;
import com.example.ddd.domain.model.valueobject.UserStatus;
import com.example.ddd.infrastructure.security.UserDetailsImpl;
import com.example.ddd.infrastructure.util.IpUtil;
import com.example.ddd.interfaces.rest.dto.auth.LoginRequest;
import com.example.ddd.interfaces.rest.dto.auth.CaptchaResponse;
import com.example.ddd.interfaces.rest.dto.auth.LoginResponse;
import com.example.ddd.interfaces.rest.dto.auth.RefreshTokenRequest;
import com.example.ddd.interfaces.rest.dto.auth.RegisterRequest;
import com.example.ddd.interfaces.rest.vo.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 *
 * @author DDD Demo
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaApplicationService captchaApplicationService;

    /**
     * 获取验证码
     *
     * @return 验证码响应
     */
    @GetMapping("/captcha")
    public Response<CaptchaResponse> getCaptcha() {
        log.debug("生成验证码");

        CaptchaResponse response = captchaApplicationService.generateCaptcha();

        return Response.success(response);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @param httpRequest HTTP 请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                          HttpServletRequest httpRequest) {
        log.info("用户登录请求: {}", request.getUsername());

        // 获取客户端信息
        String loginIp = IpUtil.getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        // 强制验证验证码
        if (request.getCaptchaKey() == null || request.getCaptchaKey().isBlank()) {
            return Response.validationError("请先获取验证码");
        }
        if (request.getCaptchaCode() == null || request.getCaptchaCode().isBlank()) {
            return Response.validationError("请输入验证码");
        }
        if (!captchaApplicationService.verifyCaptcha(request.getCaptchaKey(), request.getCaptchaCode())) {
            return Response.validationError("验证码错误或已过期");
        }

        LoginResponse response = authApplicationService.login(
                request.getUsername(),
                request.getPassword(),
                loginIp,
                userAgent
        );

        return Response.success(response);
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册成功后的用户信息
     */
    @PostMapping("/register")
    public Response<User> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求: {}", request.getUsername());

        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Response.validationError("两次密码不一致");
        }

        // 强制验证验证码
        if (request.getCaptchaKey() == null || request.getCaptchaKey().isBlank()) {
            return Response.validationError("请先获取验证码");
        }
        if (request.getCaptchaCode() == null || request.getCaptchaCode().isBlank()) {
            return Response.validationError("请输入验证码");
        }
        if (!captchaApplicationService.verifyCaptcha(request.getCaptchaKey(), request.getCaptchaCode())) {
            return Response.validationError("验证码错误或已过期");
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        // 密码加密
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail() != null ? Email.of(request.getEmail()) : null);
        user.setPhone(request.getPhone() != null ? PhoneNumber.of(request.getPhone()) : null);
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus(Status.ofUser(UserStatus.ENABLED));

        // 调用应用服务注册
        User registeredUser = authApplicationService.register(user);

        // 不返回密码
        registeredUser.setPassword(null);

        return Response.success(registeredUser);
    }

    /**
     * 刷新 Token
     *
     * @param request 刷新Token请求
     * @return 新的Token
     */
    @PostMapping("/refresh")
    public Response<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("刷新Token请求");

        LoginResponse response = authApplicationService.refreshToken(request.getRefreshToken());

        return Response.success(response);
    }

    /**
     * 用户登出
     *
     * @param userDetails 当前用户详情
     * @return 成功消息
     */
    @PostMapping("/logout")
    public Response<Void> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            log.info("用户登出: {}", userDetails.getUsername());
            authApplicationService.logout(userDetails.getUsername());
        }
        return Response.success();
    }

    /**
     * 获取当前用户信息
     *
     * @param userDetails 当前用户详情
     * @return 用户信息
     */
    @GetMapping("/current")
    public Response<LoginResponse.UserInfo> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return Response.fail(401, "请先登录");
        }

        log.debug("获取当前用户信息: {}", userDetails.getUsername());

        User user = authApplicationService.getCurrentUser(userDetails.getUsername());

        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .phone(user != null ? (user.getPhone() != null ? user.getPhone().getValue() : null) : null)
                .nickname(user != null ? user.getNickname() : null)
                .build();

        return Response.success(userInfo);
    }
}
