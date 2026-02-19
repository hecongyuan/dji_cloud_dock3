# DJI Cloud API Sample - Dock 3 适配版

[![Version](https://img.shields.io/badge/version-1.10.0-blue.svg)]()
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.12-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.2.26-brightgreen.svg)](https://vuejs.org/)
[![DJI Dock 3](https://img.shields.io/badge/DJI%20Dock%203-Supported-blue.svg)]()

> ⚠️ **重要说明**: 
> - 这是一个**第三方开发**的 DJI Cloud API 适配项目，**非 DJI 官方项目**
> - 专门针对 **DJI Dock 3（机场3）** 进行了适配和优化
> - 基于 DJI Cloud SDK 1.2.5 开发

第三方开发的 DJI 云平台示例项目，提供了与 DJI Dock 3 机场及配套无人机进行云端交互的完整解决方案。

> 💬 **技术交流 & 商用咨询**: 添加微信（见文末二维码）获取技术支持和商用版本服务

## 📋 目录

- [项目简介](#项目简介)
- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [系统架构](#系统架构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [API 文档](#api-文档)
- [项目结构](#项目结构)
- [开发指南](#开发指南)
- [常见问题](#常见问题)
- [注意事项](#注意事项)
- [相关链接](#相关链接)
- [免责声明](#免责声明)
- [许可证](#许可证)
- [贡献](#贡献)
- [技术支持](#技术支持)
- [交流与商用](#交流与商用)

## 📖 项目简介

本项目是基于 DJI Cloud API 开发的**第三方云平台示例应用**，专门针对 **DJI Dock 3（机场3）** 进行了适配和功能优化。

### 关于本项目

- **项目性质**: 第三方开发，非 DJI 官方项目
- **适配设备**: DJI Dock 3 机场及配套飞行器
- **技术基础**: 基于 DJI Cloud SDK 1.2.5 和 Cloud API 协议
- **开发目的**: 为开发者提供 Dock 3 云平台集成的参考实现

### 支持的设备

- ✅ **DJI Dock 3** - 第三代智能机场
- ✅ **DJI Matrice 3D/3TD** - Dock 3 配套飞行器
- ✅ **DJI RC Plus** - 遥控器（部分功能）

### 与官方 Demo 的区别

本项目在 DJI Cloud API 基础上进行了以下适配：
- 针对 Dock 3 的特性进行了功能优化
- 适配了 Dock 3 的新增设备状态和控制指令
- 优化了 Matrice 3 系列飞行器的数据处理
- 提供了更完整的中文文档和示例

## ✨ 功能特性

### 核心功能（Dock 3 适配）
- 🚁 **Dock 3 设备管理** - 机场、无人机、遥控器的接入与管理
- 📍 **航线任务** - 支持 Dock 3 的航线规划、任务下发、执行监控
- 📹 **实时直播** - 支持 RTMP、RTSP、WebRTC、GB28181 等多种协议
- 📷 **媒体管理** - Matrice 3D/3TD 照片/视频上传、下载、预览
- 🗺️ **地图服务** - 设备定位、航线展示、禁飞区管理
- 📊 **设备监控** - Dock 3 OSD 数据、HMS 健康管理、设备状态监控
- 🔄 **固件升级** - Dock 3 及飞行器固件在线升级
- 📝 **日志管理** - 设备日志上传与分析
- ⚙️ **远程控制** - 云端遥控、相机控制、云台控制

### 高级功能
- 🔐 JWT 身份认证
- 🔌 MQTT 消息通信
- 🌐 WebSocket 实时推送
- 📦 对象存储集成（MinIO/阿里云 OSS/AWS S3）
- 🎯 飞行区域管理
- 🔧 设备配置同步

## 🛠️ 技术栈

### 后端
- **框架**: Spring Boot 2.7.12
- **语言**: Java 11
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **消息队列**: MQTT (Eclipse Paho)
- **ORM**: MyBatis Plus 3.4.2
- **对象存储**: MinIO / 阿里云 OSS / AWS S3
- **API 文档**: Swagger/OpenAPI

### 前端
- **框架**: Vue 3.2.26
- **构建工具**: Vite 2.4.0
- **UI 组件**: Ant Design Vue 2.2.8
- **地图**: 高德地图 (AMap)
- **视频播放**: flv.js, Agora RTC SDK
- **状态管理**: Vuex 4.0.2
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **实时通信**: MQTT.js, WebSocket

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                         前端应用 (Vue 3)                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 设备管理 │  │ 航线任务 │  │ 实时直播 │  │ 媒体管理 │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
           │                    │                    │
           ▼                    ▼                    ▼
┌─────────────────────────────────────────────────────────────┐
│                    后端服务 (Spring Boot)                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ REST API │  │  MQTT    │  │ WebSocket│  │ Cloud SDK│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
           │                    │                    │
           ▼                    ▼                    ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│    MySQL     │  │     Redis    │  │  对象存储     │
└──────────────┘  └──────────────┘  └──────────────┘
           │
           ▼
┌─────────────────────────────────────────────────────────────┐
│                      MQTT Broker                             │
└─────────────────────────────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────────────────────────────┐
│              DJI Dock 3 + Matrice 3D/3TD                     │
└─────────────────────────────────────────────────────────────┘
```

## 💻 环境要求

### 必需环境
- **JDK**: 11 或更高版本
- **Maven**: 3.6+ 
- **Node.js**: 14.0+ 
- **npm/yarn/pnpm**: 任意包管理器

### 依赖服务
- **MySQL**: 8.0+
- **Redis**: 5.0+
- **MQTT Broker**: EMQX/Mosquitto 等
- **对象存储**: MinIO/阿里云 OSS/AWS S3（可选）

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <your-repository-url>
cd Cloud-API-Demo
```

> 💡 **提示**: 请将 `<your-repository-url>` 替换为实际的仓库地址

### 2. 初始化数据库

```bash
# 连接到 MySQL 数据库
mysql -u root -p

# 创建数据库
CREATE DATABASE cloud_sample CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入初始化脚本
mysql -u root -p cloud_sample < cloud_sample_init.sql
```

### 3. 配置后端

> 📖 **详细配置说明请参考 [CONFIG.md](CONFIG.md) 文档**

复制配置文件模板：

```bash
cd backend/sample/src/main/resources
cp application.yml.example application.yml
```

编辑 `application.yml`，修改以下关键配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cloud_sample
    username: root
    password: your_password

  redis:
    host: localhost
    port: 6379

mqtt:
  BASIC:
    host: localhost
    port: 1883
    username: admin
    password: public

oss:
  enable: true
  provider: minio
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket: dji-cloud

cloud-api:
  app:
    id: your_app_id
    key: your_app_key
    license: your_app_license
```

> ⚠️ **重要**: 请在 [DJI 开发者平台](https://developer.dji.com/user/apps/#all) 创建应用并获取 App ID、App Key 和 License

### 4. 启动后端

```bash
cd backend
mvn clean install
cd sample
mvn spring-boot:run
```

后端服务将在 `http://localhost:6789` 启动

### 5. 设备接入配置

#### Dock 3 设备接入

1. 在 Dock 3 设备上配置云平台信息：
   - 平台地址: `http://your-server-ip:6789`
   - MQTT 地址: `mqtt://your-server-ip:1883`
   - App ID: 在配置文件中设置的 `cloud-api.app.id`

2. 确保 Dock 3 能够访问到服务器的以下端口：
   - `6789` - HTTP API 端口
   - `1883` - MQTT 端口
   - `8083` - MQTT WebSocket 端口（DRC 远程控制）

3. 设备上线后，可在前端页面的设备管理中查看设备状态

### 6. 启动前端

复制前端配置文件：

```bash
cd frontend/src/api/http
cp config.ts.example config.ts
```

编辑 `config.ts`，配置后端地址和 DJI 凭证：

```typescript
export const CURRENT_CONFIG = {
  appId: 'your_app_id',
  appKey: 'your_app_key',
  appLicense: 'your_app_license',
  baseURL: 'http://localhost:6789/',
  websocketURL: 'ws://localhost:6789/api/v1/ws',
}
```

安装依赖并启动：

```bash
cd frontend
npm install
npm run serve
```

前端应用将在 `http://localhost:5173` 启动（端口可能不同）

### 7. 访问应用

- **前端应用**: http://localhost:5173
- **后端 API 文档**: http://localhost:6789/swagger-ui/index.html
- **WebSocket 地址**: ws://localhost:6789/api/v1/ws

## ⚙️ 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://localhost:3306/cloud_sample?useSSL=false&allowPublicKeyRetrieval=true
      username: root
      password: your_password
      initial-size: 10
      min-idle: 10
      max-active: 20
```

### MQTT 配置

支持两种 MQTT 连接：

1. **BASIC** - 基础 MQTT 连接（设备通信）
2. **DRC** - 远程控制 MQTT 连接

```yaml
mqtt:
  BASIC:
    protocol: MQTT  # MQTT/WS/WSS
    host: localhost
    port: 1883
    username: admin
    password: public
    client-id: cloud-api-sample
  DRC:
    protocol: WS
    host: localhost
    port: 8083
    path: /mqtt
```

### 对象存储配置

#### MinIO（推荐用于开发）

```yaml
oss:
  enable: true
  provider: minio
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket: dji-cloud
  expire: 3600
```

#### 阿里云 OSS

```yaml
oss:
  enable: true
  provider: ALIYUN
  endpoint: https://oss-cn-hangzhou.aliyuncs.com
  access-key: your_access_key
  secret-key: your_secret_key
  region: cn-hangzhou
  bucket: your_bucket
  role-arn: acs:ram::123456789:role/stsrole
```

#### AWS S3

```yaml
oss:
  enable: true
  provider: aws
  endpoint: https://s3.us-east-1.amazonaws.com
  access-key: your_access_key
  secret-key: your_secret_key
  region: us-east-1
  bucket: your_bucket
```

### 直播配置

支持多种直播协议：

```yaml
livestream:
  url:
    # RTMP 推流
    rtmp:
      url: rtmp://localhost:1935/rtp/
    
    # RTSP 流
    rtsp:
      username: dji-cloud-api
      password: 123456
      port: 8554
    
    # WebRTC (WHIP)
    whip:
      url: http://localhost:8088/rtc/v1/whip/?app=live&stream=
    
    # GB28181
    gb28181:
      serverIP: localhost
      serverPort: 8116
      serverID: 41010500002000000001
```

## 📚 API 文档

### Swagger UI

启动后端服务后，访问：http://localhost:6789/swagger-ui/index.html

### 主要 API 端点

| 模块 | 端点 | 说明 |
|------|------|------|
| 设备管理 | `/manage/api/v1/devices` | 设备列表、详情、绑定 |
| 航线任务 | `/wayline/api/v1/workspaces/{workspace_id}/jobs` | 任务创建、执行、监控 |
| 媒体文件 | `/media/api/v1/workspaces/{workspace_id}/files` | 文件上传、下载、删除 |
| 实时控制 | `/control/api/v1/devices/{device_sn}/drc` | 远程控制、相机控制 |
| 地图服务 | `/map/api/v1/workspaces/{workspace_id}/elements` | 地图元素管理 |
| 存储服务 | `/storage/api/v1/workspaces/{workspace_id}/sts` | 临时凭证获取 |

### WebSocket 事件

连接地址：`ws://localhost:6789/api/v1/ws`

主要事件类型：
- `device_online` - 设备上线
- `device_offline` - 设备离线
- `device_osd` - 设备状态数据
- `device_hms` - 设备健康信息
- `flight_task_progress` - 任务执行进度

## 📁 项目结构

```
Cloud-API-Demo/
├── backend/                      # 后端项目
│   ├── cloud-sdk/               # DJI Cloud SDK
│   │   └── src/main/java/com/dji/sdk/
│   │       ├── cloudapi/        # Cloud API 定义
│   │       ├── mqtt/            # MQTT 通信
│   │       ├── websocket/       # WebSocket 服务
│   │       └── config/          # SDK 配置
│   ├── sample/                  # 示例应用
│   │   └── src/main/java/com/dji/sample/
│   │       ├── component/       # 组件（MQTT、Redis、OSS）
│   │       ├── manage/          # 设备管理
│   │       ├── wayline/         # 航线任务
│   │       ├── media/           # 媒体管理
│   │       ├── control/         # 设备控制
│   │       ├── map/             # 地图服务
│   │       └── storage/         # 存储服务
│   └── pom.xml
├── frontend/                     # 前端项目
│   ├── src/
│   │   ├── api/                 # API 接口
│   │   ├── components/          # Vue 组件
│   │   │   ├── devices/         # 设备相关组件
│   │   │   ├── task/            # 任务相关组件
│   │   │   ├── flight-area/     # 飞行区域组件
│   │   │   └── g-map/           # 地图组件
│   │   ├── pages/               # 页面
│   │   ├── router/              # 路由配置
│   │   ├── store/               # 状态管理
│   │   ├── utils/               # 工具函数
│   │   ├── mqtt/                # MQTT 客户端
│   │   └── websocket/           # WebSocket 客户端
│   ├── package.json
│   └── vite.config.ts
├── cloud_sample_init.sql         # 数据库初始化脚本
├── LICENSE
└── README.md
```

## 🔧 开发指南

### 后端开发

#### 1. 实现 Cloud SDK 方法

创建服务类继承 SDK 抽象类：

```java
@Service
public class DeviceServiceImpl extends AbstractDeviceService {
    
    @Override
    public void updateTopoOnline(UpdateTopo topo) {
        // 实现设备上线逻辑
        log.info("Device online: {}", topo.getDeviceSn());
    }
}
```

#### 2. 调用 Cloud SDK 方法

注入服务并调用：

```java
@Service
public class WaylineJobService {
    
    @Autowired
    private AbstractWaylineService waylineService;
    
    public void executeJob(String jobId) {
        waylineService.flightTaskPrepare(jobId);
    }
}
```

#### 3. 实现 HTTP 接口

实现 SDK 定义的接口：

```java
@RestController
public class DeviceController implements IDeviceApi {
    
    @Override
    public HttpResultResponse<List<Device>> getDevices() {
        // 实现获取设备列表
        return HttpResultResponse.success(deviceList);
    }
}
```

### 前端开发

#### 1. API 调用

```typescript
import { getDevices } from '@/api/manage'

const fetchDevices = async () => {
  const res = await getDevices({ workspace_id: workspaceId })
  if (res.code === 0) {
    devices.value = res.data
  }
}
```

#### 2. MQTT 订阅

```typescript
import { useMqtt } from '@/components/g-map/use-mqtt'

const { subscribe } = useMqtt()

subscribe('sys/product/+/status', (topic, message) => {
  console.log('Device status:', message)
})
```

#### 3. WebSocket 连接

```typescript
import { useConnectWebsocket } from '@/hooks/use-connect-websocket'

const { connect, on } = useConnectWebsocket()

connect()
on('device_osd', (data) => {
  console.log('OSD data:', data)
})
```

### 添加新功能

1. **后端**：
   - 在 `backend/sample/src/main/java/com/dji/sample/` 下创建新模块
   - 定义 Controller、Service、DAO
   - 在 `application.yml` 中添加配置

2. **前端**：
   - 在 `frontend/src/api/` 下添加 API 接口
   - 在 `frontend/src/components/` 下创建组件
   - 在 `frontend/src/pages/` 下添加页面
   - 在 `router` 中配置路由

## 🐛 常见问题

### 1. 后端启动失败

**问题**: 数据库连接失败
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**解决方案**:
- 检查 MySQL 是否启动
- 确认数据库连接配置正确
- 检查防火墙设置

### 2. MQTT 连接失败

**问题**: 无法连接到 MQTT Broker

**解决方案**:
- 确认 MQTT Broker 已启动
- 检查端口是否被占用
- 验证用户名密码是否正确

### 3. 前端无法访问后端 API

**问题**: CORS 跨域错误

**解决方案**:
- 后端已配置 CORS，检查 `CorsFilter` 配置
- 确认前端配置的后端地址正确

### 4. 对象存储上传失败

**问题**: MinIO 连接失败

**解决方案**:
- 确认 MinIO 服务已启动
- 检查 bucket 是否已创建
- 验证访问密钥是否正确

### 5. Dock 3 设备无法上线

**问题**: Dock 3 连接不上平台

**解决方案**:
- 检查 Dock 3 网络连接是否正常
- 确认 Dock 3 配置的平台地址和端口正确
- 验证 App ID、Key、License 配置正确
- 检查服务器防火墙是否开放了必要端口（6789、1883、8083）
- 查看后端日志 `logs/cloud-api-sample.log` 排查问题

### 6. 航线任务执行失败

**问题**: 任务下发后 Dock 3 无响应

**解决方案**:
- 确认航线文件格式符合 Dock 3 要求
- 检查 Dock 3 当前状态是否允许执行任务
- 查看设备 HMS 信息，确认无异常告警
- 检查 MQTT 消息是否正常收发

### 7. 视频直播无法播放

**问题**: 前端无法看到实时视频

**解决方案**:
- 确认直播服务（RTMP/WebRTC）已正确配置
- 检查 Dock 3 是否已开启直播推流
- 验证网络带宽是否足够
- 查看浏览器控制台错误信息

## 📌 注意事项

### Dock 3 特别说明

1. **设备兼容性**: 本项目专门针对 Dock 3 适配，其他型号设备可能需要额外适配
2. **固件版本**: 建议 Dock 3 固件版本保持最新，以获得最佳兼容性
3. **网络要求**: Dock 3 需要稳定的网络连接，建议使用有线网络或 4G/5G
4. **App 凭证**: 必须在 DJI 开发者平台创建应用并获取有效的 License

### 生产环境部署建议

1. **安全加固**:
   - 修改默认的数据库密码
   - 配置 HTTPS 证书
   - 启用 MQTT 认证和 TLS
   - 配置防火墙规则

2. **性能优化**:
   - 使用 Nginx 反向代理
   - 配置 Redis 集群
   - 优化数据库索引
   - 启用 CDN 加速

3. **监控告警**:
   - 配置日志收集和分析
   - 设置系统监控告警
   - 定期备份数据库

## 🔗 相关链接

- **DJI 开发者平台**: https://developer.dji.com
- **Cloud API 文档**: https://developer.dji.com/doc/cloud-api-tutorial/cn/
- **DJI 技术支持**: https://sdk-forum.dji.net/

## ⚠️ 免责声明

本项目为第三方开发的示例项目，仅供学习和参考使用。使用本项目产生的任何问题，开发者不承担责任。生产环境使用请自行评估风险并进行充分测试。

DJI、DJI Dock 3、Matrice 3D/3TD 等为 DJI 公司的注册商标。本项目与 DJI 公司无官方关联。

## 📝 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

如果这个项目对你有帮助，请给个 Star ⭐️

## 📞 技术支持

- **Issues**: 在本仓库提交 Issue
- **DJI 官方文档**: https://developer.dji.com/doc/cloud-api-tutorial/cn/
- **DJI 开发者论坛**: https://sdk-forum.dji.net/

## 💬 交流与商用

### 技术交流

如需技术交流、问题咨询或商用版本支持，欢迎添加微信：

<div align="center">
  <img src="wx.JPG" alt="微信二维码" width="200"/>
  <p>扫码添加微信</p>
</div>

### 商用版本

本项目为开源学习版本，如需以下服务，请联系微信咨询：

- 🏢 **企业级定制开发** - 根据业务需求定制功能
- 🔧 **技术支持服务** - 提供专业的技术支持和培训
- 🚀 **部署实施服务** - 协助生产环境部署和优化
- 📊 **功能扩展开发** - 开发更多高级功能模块
- 🔐 **安全加固服务** - 企业级安全方案设计与实施
- 📱 **移动端开发** - iOS/Android 原生应用开发

> 📖 **详细服务内容和案例请查看 [CONTACT.md](CONTACT.md)**

---

**注意**: 本项目为第三方开发，仅供学习和参考使用。生产环境部署请根据实际情况进行安全加固和性能优化。
