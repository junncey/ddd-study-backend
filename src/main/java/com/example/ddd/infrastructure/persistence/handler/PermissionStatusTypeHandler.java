package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.PermissionStatus;
import com.example.ddd.domain.model.valueobject.Status;
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
 * PermissionStatus 值对象的 MyBatis TypeHandler
 * 负责 Status<PermissionStatus> 与 TINYINT 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes({Status.class})
public class PermissionStatusTypeHandler extends BaseTypeHandler<Status<PermissionStatus>> {

    private static final Logger log = LoggerFactory.getLogger(PermissionStatusTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Status<PermissionStatus> parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public Status<PermissionStatus> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return convertToStatus(value, columnName);
    }

    @Override
    public Status<PermissionStatus> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return convertToStatus(value, String.valueOf(columnIndex));
    }

    @Override
    public Status<PermissionStatus> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer value = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return convertToStatus(value, String.valueOf(columnIndex));
    }

    /**
     * 将整数转换为 Status<PermissionStatus>
     * 如果转换失败，记录警告日志并返回 null
     *
     * @param value 整数值
     * @param source 数据来源（用于日志）
     * @return Status<PermissionStatus> 对象，如果转换失败则返回 null
     */
    private Status<PermissionStatus> convertToStatus(Integer value, String source) {
        if (value == null) {
            return null;
        }
        try {
            return Status.ofPermission(value);
        } catch (IllegalArgumentException e) {
            log.warn("数据库中的权限状态值不正确 [source={}, value={}]: {}", source, value, e.getMessage());
            return null;
        }
    }
}
