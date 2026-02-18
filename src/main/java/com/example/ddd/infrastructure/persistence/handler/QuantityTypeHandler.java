package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.Quantity;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Quantity 值对象的 MyBatis TypeHandler
 * 负责 Quantity 与 INTEGER 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(Quantity.class)
public class QuantityTypeHandler extends BaseTypeHandler<Quantity> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Quantity parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public Quantity getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        return convertToQuantity(value, rs.wasNull());
    }

    @Override
    public Quantity getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer value = rs.getInt(columnIndex);
        return convertToQuantity(value, rs.wasNull());
    }

    @Override
    public Quantity getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer value = cs.getInt(columnIndex);
        return convertToQuantity(value, cs.wasNull());
    }

    private Quantity convertToQuantity(Integer value, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        return Quantity.of(value);
    }
}
