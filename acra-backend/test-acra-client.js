/**
 * 测试ACRA崩溃报告发送
 * 这个脚本模拟Android ACRA客户端向后端发送崩溃报告
 */

const axios = require('axios');

async function sendCrashReport() {
    // 模拟ACRA发送的崩溃报告数据
    const crashData = {
        // ACRA标准字段
        'REPORT_ID': 'test-report-' + Date.now(),
        'APP_VERSION_CODE': '1',
        'APP_VERSION_NAME': '1.0.0',
        'PACKAGE_NAME': 'com.example.testapp',
        'FILE_PATH': '/data/data/com.example.testapp',
        'PHONE_MODEL': 'TestDevice',
        'ANDROID_VERSION': '11',
        'BUILD': '{"BRAND":"test","DEVICE":"test","MODEL":"TestDevice","BOARD":"testboard","DISPLAY":"test_display","FINGERPRINT":"test_fingerprint","HARDWARE":"test_hw","HOST":"test_host","ID":"test_id","MANUFACTURER":"TestManufacturer","PRODUCT":"test_product","TAGS":"test_tags","TIME":1234567890,"TYPE":"user","USER":"test_user","VERSION.CODENAME":"REL","VERSION.INCREMENTAL":"1","VERSION.RELEASE":"11","VERSION.SDK_INT":30}',
        'BRAND': 'TestBrand',
        'PRODUCT': 'TestProduct',
        'TOTAL_MEM_SIZE': '1073741824',
        'AVAILABLE_MEM_SIZE': '536870912',
        'BUILD_CONFIG': '{}',
        'CUSTOM_DATA': '{}',
        'IS_SILENT': 'false',
        'DEVICE_FEATURES': '{}',
        'ENVIRONMENT': '{}',
        'SETTINGS_GLOBAL': '{}',
        'SETTINGS_SECURE': '{}',
        'SETTINGS_SYSTEM': '{}',
        'SHARED_PREFERENCES': '{}',
        'SIGNAL_STRENGTH': '{}',
        'SYSTEM_LOG': 'D/test: Test log entry',
        'THREAD_DETAILS': 'main(1): tid=1 systid=1234',
        'USER_APP_START_DATE': '2023-01-01T00:00:00.000Z',
        'USER_CRASH_DATE': '2023-01-01T12:00:00.000Z',
        'USER_EMAIL': '',
        'STACK_TRACE': 'java.lang.RuntimeException: Test crash\n\tat com.example.TestClass.testMethod(TestClass.java:10)\n\tat java.lang.reflect.Method.invoke(Native Method)',
        'APPLICATION_LOG': 'D/Application: Starting application\nE/Application: Unhandled exception occurred'
    };

    try {
        console.log('正在发送崩溃报告到ACRA后端...');
        const response = await axios.post('http://localhost:3456/report', crashData, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'User-Agent': 'ACRA-Android/5.8.1'
            }
        });

        console.log('响应状态:', response.status);
        console.log('响应数据:', response.data);
        console.log('崩溃报告已成功发送！');
    } catch (error) {
        console.error('发送崩溃报告时出错:', error.message);
        if (error.response) {
            console.error('响应数据:', error.response.data);
            console.error('响应状态:', error.response.status);
        }
    }
}

// 执行测试
sendCrashReport();