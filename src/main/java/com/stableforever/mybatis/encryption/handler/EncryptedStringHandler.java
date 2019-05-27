package com.stableforever.mybatis.encryption.handler;

import com.stableforever.mybatis.encryption.StringEncryptorProvider;
import com.stableforever.mybatis.encryption.alias.EncryptedString;
import com.stableforever.mybatis.encryption.spi.StringEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * EncryptedString具体加密处理
 *
 * @author colin
 * @version 0.1
 */
@Slf4j
public class EncryptedStringHandler extends BaseTypeHandler<EncryptedString> {
    final StringEncryptor encryptor;
    public EncryptedStringHandler() {
        this.encryptor = StringEncryptorProvider.getEncryptor();
        log.info("Encryptor is {}", this.encryptor == null ? null : this.encryptor.getClass().getCanonicalName());
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EncryptedString parameter, JdbcType jdbcType) throws SQLException {
        StringEncryptor encryptor = this.encryptor;
        if (null == encryptor) {
            log.warn("Encryptor for encrypting string was not found");
            ps.setString(i, parameter.getValue());
        } else {
            ps.setString(i, encryptor.encrypt(parameter.getValue()));
        }
    }

    @Override
    public EncryptedString getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getDecryptedString(rs.getString(columnName));
    }

    @Override
    public EncryptedString getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getDecryptedString(rs.getString(columnIndex));
    }

    @Override
    public EncryptedString getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getDecryptedString(cs.getString(columnIndex));
    }

    private EncryptedString getDecryptedString(String value) {
        if (EncryptedString.isStringEmpty(value)) {
            return null;
        }
        StringEncryptor encryptor = this.encryptor;
        if (null == encryptor) {
            log.warn("Encryptor for encrypting string was not found");
            return new EncryptedString(value);
        } else {
            return new EncryptedString(encryptor.decrypt(value));
        }
    }
}
