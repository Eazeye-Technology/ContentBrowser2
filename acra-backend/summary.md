# ACRA崩溃报告后端服务总结

## 项目概述

本项目是一个使用Node.js实现的Android ACRA崩溃报告后端服务，专门用于接收、存储和管理来自Android应用的崩溃报告。该服务实现了完整的崩溃报告处理流程，包括接收、存储、检索等功能。

## 技术架构

- **后端框架**: Express.js
- **语言**: JavaScript (Node.js)
- **依赖管理**: npm
- **文件系统操作**: fs-extra
- **中间件**: body-parser, multer
- **前端界面**: HTML/CSS/JavaScript

## 核心功能

### 1. 崩溃报告接收
- 提供 `/report` API端点接收来自ACRA客户端的POST请求
- 支持表单数据格式的崩溃报告（ACRA标准格式）
- 自动解析和验证传入的数据

### 2. 数据存储
- 将接收到的崩溃报告保存为JSON格式文件
- 使用时间戳生成唯一的文件名确保不重复
- 存储在 `./crash-reports/` 目录下

### 3. 报告管理
- 提供 `/reports` 端点列出所有已保存的崩溃报告
- 提供 `/report/:fileName` 端点下载特定的崩溃报告
- 提供 `/health` 端点检查服务健康状态

### 4. 安全措施
- 文件名验证防止路径遍历攻击
- 输入数据验证确保数据完整性

### 5. 用户界面
- 提供Web界面用于管理崩溃报告
- 支持查看、下载和管理崩溃报告
- 实时显示服务状态和报告列表

## API端点

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/report` | 接收ACRA崩溃报告 |
| GET | `/health` | 服务健康检查 |
| GET | `/reports` | 获取所有崩溃报告列表 |
| GET | `/report/:fileName` | 下载指定的崩溃报告 |

## 文件结构

```
acra-backend/
├── server.js              # 主服务器文件
├── package.json           # 项目配置和依赖
├── README.md             # 项目说明文档
├── test-acra-client.js   # 测试客户端脚本
├── public/               # 静态资源目录
│   └── index.html        # 管理界面
└── crash-reports/        # 崩溃报告存储目录
    └── crash-*.json      # 单个崩溃报告文件
```

## 部署和使用

### 安装
```bash
npm install
```

### 启动
```bash
npm start
```

### 开发模式
```bash
npm run dev
```

## ACRA客户端配置

在Android项目中配置ACRA以发送崩溃报告到此服务：

```java
@Configuration(
    reportFormat = StringFormat.JSON,
    reportingInteractionMode = ReportingInteractionMode.TOAST,
    sendReportsInDevMode = true,
    formUri = "http://your-server-address:3000/report", // 替换为实际服务器地址
    formUriBasicAuthLogin = "", // 如需认证
    formUriBasicAuthPassword = "" // 如需认证
)
public class YourApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
```

## 测试功能

项目包含测试脚本 `test-acra-client.js`，可用来验证后端服务是否正常工作。该脚本模拟真实的ACRA客户端发送崩溃报告到后端。

## 特色功能

1. **实时监控**: 通过Web界面实时查看和管理崩溃报告
2. **安全存储**: 所有崩溃报告以结构化JSON格式安全存储
3. **易于扩展**: 模块化设计便于添加新功能
4. **跨平台兼容**: 基于Web技术，可在任何平台运行

## 总结

该项目成功实现了完整的ACRA崩溃报告后端解决方案，具有良好的可扩展性、安全性和易用性。通过提供完整的API接口和用户界面，使开发者能够轻松接收、存储和管理Android应用的崩溃报告，从而快速定位和修复问题，提高应用质量。