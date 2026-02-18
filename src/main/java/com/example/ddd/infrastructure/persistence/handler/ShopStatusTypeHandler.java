package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.ShopStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ShopStatus 枚举的 MyBatis TypeHandler
 * 负责 ShopStatus 与 TINYINT 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(ShopStatus.class)
public class ShopStatusTypeHandler extends BaseTypeHandler<ShopStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ShopStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public ShopStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        return convertToShopStatus(value, rs.wasNull());
    }

    @Override
    public ShopStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer value = rs.getInt(columnIndex);
        return convertToShopStatus(value, rs.wasNull());
    }

    @Override
    public ShopStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer value = cs.getInt(columnIndex);
        return convertToShopStatus(value, cs.wasNull());
    }

    private ShopStatus convertToShopStatus(Integer value, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        return ShopStatus.fromValue(value);
    }
}
