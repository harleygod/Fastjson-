package com.govuln.fastjsondemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FastJSON 1.2.24 漏洞演示 Servlet
 * 
 * ⚠️ 危险：此代码仅用于安全研究和漏洞复现
 * 生产环境请升级 FastJSON 版本或关闭 autotype
 */
public class FastJSONServlet extends HttpServlet {
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // 注意：FastJSON 1.2.24 默认 autotype 是开启的
        // 在 1.2.25+ 版本中才默认关闭，需要手动开启
        
        // ⚠️ 危险：允许 JNDI 从远程加载类（JDK 8u191+ 默认禁止）
        // 仅用于漏洞复现，生产环境不应设置
        // 这是 JdbcRowSetImpl JNDI 注入成功的关键配置
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        
        System.out.println("[FastJSON] trustURLCodebase 已设置为 true");
        System.out.println("[FastJSON] autotype 默认开启（FastJSON 1.2.24）");
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>FastJSON Servlet</h1>");
        out.println("<p>请使用 POST 方法提交 JSON 数据</p>");
        out.println("<p>示例：{\"name\":\"test\",\"age\":20}</p>");
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        try {
            String jsonData = req.getParameter("data");
            if (jsonData == null || jsonData.trim().isEmpty()) {
                out.println("<h2>错误：未提供 JSON 数据</h2>");
                return;
            }
            
            out.println("<h2>接收到的 JSON 数据：</h2>");
            out.println("<pre>" + jsonData + "</pre>");
            
            // ⚠️ 危险：直接使用 parseObject，会触发 autotype 反序列化
            // 如果 JSON 中包含 @type 字段，FastJSON 会自动反序列化为指定类
            // Feature.SupportNonPublicField: 支持设置私有字段（TemplatesImpl 需要）
            // 注意：FastJSON 1.2.24 默认 autotype 是开启的
            Object obj = JSON.parseObject(jsonData, Feature.SupportNonPublicField);
            
            out.println("<h2>解析结果：</h2>");
            out.println("<pre>" + obj.toString() + "</pre>");
            out.println("<p>解析成功！</p>");
            
        } catch (Exception e) {
            out.println("<h2>错误：</h2>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
}

