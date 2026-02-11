package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.Email;
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
 * Email 值对象的 MyBatis TypeHandler
 * 负责 Email 与 VARCHAR 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(Email.class)
public class EmailTypeHandler extends BaseTypeHandler<Email> {

    private static final Logger log = LoggerFactory.getLogger(EmailTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Email parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public Email getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToEmail(value, columnName);
    }

    @Override
    public Email getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToEmail(value, String.valueOf(columnIndex));
    }

    @Override
    public Email getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToEmail(value, String.valueOf(columnIndex));
    }

    /**
     * 将字符串转换为 Email
     * 如果转换失败，记录警告日志并返回 null
     *
     * @param value 字符串值
     * @param source 数据来源（用于日志）
     * @return Email 对象，如果转换失败则返回 null
     */
    private Email convertToEmail(String value, String source) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Email.of(value);
        } catch (IllegalArgumentException e) {
            log.warn("数据库中的邮箱格式不正确 [source={}, value={}]: {}", source, value, e.getMessage());
            return null;
        }
    }
}
