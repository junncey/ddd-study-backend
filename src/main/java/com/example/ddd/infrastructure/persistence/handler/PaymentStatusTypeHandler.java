package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.PaymentStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PaymentStatus 枚举的 MyBatis TypeHandler
 * 负责 PaymentStatus 与 TINYINT 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(PaymentStatus.class)
public class PaymentStatusTypeHandler extends BaseTypeHandler<PaymentStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PaymentStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public PaymentStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        return convertToPaymentStatus(value, rs.wasNull());
    }

    @Override
    public PaymentStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer value = rs.getInt(columnIndex);
        return convertToPaymentStatus(value, rs.wasNull());
    }

    @Override
    public PaymentStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer value = cs.getInt(columnIndex);
        return convertToPaymentStatus(value, cs.wasNull());
    }

    private PaymentStatus convertToPaymentStatus(Integer value, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        return PaymentStatus.fromValue(value);
    }
}
