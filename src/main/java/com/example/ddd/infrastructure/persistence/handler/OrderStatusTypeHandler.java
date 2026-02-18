package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.OrderStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * OrderStatus 枚举的 MyBatis TypeHandler
 * 负责 OrderStatus 与 TINYINT 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(OrderStatus.class)
public class OrderStatusTypeHandler extends BaseTypeHandler<OrderStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, OrderStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public OrderStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        return convertToOrderStatus(value, rs.wasNull());
    }

    @Override
    public OrderStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer value = rs.getInt(columnIndex);
        return convertToOrderStatus(value, rs.wasNull());
    }

    @Override
    public OrderStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer value = cs.getInt(columnIndex);
        return convertToOrderStatus(value, cs.wasNull());
    }

    private OrderStatus convertToOrderStatus(Integer value, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        return OrderStatus.fromValue(value);
    }
}
