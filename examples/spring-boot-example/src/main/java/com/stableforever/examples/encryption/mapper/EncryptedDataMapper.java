package com.stableforever.examples.encryption.mapper;

import com.stableforever.examples.encryption.entity.EncryptedData;
import com.stableforever.mybatis.encryption.alias.EncryptedString;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface EncryptedDataMapper {
    /**
     * 按Id读取数据
     *
     * @param id
     * @return
     */
    @Select("SELECT id, encrypted_data FROM encrypted WHERE id = #{id} LIMIT 1")
    EncryptedData getEncrypted(@Param("id") int id);

    /**
     * 保存数据
     *
     * @param data
     */
    @Insert("INSERT INTO encrypted(encrypted_data) VALUES(#{encryptedData})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveEncrypted(EncryptedData data);

    /**
     * 查询数据
     *
     * @param content
     * @return
     */
    @Select("SELECT id, encrypted_data FROM encrypted WHERE encrypted_data LIKE CONCAT(#{item}, '%') ORDER BY id DESC LIMIT 1")
    EncryptedData queryData(@Param("item") EncryptedString content);
}
