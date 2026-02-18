package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.ProductStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ProductStatus 枚举的 MyBatis TypeHandler
 * 负责 ProductStatus 与 TINYINT 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(ProductStatus.class)
public class ProductStatusTypeHandler extends BaseTypeHandler<ProductStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ProductStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public ProductStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        return convertToProductStatus(value, rs.wasNull());
    }

    @Override
    public ProductStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer value = rs.getInt(columnIndex);
        return convertToProductStatus(value, rs.wasNull());
    }

    @Override
    public ProductStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer value = cs.getInt(columnIndex);
        return convertToProductStatus(value, cs.wasNull());
    }

    private ProductStatus convertToProductStatus(Integer value, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        return ProductStatus.fromValue(value);
    }
}
