package com.stableforever.examples.encryption.entity;

import com.stableforever.mybatis.encryption.alias.EncryptedString;
import lombok.Data;

/**
 * 加密的数据
 * @author colin
 * @version 0.1
 */
@Data
public class EncryptedData {
    private int id;
    private EncryptedString encryptedData;
}
