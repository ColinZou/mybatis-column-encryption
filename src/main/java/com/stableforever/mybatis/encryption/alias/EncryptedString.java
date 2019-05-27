package com.stableforever.mybatis.encryption.alias;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * 加密字符串
 *
 * @author colin
 * @version 0.1
 */
@Alias("EncryptedString")
@JsonSerialize(using = EncryptedStringJsonSerializer.class)
public class EncryptedString implements Serializable {
    /**
     * 字符串
     */
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EncryptedString(String value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return isStringEmpty(this.value);
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return null;
        }
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EncryptedString)) {
            return false;
        }
        EncryptedString other = (EncryptedString) obj;
        if (this.value == null || other.getValue() == null) {
            return false;
        }
        return this.value.equals(other.getValue());
    }

    public static boolean isStringEmpty(String input) {
        return null == input || input.length() == 0;
    }
}
