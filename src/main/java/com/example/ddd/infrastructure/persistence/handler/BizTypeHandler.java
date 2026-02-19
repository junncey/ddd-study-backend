package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.BizType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * BizType 枚举的 MyBatis TypeHandler
 * 负责 BizType 与 VARCHAR 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(BizType.class)
public class BizTypeHandler extends BaseTypeHandler<BizType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BizType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public BizType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToBizType(value, rs.wasNull());
    }

    @Override
    public BizType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToBizType(value, rs.wasNull());
    }

    @Override
    public BizType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToBizType(value, cs.wasNull());
    }

    private BizType convertToBizType(String value, boolean wasNull) {
        if (wasNull || value == null) {
            return null;
        }
        return BizType.fromCode(value);
    }
}
