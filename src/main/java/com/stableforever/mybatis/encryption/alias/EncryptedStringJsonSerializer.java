package com.stableforever.mybatis.encryption.alias;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 加密字符串的json序列化
 * 不太建议返回此字符串，如果实在要返回
 *
 * @author colin
 * @version 0.1
 */
public class EncryptedStringJsonSerializer extends JsonSerializer<EncryptedString> {
    private static final String NO_JSON_OUTPUT_PROP_NAME = "mybatis.column-encryption.no-json-serialization";
    private static final String TRUE = "true";
    /**
     * 是否拒绝输出到json
     */
    private final boolean skipOutputToJson;

    public EncryptedStringJsonSerializer() {
        String prop = System.getProperty(NO_JSON_OUTPUT_PROP_NAME, "false");
        skipOutputToJson = TRUE.equalsIgnoreCase(prop);
    }

    @Override
    public void serialize(EncryptedString value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 如果不允许输出到JSON，则输出*
        if (skipOutputToJson) {
            gen.writeString("******");
            return;
        }
        gen.writeString(value.toString());
    }
}
