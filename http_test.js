// loadtest.js - 完整的HTTP压测工具
const http = require('http');
const https = require('https');
const url = require('url');
const querystring = require('querystring');

class LoadTester {
    constructor(options = {}) {
        this.url = options.url || 'http://localhost:3000';
        this.method = options.method || 'GET';
        this.concurrency = options.concurrency || 10;
        this.duration = options.duration || 10; // 秒
        this.headers = options.headers || {};
        this.body = options.body || null;
        this.expectedStatus = options.expectedStatus || [200];

        // 统计
        this.totalRequests = 0;
        this.successful = 0;
        this.failed = 0;
        this.latencies = [];
        this.statusCodes = {};
        this.startTime = null;
        this.stopFlag = false;
    }

    makeRequest() {
        return new Promise((resolve) => {
            const start = Date.now();
            const parsedUrl = url.parse(this.url);
            const isHttps = parsedUrl.protocol === 'https:';

            const options = {
                hostname: parsedUrl.hostname,
                port: parsedUrl.port || (isHttps ? 443 : 80),
                path: parsedUrl.path,
                method: this.method,
                headers: this.headers,
                timeout: 10000
            };

            const req = (isHttps ? https : http).request(options, (res) => {
                const latency = Date.now() - start;
                const statusCode = res.statusCode;

                // 读取响应体（确保请求完成）
                res.on('data', () => {});
                res.on('end', () => {
                    const success = this.expectedStatus.includes(statusCode);
                    resolve({ latency, success, statusCode });
                });
            });

            req.on('error', (error) => {
                const latency = Date.now() - start;
                resolve({ latency, success: false, statusCode: 0 });
            });

            req.on('timeout', () => {
                req.destroy();
                const latency = Date.now() - start;
                resolve({ latency, success: false, statusCode: 0 });
            });

            // 发送请求体
            if (this.body) {
                const bodyStr = typeof this.body === 'object'
                    ? JSON.stringify(this.body)
                    : this.body;
                req.write(bodyStr);
            }

            req.end();
        });
    }

    async worker() {
        while (!this.stopFlag) {
            const result = await this.makeRequest();

            // 更新统计（线程安全）
            this.totalRequests++;
            this.latencies.push(result.latency);

            if (result.success) {
                this.successful++;
            } else {
                this.failed++;
            }

            const status = result.statusCode;
            this.statusCodes[status] = (this.statusCodes[status] || 0) + 1;
        }
    }

    async start() {
        console.log('\n\x1b[36mHTTP压测工具\x1b[0m');
        console.log('='.repeat(50));
        console.log(`目标URL: ${this.url}`);
        console.log(`请求方法: ${this.method}`);
        console.log(`并发数: ${this.concurrency}`);
        console.log(`持续时间: ${this.duration}秒`);

        if (Object.keys(this.headers).length > 0) {
            console.log('请求头:');
            Object.entries(this.headers).forEach(([key, value]) => {
                console.log(`  ${key}: ${value}`);
            });
        }

        if (this.body) {
            console.log(`请求体: ${JSON.stringify(this.body).substring(0, 100)}${JSON.stringify(this.body).length > 100 ? '...' : ''}`);
        }

        console.log('='.repeat(50));
        console.log('\n开始压测...\n');

        this.startTime = Date.now();

        // 启动所有worker
        const workers = [];
        for (let i = 0; i < this.concurrency; i++) {
            workers.push(this.worker());
        }

        // 定时停止
        setTimeout(() => {
            this.stopFlag = true;

            // 等待所有worker完成
            setTimeout(() => {
                this.printResults();
                process.exit(0);
            }, 1000);

        }, this.duration * 1000);
    }

    printResults() {
        const actualDuration = (Date.now() - this.startTime) / 1000;
        const qps = this.totalRequests / actualDuration;
        const successRate = (this.successful / this.totalRequests) * 100;

        // 计算延迟统计
        const sortedLatencies = this.latencies.sort((a, b) => a - b);
        const avgLatency = sortedLatencies.reduce((a, b) => a + b, 0) / sortedLatencies.length;
        const p95 = sortedLatencies[Math.floor(sortedLatencies.length * 0.95)];
        const p99 = sortedLatencies[Math.floor(sortedLatencies.length * 0.99)];

        console.log('\n\x1b[32m' + '='.repeat(50) + '\x1b[0m');
        console.log('\x1b[33m压测结果:\x1b[0m');
        console.log(`总请求数: ${this.totalRequests}`);
        console.log(`成功请求: ${this.successful}`);
        console.log(`失败请求: ${this.failed}`);
        console.log(`成功率: ${successRate.toFixed(2)}%`);
        console.log(`持续时间: ${actualDuration.toFixed(2)}秒`);
        console.log(`\x1b[36mQPS: ${qps.toFixed(2)}\x1b[0m`);
        console.log(`平均响应时间: ${avgLatency.toFixed(2)}ms`);
        console.log(`P95响应时间: ${p95 ? p95.toFixed(2) : 'N/A'}ms`);
        console.log(`P99响应时间: ${p99 ? p99.toFixed(2) : 'N/A'}ms`);

        console.log('\n状态码分布:');
        Object.entries(this.statusCodes)
            .sort(([a], [b]) => a - b)
            .forEach(([code, count]) => {
                const percentage = (count / this.totalRequests) * 100;
                const color = code >= 200 && code < 300 ? '\x1b[32m' :
                            code >= 400 && code < 500 ? '\x1b[33m' :
                            code >= 500 ? '\x1b[31m' : '\x1b[0m';
                console.log(`${color}  ${code}: ${count}次 (${percentage.toFixed(1)}%)\x1b[0m`);
            });

        console.log('\x1b[32m' + '='.repeat(50) + '\x1b[0m');
    }
}

// 使用示例
const tester = new LoadTester({
    url: 'http://localhost:8080/auth/login',
    method: 'POST',
    concurrency: 500,
    duration: 10,
    headers: {
        'Content-Type': 'application/json',
        'User-Agent': 'LoadTest/1.0'
    },
    body: {
        username: 'datam',
        password: 'Datam@1769232030316'
    },
    expectedStatus: [200, 201]
});
//const tester = new LoadTester({
//    url: 'http://localhost:8080/template/app/withResultJson',
//    method: 'GET',
//    concurrency: 50,
//    duration: 10,
//    headers: {
//        'Content-Type': 'application/json',
//        'User-Agent': 'LoadTest/1.0',
//        'authorization': 'Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkYXRhbSIsImlhdCI6MTc2OTIzNTEzMywiZXhwIjoxNzY5MjM4NzMzLCJhdXRob3JpdGllcyI6IiIsInRva2VuX3R5cGUiOiJhY2Nlc3MifQ.RDBW8aDBq7ZKe1FwvDaAPd-WNlvX-OOqpFrrbRAfI3aVTqmZn7r4UGKCEqgK-t96-3WUhCwLcxtX6tr5A4mLLQ'
//    },
//    expectedStatus: [200, 201]
//});

tester.start();