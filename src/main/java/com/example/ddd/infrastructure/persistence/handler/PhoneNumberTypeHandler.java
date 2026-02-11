package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.PhoneNumber;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PhoneNumber 值对象的 MyBatis TypeHandler
 * 负责 PhoneNumber 与 VARCHAR 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(PhoneNumber.class)
public class PhoneNumberTypeHandler extends BaseTypeHandler<PhoneNumber> {

    private static final Logger log = LoggerFactory.getLogger(PhoneNumberTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PhoneNumber parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public PhoneNumber getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToPhoneNumber(value, columnName);
    }

    @Override
    public PhoneNumber getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToPhoneNumber(value, String.valueOf(columnIndex));
    }

    @Override
    public PhoneNumber getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToPhoneNumber(value, String.valueOf(columnIndex));
    }

    /**
     * 将字符串转换为 PhoneNumber
     * 如果转换失败，记录警告日志并返回 null
     *
     * @param value 字符串值
     * @param source 数据来源（用于日志）
     * @return PhoneNumber 对象，如果转换失败则返回 null
     */
    private PhoneNumber convertToPhoneNumber(String value, String source) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return PhoneNumber.of(value);
        } catch (IllegalArgumentException e) {
            log.warn("数据库中的手机号格式不正确 [source={}, value={}]: {}", source, value, e.getMessage());
            return null;
        }
    }
}
