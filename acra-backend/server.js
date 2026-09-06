const express = require('express');
const fs = require('fs-extra');
const path = require('path');
const bodyParser = require('body-parser');
const multer = require('multer');

const app = express();
const PORT = process.env.PORT || 3456;

// 确保崩溃报告目录存在
const CRASH_REPORT_DIR = './crash-reports';
fs.ensureDirSync(CRASH_REPORT_DIR);

// 配置multer以处理表单数据（ACRA通常使用表单方式提交）
// 使用中间件解析表单数据
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

// 处理ACRA崩溃报告的路由
app.post('/report', async (req, res) => {
    try {
        console.log('收到ACRA崩溃报告');
        
        // 获取当前时间戳用于生成唯一文件名
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
        const fileName = `crash-${timestamp}.json`;
        const filePath = path.join(CRASH_REPORT_DIR, fileName);
        
        // 准备要保存的数据
        const crashReport = {
            receivedAt: new Date().toISOString(),
            userAgent: req.headers['user-agent'] || null,
            body: req.body,
            headers: {
                'content-type': req.headers['content-type'],
                'content-length': req.headers['content-length']
            }
        };
        
        // 将崩溃报告写入文件
        await fs.writeJson(filePath, crashReport, { spaces: 2 });
        
        console.log(`崩溃报告已保存至: ${filePath}`);
        
        // 返回成功响应
        res.status(200).json({ 
            success: true, 
            message: '崩溃报告已成功接收并保存',
            fileName: fileName
        });
    } catch (error) {
        console.error('处理崩溃报告时出错:', error);
        res.status(500).json({ 
            success: false, 
            error: '保存崩溃报告失败' 
        });
    }
});

// 健康检查端点
app.get('/health', (req, res) => {
    res.status(200).json({ status: 'OK', timestamp: new Date().toISOString() });
});

// 提供查看已保存崩溃报告的接口
/*
app.get('/reports', async (req, res) => {
    try {
        const files = await fs.readdir(CRASH_REPORT_DIR);
        const reports = files
            .filter(file => file.startsWith('crash-') && file.endsWith('.json'))
            .map(file => ({
                fileName: file,
                fullPath: path.join(CRASH_REPORT_DIR, file),
                createdAt: fs.statSync(path.join(CRASH_REPORT_DIR, file)).mtime
            }))
            .sort((a, b) => b.createdAt - a.createdAt); // 按时间倒序排列
        
        res.status(200).json({ reports });
    } catch (error) {
        console.error('获取崩溃报告列表时出错:', error);
        res.status(500).json({ error: '获取崩溃报告列表失败' });
    }
});
*/

// 提供下载特定崩溃报告的接口
/*
app.get('/report/:fileName', async (req, res) => {
    try {
        const fileName = req.params.fileName;
        const filePath = path.join(CRASH_REPORT_DIR, fileName);
        
        // 验证文件名是否有效（防止路径遍历攻击）
        if (!fileName.match(/^crash-.*\.json$/)) {
            return res.status(400).json({ error: '无效的文件名' });
        }
        
        // 检查文件是否存在
        if (!(await fs.pathExists(filePath))) {
            return res.status(404).json({ error: '文件不存在' });
        }
        
        res.sendFile(filePath);
    } catch (error) {
        console.error('获取崩溃报告时出错:', error);
        res.status(500).json({ error: '获取崩溃报告失败' });
    }
});
*/

// 启动服务器
app.listen(PORT, () => {
    console.log(`ACRA崩溃报告后端服务正在运行在端口 ${PORT}`);
    console.log(`崩溃报告将保存在: ${path.resolve(CRASH_REPORT_DIR)}`);
    console.log('API端点:');
    console.log('  POST /report - 接收崩溃报告');
    console.log('  GET  /health - 健康检查');
    console.log('  GET  /reports - 列出所有崩溃报告');
    console.log('  GET  /report/:fileName - 下载特定崩溃报告');
});