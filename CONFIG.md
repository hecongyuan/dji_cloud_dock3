# 配置说明文档

本文档说明如何配置项目中的敏感信息。

## ⚠️ 安全提示

- **切勿将包含真实密码和密钥的配置文件提交到 Git 仓库**
- 项目已配置 `.gitignore` 忽略敏感配置文件
- 使用 `.example` 示例文件作为配置模板

## 📝 配置步骤

### 1. 后端配置

#### 复制配置文件

```bash
cd backend/sample/src/main/resources
cp application.yml.example application.yml
```

#### 修改配置项

编辑 `application.yml` 文件，替换以下占位符：

##### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cloud_sample
    username: root
    password: your_password  # 替换为你的 MySQL 密码
```

##### Redis 配置

```yaml
  redis:
    host: localhost
    port: 6379
    # password: your_redis_password  # 如果 Redis 启用了密码认证
```

##### MQTT 配置

```yaml
mqtt:
  BASIC:
    host: localhost
    port: 1883
    username: your_mqtt_username  # 替换为你的 MQTT 用户名
    password: your_mqtt_password  # 替换为你的 MQTT 密码
    client-id: your_client_id     # 替换为唯一的客户端 ID
  DRC:
    host: localhost
    port: 8083
    username: your_mqtt_username  # 替换为你的 MQTT 用户名
    password: your_mqtt_password  # 替换为你的 MQTT 密码
```

##### 对象存储配置（MinIO）

```yaml
oss:
  enable: true
  provider: minio
  endpoint: http://localhost:9000
  access-key: your_access_key  # 替换为你的 MinIO Access Key
  secret-key: your_secret_key  # 替换为你的 MinIO Secret Key
  bucket: dji-cloud
```

##### DJI Cloud API 凭证

```yaml
cloud-api:
  app:
    id: your_app_id          # 替换为你的 DJI App ID
    key: your_app_key        # 替换为你的 DJI App Key
    license: your_app_license # 替换为你的 DJI App License
```

> 💡 在 [DJI 开发者平台](https://developer.dji.com/user/apps/#all) 创建应用获取凭证

##### 直播服务配置

```yaml
livestream:
  url:
    gb28181:
      serverIP: localhost
      agentPassword: your_password  # 替换为 GB28181 密码
    rtsp:
      username: dji-cloud-api
      password: your_password       # 替换为 RTSP 密码
```

### 2. 前端配置

#### 复制配置文件

```bash
cd frontend/src/api/http
cp config.ts.example config.ts
```

#### 修改配置项

编辑 `config.ts` 文件，替换以下占位符：

##### DJI Cloud API 凭证

```typescript
export const CURRENT_CONFIG = {
  appId: 'your_app_id',          // 替换为你的 DJI App ID
  appKey: 'your_app_key',        // 替换为你的 DJI App Key
  appLicense: 'your_app_license', // 替换为你的 DJI App License
```

##### 后端服务地址

```typescript
  baseURL: 'http://localhost:6789/',
  websocketURL: 'ws://localhost:6789/api/v1/ws',
```

如果后端部署在其他服务器，修改为实际地址：

```typescript
  baseURL: 'http://your-server-ip:6789/',
  websocketURL: 'ws://your-server-ip:6789/api/v1/ws',
```

##### 直播服务配置

```typescript
  rtmpURL: 'rtmp://localhost:1935/rtp/',
  gbServerIp: 'localhost',
  gbPassword: 'your_password',      // 替换为 GB28181 密码
  rtspPassword: 'your_password',    // 替换为 RTSP 密码
```

##### 高德地图 Key

```typescript
  amapKey: 'your_amap_key',  // 在高德开放平台申请
```

> 💡 在 [高德开放平台](https://lbs.amap.com/) 申请 Web 端 Key

### 3. 数据库初始化

执行数据库初始化脚本前，建议修改默认用户密码：

```sql
-- 修改 cloud_sample_init.sql 中的用户密码
INSERT INTO `manage_user` (...) VALUES (..., 'admin', 'your_strong_password', ...);
```

## 🔐 安全建议

### 开发环境

1. 使用强密码，避免使用简单密码如 `123456`、`admin` 等
2. 定期更换密码和密钥
3. 不要在代码注释中包含真实密码

### 生产环境

1. **数据库安全**：
   - 使用强密码
   - 限制数据库访问 IP
   - 启用 SSL 连接

2. **MQTT 安全**：
   - 启用 TLS/SSL 加密
   - 使用强密码认证
   - 配置访问控制列表（ACL）

3. **对象存储安全**：
   - 使用临时凭证（STS）
   - 配置 Bucket 访问策略
   - 启用 HTTPS

4. **API 安全**：
   - 配置 HTTPS 证书
   - 启用防火墙规则
   - 使用反向代理（Nginx）

5. **密钥管理**：
   - 使用环境变量存储敏感信息
   - 考虑使用密钥管理服务（如 HashiCorp Vault）
   - 定期轮换密钥

## 📋 配置检查清单

部署前请确认：

- [ ] 已修改所有默认密码
- [ ] DJI App 凭证已正确配置
- [ ] 数据库连接信息正确
- [ ] Redis 连接信息正确
- [ ] MQTT Broker 连接信息正确
- [ ] 对象存储配置正确
- [ ] 直播服务配置正确
- [ ] 前后端地址配置一致
- [ ] 敏感配置文件未提交到 Git

## 🆘 常见问题

### Q: 如何获取 DJI App 凭证？

A: 访问 [DJI 开发者平台](https://developer.dji.com/user/apps/#all)，注册并创建应用，即可获得 App ID、App Key 和 License。

### Q: 配置文件被误提交到 Git 怎么办？

A: 
1. 立即修改所有泄露的密码和密钥
2. 使用 `git filter-branch` 或 `BFG Repo-Cleaner` 从历史记录中删除敏感文件
3. 强制推送到远程仓库

### Q: 如何使用环境变量配置？

A: Spring Boot 支持通过环境变量覆盖配置：

```bash
export SPRING_DATASOURCE_PASSWORD=your_password
export CLOUD_API_APP_ID=your_app_id
export CLOUD_API_APP_KEY=your_app_key
```

### Q: 生产环境如何管理配置？

A: 建议使用：
- Spring Cloud Config Server
- Kubernetes ConfigMap/Secret
- Docker Secrets
- 环境变量

## 📞 技术支持

如有配置问题，请在项目 Issues 中提问，但请勿在 Issue 中包含真实的密码和密钥。
