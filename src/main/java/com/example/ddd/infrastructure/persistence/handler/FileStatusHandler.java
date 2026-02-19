package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.FileStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * FileStatus 枚举的 MyBatis TypeHandler
 * 负责 FileStatus 与 TINYINT 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes(FileStatus.class)
public class FileStatusHandler extends BaseTypeHandler<FileStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, FileStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public FileStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        return convertToFileStatus(value, rs.wasNull());
    }

    @Override
    public FileStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer value = rs.getInt(columnIndex);
        return convertToFileStatus(value, rs.wasNull());
    }

    @Override
    public FileStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer value = cs.getInt(columnIndex);
        return convertToFileStatus(value, cs.wasNull());
    }

    private FileStatus convertToFileStatus(Integer value, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        return FileStatus.fromValue(value);
    }
}
