package com.example.ddd.infrastructure.persistence.handler;

import com.example.ddd.domain.model.valueobject.LoginStatus;
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
 * LoginStatus 值对象的 MyBatis TypeHandler
 * 负责 Status<LoginStatus> 与 TINYINT 之间的转换
 *
 * @author DDD Demo
 */
@MappedTypes({Status.class})
public class LoginStatusTypeHandler extends BaseTypeHandler<Status<LoginStatus>> {

    private static final Logger log = LoggerFactory.getLogger(LoginStatusTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Status<LoginStatus> parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public Status<LoginStatus> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return convertToStatus(value, columnName);
    }

    @Override
    public Status<LoginStatus> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return convertToStatus(value, String.valueOf(columnIndex));
    }

    @Override
    public Status<LoginStatus> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer value = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return convertToStatus(value, String.valueOf(columnIndex));
    }

    /**
     * 将整数转换为 Status<LoginStatus>
     * 如果转换失败，记录警告日志并返回 null
     *
     * @param value 整数值
     * @param source 数据来源（用于日志）
     * @return Status<LoginStatus> 对象，如果转换失败则返回 null
     */
    private Status<LoginStatus> convertToStatus(Integer value, String source) {
        if (value == null) {
            return null;
        }
        try {
            return Status.ofLogin(value);
        } catch (IllegalArgumentException e) {
            log.warn("数据库中的登录状态值不正确 [source={}, value={}]: {}", source, value, e.getMessage());
            return null;
        }
    }
}
