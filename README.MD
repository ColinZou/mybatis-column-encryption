# 基于mybatis的数据库加密

## 一、说明

在某些涉及到数据安全的场景（如客户数据）,为了防止脱库或者少数需要做数据安全防范的场景。需要针对数据做加密，但通常整库加密的时候直接使用数据库自带的加密即可。

本代码库基于MyBatis的数据库列加密库。现行很多解决方案要么是基于整库实施， 要么就是基于没有办法实现加密以后的LIKE查询。通常情况下，普通业务最多只需要对部分字段进行加密即可。因此，开发了这个库。

### 设计思路

基于MyBatis的Alias与TypeHandler。其中，com.stableforever.mybatis.encryption.alias.EncryptedString为用于HOLD加密数据的类型Alias。com.stableforever.mybatis.encryption.handler.EncryptedStringHandler为具体的TypeHandler，用于处理数据库查询的入参时，自动把未加密的字符串转换为加密格式；同时，从数据库查询结果当中读取数据时自动把数据解密。

## 二、使用说明

### 1. 添加Maven仓库地址

在pom.xml当中添加如下仓库地址

```xml
<repositories>
    <repository>
        <id>oss.sonatype.org</id>
        <name>Sona Type OSS</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>
</repositories>
```

### 2. 引入依赖

```xml
<dependency>
    <groupId>com.stableforever</groupId>
    <artifactId>mybatis-column-data-encryption</artifactId>
    <version>0.0.3</version>
</dependency>
```
如果此版本使用有问题（可能MAVEN仓库还在同步当中），请暂时使用版本号0.0.1。
### 3. 在数据模型当中使用EncryptedString作为要加密的字段类型

示例如下：

```java
@Data
public class EncryptedData {
    private int id;
    private EncryptedString encryptedData;
}
```

### 4. 实现加密方法

可以参数examples/spring-boot-example/src/main/java/com/stableforever/examples/encryption/AesStringEncryptor.java。

**特别注意**：如果你需要对加密的数据做LIKE查询，在加密时，必须像样例一样针对对字符逐一加密。

### 5. 暴露加密实现

创建以下纯文本文件：src/main/resources/META-INF/services/com.stableforever.mybatis.encryption.spi.StringEncryptor

文件内容为#4创建的加密类类名。

### 6. 打完收工

**注意**： 当你需要对加密过后的使用LIKE查询时，查询参数必须使用EncryptedString。且%需要使用数据库内置的一些方法来追加，如下：

```java
@Select("SELECT id, encrypted_data FROM encrypted WHERE encrypted_data LIKE CONCAT(#{item}, '%') ORDER BY id DESC LIMIT 1")
    EncryptedData queryData(@Param("item") EncryptedString content);
```

## 三、关于样例的说明

样例目录：examples/spring-boot-example/

样例使用的数据库：mariadb(mysql同样适用)

数据库脚本：examples/spring-boot-example/src/main/resources/testdb.sql

如果你有更多的alias或者handler，根据mybatis的说明，你的spring boot配置当中可以使用逗号分隔，像这样写：xxxx.xxx,xxxx.xxxx

