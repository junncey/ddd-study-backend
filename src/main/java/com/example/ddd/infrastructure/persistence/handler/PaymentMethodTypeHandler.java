package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.PaymentMethod;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PaymentMethod 枚举的 MyBatis TypeHandler
 * 负责 PaymentMethod 与 TINYINT 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(PaymentMethod.class)
public class PaymentMethodTypeHandler extends BaseTypeHandler<PaymentMethod> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PaymentMethod parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public PaymentMethod getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        return convertToPaymentMethod(value, rs.wasNull());
    }

    @Override
    public PaymentMethod getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer value = rs.getInt(columnIndex);
        return convertToPaymentMethod(value, rs.wasNull());
    }

    @Override
    public PaymentMethod getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer value = cs.getInt(columnIndex);
        return convertToPaymentMethod(value, cs.wasNull());
    }

    private PaymentMethod convertToPaymentMethod(Integer value, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        return PaymentMethod.fromValue(value);
    }
}
