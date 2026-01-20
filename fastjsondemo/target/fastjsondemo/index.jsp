<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>FastJSON Demo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .container {
            border: 1px solid #ddd;
            padding: 20px;
            border-radius: 5px;
        }
        textarea {
            width: 100%;
            height: 200px;
            font-family: monospace;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            cursor: pointer;
            margin-top: 10px;
        }
        button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>FastJSON 1.2.24 Demo</h1>
        <p>这是一个存在 autotype 反序列化漏洞的 FastJSON 演示应用</p>
        
        <h2>测试 JSON 解析</h2>
        <form action="json" method="POST">
            <label for="jsonInput">输入 JSON 数据：</label><br>
            <textarea id="jsonInput" name="data" placeholder='{"name":"test","age":20}'></textarea><br>
            <button type="submit">提交</button>
        </form>
        
        <h3>说明</h3>
        <ul>
            <li>FastJSON 版本：1.2.24</li>
            <li>漏洞类型：autotype 反序列化漏洞</li>
            <li>当 JSON 中包含 @type 字段时，FastJSON 会自动反序列化为指定类</li>
        </ul>
    </div>
</body>
</html>


