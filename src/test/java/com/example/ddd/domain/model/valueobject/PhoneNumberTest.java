package com.example.ddd.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PhoneNumber 值对象单元测试
 *
 * @author DDD Demo
 */
@DisplayName("PhoneNumber 值对象测试")
class PhoneNumberTest {

    @Test
    @DisplayName("创建有效手机号应该成功")
    void testCreateValidPhoneNumber() {
        // Given
        String phone = "13800138000";

        // When
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertNotNull(result);
        assertEquals(phone, result.getValue());
    }

    @Test
    @DisplayName("创建 null 手机号应该抛出异常")
    void testCreateNullPhoneNumber() {
        // Given
        String phone = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PhoneNumber.of(phone)
        );
        assertTrue(exception.getMessage().contains("手机号不能为空"));
    }

    @Test
    @DisplayName("创建空手机号应该抛出异常")
    void testCreateEmptyPhoneNumber() {
        // Given
        String phone = "";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PhoneNumber.of(phone)
        );
        assertTrue(exception.getMessage().contains("手机号不能为空"));
    }

    @Test
    @DisplayName("创建错误格式的手机号应该抛出异常")
    void testCreateInvalidPhoneNumber() {
        // Given
        String phone = "12345678901";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PhoneNumber.of(phone)
        );
        assertTrue(exception.getMessage().contains("手机号格式不正确"));
    }

    @Test
    @DisplayName("创建12位手机号应该抛出异常")
    void testCreateTooLongPhoneNumber() {
        // Given
        String phone = "138001380001";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PhoneNumber.of(phone)
        );
        assertTrue(exception.getMessage().contains("手机号格式不正确"));
    }

    @Test
    @DisplayName("创建10位手机号应该抛出异常")
    void testCreateTooShortPhoneNumber() {
        // Given
        String phone = "1380013800";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PhoneNumber.of(phone)
        );
        assertTrue(exception.getMessage().contains("手机号格式不正确"));
    }

    @Test
    @DisplayName("创建以2开头的手机号应该抛出异常")
    void testCreatePhoneNumberStartingWith2() {
        // Given
        String phone = "22800138000";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PhoneNumber.of(phone)
        );
        assertTrue(exception.getMessage().contains("手机号格式不正确"));
    }

    @Test
    @DisplayName("相同手机号的 equals 应该返回 true")
    void testEqualsSamePhoneNumber() {
        // Given
        String phone = "13800138000";
        PhoneNumber phone1 = PhoneNumber.of(phone);
        PhoneNumber phone2 = PhoneNumber.of(phone);

        // Then
        assertEquals(phone1, phone2);
        assertEquals(phone1.hashCode(), phone2.hashCode());
    }

    @Test
    @DisplayName("不同手机号的 equals 应该返回 false")
    void testEqualsDifferentPhoneNumber() {
        // Given
        PhoneNumber phone1 = PhoneNumber.of("13800138000");
        PhoneNumber phone2 = PhoneNumber.of("13900139000");

        // Then
        assertNotEquals(phone1, phone2);
    }

    @Test
    @DisplayName("toString 应该返回手机号字符串")
    void testToString() {
        // Given
        String phone = "13800138000";
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertEquals(phone, result.toString());
    }

    @Test
    @DisplayName("创建带空格的手机号应该自动去除空格")
    void testCreatePhoneNumberWithSpaces() {
        // Given
        String phone = "  13800138000  ";

        // When
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertEquals("13800138000", result.getValue());
    }

    @Test
    @DisplayName("ofNullable 对 null 应该返回 null")
    void testOfNullableWithNull() {
        // Given
        String phone = null;

        // When
        PhoneNumber result = PhoneNumber.ofNullable(phone);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("ofNullable 对空字符串应该返回 null")
    void testOfNullableWithEmpty() {
        // Given
        String phone = "";

        // When
        PhoneNumber result = PhoneNumber.ofNullable(phone);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("ofNullable 对有效手机号应该返回 PhoneNumber 对象")
    void testOfNullableWithValidPhoneNumber() {
        // Given
        String phone = "13800138000";

        // When
        PhoneNumber result = PhoneNumber.ofNullable(phone);

        // Then
        assertNotNull(result);
        assertEquals(phone, result.getValue());
    }

    @Test
    @DisplayName("sameValueAs 对相同值应该返回 true")
    void testSameValueAs() {
        // Given
        String phone = "13800138000";
        PhoneNumber phone1 = PhoneNumber.of(phone);
        PhoneNumber phone2 = PhoneNumber.of(phone);

        // Then
        assertTrue(phone1.sameValueAs(phone2));
    }

    @Test
    @DisplayName("sameValueAs 对不同值应该返回 false")
    void testSameValueAsDifferent() {
        // Given
        PhoneNumber phone1 = PhoneNumber.of("13800138000");
        PhoneNumber phone2 = PhoneNumber.of("13900139000");

        // Then
        assertFalse(phone1.sameValueAs(phone2));
    }

    @Test
    @DisplayName("创建13开头的手机号应该成功")
    void testCreatePhoneNumberStartingWith13() {
        // Given
        String phone = "13800138000";

        // When
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertNotNull(result);
        assertEquals(phone, result.getValue());
    }

    @Test
    @DisplayName("创建14开头的手机号应该成功")
    void testCreatePhoneNumberStartingWith14() {
        // Given
        String phone = "14800148000";

        // When
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertNotNull(result);
        assertEquals(phone, result.getValue());
    }

    @Test
    @DisplayName("创建15开头的手机号应该成功")
    void testCreatePhoneNumberStartingWith15() {
        // Given
        String phone = "15800158000";

        // When
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertNotNull(result);
        assertEquals(phone, result.getValue());
    }

    @Test
    @DisplayName("创建16开头的手机号应该成功")
    void testCreatePhoneNumberStartingWith16() {
        // Given
        String phone = "16800168000";

        // When
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertNotNull(result);
        assertEquals(phone, result.getValue());
    }

    @Test
    @DisplayName("创建17开头的手机号应该成功")
    void testCreatePhoneNumberStartingWith17() {
        // Given
        String phone = "17800178000";

        // When
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertNotNull(result);
        assertEquals(phone, result.getValue());
    }

    @Test
    @DisplayName("创建18开头的手机号应该成功")
    void testCreatePhoneNumberStartingWith18() {
        // Given
        String phone = "18800188000";

        // When
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertNotNull(result);
        assertEquals(phone, result.getValue());
    }

    @Test
    @DisplayName("创建19开头的手机号应该成功")
    void testCreatePhoneNumberStartingWith19() {
        // Given
        String phone = "19800198000";

        // When
        PhoneNumber result = PhoneNumber.of(phone);

        // Then
        assertNotNull(result);
        assertEquals(phone, result.getValue());
    }
}
