# 安全配置说明 / Security Configuration Notice

## ⚠️ 重要提示 / Important Notice

本项目的敏感信息已被清理并替换为占位符。在部署前，您需要配置实际的凭证和密钥。

All sensitive information in this project has been sanitized and replaced with placeholders. You need to configure actual credentials and keys before deployment.

---

## 配置步骤 / Configuration Steps

### 步骤1：复制配置模板 / Step 1: Copy Configuration Templates

```bash
# 复制应用配置文件
cp source/backend_service/sample/src/main/resources/application.yml.example \
   source/backend_service/sample/src/main/resources/application.yml

# 复制数据库初始化脚本
cp source/mysql/init.sql.example source/mysql/init.sql

# 复制数据库权限脚本
cp source/mysql/privileges.sql.example source/mysql/privileges.sql
```

### 步骤2：配置敏感信息 / Step 2: Configure Sensitive Information

编辑以下文件并替换所有占位符（格式为 `[YOUR_*]`）：

Edit the following files and replace all placeholders (format: `[YOUR_*]`):

1. `source/backend_service/sample/src/main/resources/application.yml`
2. `source/mysql/init.sql`
3. `source/mysql/privileges.sql`

详细配置说明请参考 `CONFIG_GUIDE.md` 文件。

For detailed configuration instructions, please refer to the `CONFIG_GUIDE.md` file.

### 步骤3：验证配置 / Step 3: Verify Configuration

在启动服务前，请确保：

Before starting the service, make sure:

- [ ] 所有 `[YOUR_*]` 占位符已被替换
- [ ] 数据库连接信息正确
- [ ] DJI Cloud API凭证有效
- [ ] 所有服务器地址可访问
- [ ] 密码符合安全要求（强密码）

---

## 配置文件管理 / Configuration File Management

### 使用环境变量（推荐）/ Using Environment Variables (Recommended)

您可以使用环境变量来管理敏感配置：

You can use environment variables to manage sensitive configurations:

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:3306/cloud_sample
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}
```

然后在启动时设置环境变量：

Then set environment variables when starting:

```bash
export MYSQL_HOST=your-mysql-host
export MYSQL_USERNAME=your-username
export MYSQL_PASSWORD=your-password
```

### 使用配置管理工具 / Using Configuration Management Tools

对于生产环境，建议使用专业的配置管理工具：

For production environments, it is recommended to use professional configuration management tools:

- Spring Cloud Config
- HashiCorp Vault
- AWS Secrets Manager
- Azure Key Vault
- Kubernetes Secrets

---

## 安全检查清单 / Security Checklist

部署前请确认：

Before deployment, please confirm:

- [ ] 配置文件已添加到 `.gitignore`
- [ ] 不包含任何真实的IP地址、密码或密钥
- [ ] 数据库使用强密码
- [ ] 生产环境启用了SSL/TLS
- [ ] 限制了数据库和服务的网络访问
- [ ] 定期备份配置文件（加密存储）
- [ ] 团队成员了解安全配置要求

---

## 获取帮助 / Getting Help

如果您在配置过程中遇到问题，请：

If you encounter problems during configuration:

1. 查看 `CONFIG_GUIDE.md` 获取详细配置说明
2. 检查服务日志文件
3. 参考DJI官方文档：https://developer.dji.com/
4. 联系技术支持团队

---

## 文件清单 / File List

配置相关文件：

Configuration-related files:

```
.
├── .gitignore                          # Git忽略文件配置
├── CONFIG_GUIDE.md                     # 详细配置指南
├── README_SECURITY.md                  # 本文件
├── source/
│   ├── backend_service/
│   │   └── sample/
│   │       └── src/
│   │           └── main/
│   │               └── resources/
│   │                   ├── application.yml          # 实际配置（不提交）
│   │                   └── application.yml.example  # 配置模板
│   └── mysql/
│       ├── init.sql                    # 实际初始化脚本（不提交）
│       ├── init.sql.example            # 初始化脚本模板
│       ├── privileges.sql              # 实际权限脚本（不提交）
│       └── privileges.sql.example      # 权限脚本模板
```

---

**最后更新 / Last Updated:** 2024

**注意 / Note:** 请妥善保管您的配置文件和凭证，不要分享给未授权人员。

Please keep your configuration files and credentials safe and do not share them with unauthorized persons.
