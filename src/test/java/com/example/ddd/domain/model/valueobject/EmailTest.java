package com.example.ddd.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Email 值对象单元测试
 *
 * @author DDD Demo
 */
@DisplayName("Email 值对象测试")
class EmailTest {

    @Test
    @DisplayName("创建有效邮箱应该成功")
    void testCreateValidEmail() {
        // Given
        String email = "test@example.com";

        // When
        Email result = Email.of(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getValue());
    }

    @Test
    @DisplayName("创建 null 邮箱应该抛出异常")
    void testCreateNullEmail() {
        // Given
        String email = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of(email)
        );
        assertTrue(exception.getMessage().contains("邮箱不能为空"));
    }

    @Test
    @DisplayName("创建空邮箱应该抛出异常")
    void testCreateEmptyEmail() {
        // Given
        String email = "";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of(email)
        );
        assertTrue(exception.getMessage().contains("邮箱不能为空"));
    }

    @Test
    @DisplayName("创建无效格式的邮箱应该抛出异常")
    void testCreateInvalidEmail() {
        // Given
        String email = "invalid-email";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of(email)
        );
        assertTrue(exception.getMessage().contains("邮箱格式不正确"));
    }

    @Test
    @DisplayName("创建无@符号的邮箱应该抛出异常")
    void testCreateEmailWithoutAt() {
        // Given
        String email = "example.com";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of(email)
        );
        assertTrue(exception.getMessage().contains("邮箱格式不正确"));
    }

    @Test
    @DisplayName("相同邮箱的 equals 应该返回 true")
    void testEqualsSameEmail() {
        // Given
        String email = "test@example.com";
        Email email1 = Email.of(email);
        Email email2 = Email.of(email);

        // Then
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    @DisplayName("不同邮箱的 equals 应该返回 false")
    void testEqualsDifferentEmail() {
        // Given
        Email email1 = Email.of("test1@example.com");
        Email email2 = Email.of("test2@example.com");

        // Then
        assertNotEquals(email1, email2);
    }

    @Test
    @DisplayName("toString 应该返回邮箱字符串")
    void testToString() {
        // Given
        String email = "test@example.com";
        Email result = Email.of(email);

        // Then
        assertEquals(email, result.toString());
    }

    @Test
    @DisplayName("创建带空格的邮箱应该自动去除空格")
    void testCreateEmailWithSpaces() {
        // Given
        String email = "  test@example.com  ";

        // When
        Email result = Email.of(email);

        // Then
        assertEquals("test@example.com", result.getValue());
    }

    @Test
    @DisplayName("ofNullable 对 null 应该返回 null")
    void testOfNullableWithNull() {
        // Given
        String email = null;

        // When
        Email result = Email.ofNullable(email);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("ofNullable 对空字符串应该返回 null")
    void testOfNullableWithEmpty() {
        // Given
        String email = "";

        // When
        Email result = Email.ofNullable(email);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("ofNullable 对有效邮箱应该返回 Email 对象")
    void testOfNullableWithValidEmail() {
        // Given
        String email = "test@example.com";

        // When
        Email result = Email.ofNullable(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getValue());
    }

    @Test
    @DisplayName("sameValueAs 对相同值应该返回 true")
    void testSameValueAs() {
        // Given
        String email = "test@example.com";
        Email email1 = Email.of(email);
        Email email2 = Email.of(email);

        // Then
        assertTrue(email1.sameValueAs(email2));
    }

    @Test
    @DisplayName("sameValueAs 对不同值应该返回 false")
    void testSameValueAsDifferent() {
        // Given
        Email email1 = Email.of("test1@example.com");
        Email email2 = Email.of("test2@example.com");

        // Then
        assertFalse(email1.sameValueAs(email2));
    }
}
