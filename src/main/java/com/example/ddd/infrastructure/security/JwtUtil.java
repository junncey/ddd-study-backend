package com.example.ddd.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 工具类
 *
 * @author DDD Demo
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT 密钥
     */
    @Value("${jwt.secret:}")
    private String secret;

    /**
     * Token 有效期（毫秒）默认 7 天
     */
    @Value("${jwt.expiration:604800000}")
    private Long expiration;

    /**
     * Refresh Token 有效期（毫秒）默认 30 天
     */
    @Value("${jwt.refresh-expiration:2592000000}")
    private Long refreshExpiration;

    // 已知的开发/测试用默认密钥（用于检测不安全配置）
    private static final String[] KNOWN_INSECURE_SECRETS = {
            "devSecretKey", "testSecretKey", "defaultSecret", "changeMe",
            "pleaseReplace", "ForTesting", "development", "example"
    };

    /**
     * 初始化时验证JWT密钥配置
     */
    @PostConstruct
    public void init() {
        // 检查密钥是否配置
        if (secret == null || secret.isBlank()) {
            log.error("JWT密钥未配置！请设置环境变量 JWT_SECRET 或配置 jwt.secret");
            throw new IllegalStateException("JWT密钥未配置，请设置环境变量 JWT_SECRET");
        }

        // 检查密钥长度
        if (secret.length() < 64) {
            log.warn("JWT密钥长度不足64字符（当前{}字符），建议使用更长的密钥", secret.length());
        }

        // 检查是否使用已知的不安全密钥
        String lowerSecret = secret.toLowerCase();
        for (String insecure : KNOWN_INSECURE_SECRETS) {
            if (lowerSecret.contains(insecure.toLowerCase())) {
                log.warn("JWT密钥包含不安全的关键词: {}，建议更换为随机生成的密钥", insecure);
                log.warn("生成密钥命令: openssl rand -base64 64");
                break;
            }
        }

        log.info("JWT工具类初始化完成");
    }

    /**
     * 生成密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从 token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从 token 中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从 token 中获取指定声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从 token 中获取所有声明
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查 token 是否过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 为用户生成 token
     *
     * @param username 用户名
     * @return JWT token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, expiration);
    }

    /**
     * 为用户生成 token（带额外声明）
     *
     * @param claims 额外声明
     * @param username 用户名
     * @return JWT token
     */
    public String generateToken(Map<String, Object> claims, String username) {
        return createToken(claims, username, expiration);
    }

    /**
     * 生成 refresh token
     *
     * @param username 用户名
     * @return refresh token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, username, refreshExpiration);
    }

    /**
     * 创建 token
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 验证 token
     *
     * @param token JWT token
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 验证 token（不检查用户名）
     *
     * @param token JWT token
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 刷新 token
     *
     * @param token 旧的 token
     * @return 新的 token
     */
    public String refreshToken(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return createToken(new HashMap<>(claims), claims.getSubject(), expiration);
        } catch (Exception e) {
            log.error("Token 刷新失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 token 剩余有效时间（秒）
     */
    public Long getTokenRemainingTime(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return remaining > 0 ? remaining / 1000 : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
