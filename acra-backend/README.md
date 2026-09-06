# ACRA崩溃报告后端服务

这是一个使用Node.js实现的后端服务，用于接收Android应用通过ACRA库发送的崩溃报告，并将其保存到本地文件系统中。

## 功能特性

- 接收来自ACRA客户端的崩溃报告
- 将崩溃报告保存为JSON格式文件
- 提供API端点用于查看和下载崩溃报告
- 安全验证以防止路径遍历攻击

## 安装与启动

1. 克隆或复制本项目代码
2. 安装依赖包：
   ```bash
   npm install
   ```
3. 启动服务：
   ```bash
   npm start
   ```

## API端点

- `POST /report` - 接收ACRA崩溃报告（这是ACRA客户端调用的主要端点）
- `GET /health` - 服务健康状态检查
- `GET /reports` - 获取所有崩溃报告的列表
- `GET /report/:fileName` - 下载指定的崩溃报告

## ACRA客户端配置示例

在您的Android项目中，您需要这样配置ACRA（示例）：

```java
@Configuration(
    reportFormat = StringFormat.JSON,
    reportingInteractionMode = ReportingInteractionMode.TOAST,
    sendReportsInDevMode = true,
    formUri = "http://your-server-address:3000/report", // 替换为您的服务器地址
    formUriBasicAuthLogin = "", // 如果需要认证
    formUriBasicAuthPassword = "" // 如果需要认证
)
public class YourApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
```

## 文件存储

崩溃报告将保存在项目根目录下的 `./crash-reports/` 目录中，每个报告都使用时间戳作为文件名（如 `crash-2023-01-01T12-30-45-123Z.json`）。

## 开发模式

在开发过程中，您可以使用以下命令启动服务，它会在代码更改时自动重启：

```bash
npm run dev
```