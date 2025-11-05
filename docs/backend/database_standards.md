
# 数据库开发规范

## 目录

- [1. 数据库选型](#1-数据库选型)
- [2. 数据库设计原则](#2-数据库设计原则)
- [3. 命名规范](#3-命名规范)
- [4. 字段设计](#4-字段设计)
- [5. 数据访问层](#5-数据访问层)
- [6. SQL 查询规范](#6-sql-查询规范)
- [7. 数据迁移](#7-数据迁移)

---

## 1. 数据库选型
- **MySQL 8.0**: 主要关系型数据库
  - **存储引擎**: 使用InnoDB存储引擎
  - **字符集**: 使用utf8mb4_unicode_ci字符集

## 2. 数据库设计原则
- **规范化设计**: 遵循第三范式（3NF）设计表结构
- **合理冗余**: 在必要时允许适度冗余以提高查询性能
- **使用索引**: 为常用查询字段创建适当索引
- **避免过度索引**: 索引会影响写入性能，需权衡利弊

## 3. 命名规范
- **表名**: 使用小写字母，单词间用下划线分隔，使用名词复数形式（例如：`users`, `order_items`）
- **字段名**: 使用小写字母，单词间用下划线分隔（例如：`first_name`, `created_at`）
- **索引名**: 使用 `idx_表名_字段名` 格式（例如：`idx_users_email`）
- **外键名**: 使用 `fk_表名_关联表名` 格式（例如：`fk_order_items_orders`）

## 4. 字段设计
- **主键**: 使用自增整数或UUID
- **创建/更新时间**: 每个表**必须**包含 `created_at`, `updated_at` 字段
- **软删除**: **必须**使用 `is_deleted` 字段实现软删除，**禁止**物理删除数据
- **审计字段**: 每个表**必须**包含以下审计相关字段
  - `creator_id`: **必须**记录创建者ID
  - `updater_id`: **必须**记录最后更新者ID
  - `creator`: **必须**记录创建者姓名或用户名
  - `updater`: **必须**记录最后更新者姓名或用户名

- **数据类型选择**：
  - 使用 `VARCHAR` 作为主键类型
  - 使用 `VARCHAR` 替代 `CHAR`，除非固定长度
  - 使用 `DECIMAL` 处理金额，避免使用 `FLOAT` 或 `DOUBLE`
  - 使用 `DATETIME` 或 `TIMESTAMP` 处理时间
  - 使用 `TEXT` 处理大文本，根据大小选择 `TEXT/MEDIUMTEXT/LONGTEXT`

## 5. 数据访问层
- **使用 MyBatis-Plus**: 采用MyBatis-Plus作为ORM框架，简化数据访问层代码
- **严格遵循Repository模式**: 所有数据访问必须通过Repository接口进行
- **避免原生SQL**: 优先使用MyBatis-Plus提供的QueryWrapper和LambdaQueryWrapper
- **分页查询**: 大数据集查询必须使用MyBatis-Plus的分页插件实现分页
- **禁止在Controller层直连数据库**: Controller层不允许直接访问数据库或Repository，必须通过Service层间接访问
- **数据访问分层**: 遵循Domain Repository接口 -> Gateway Repository实现 -> Service调用的分层模式
- **使用BaseMapper**: 继承MyBatis-Plus的BaseMapper接口，利用内置CRUD方法

## 6. SQL 查询规范
- **查询优化**：
  - 避免使用 `SELECT *`，明确指定字段
  - 使用 `LIMIT` 分页查询，避免大结果集
  - 避免在 `WHERE` 子句中对字段进行函数操作
  - 使用 `JOIN` 替代子查询
  - 合理使用 `EXPLAIN` 分析查询计划

## 7. 数据迁移
- **使用 Flyway**: 采用Flyway作为数据库版本控制和迁移工具
- **统一管理**: **所有数据库迁移脚本必须统一放置在app module中管理**，禁止在各业务模块的gateway层存放迁移脚本
- **环境分离**: **必须区分生产和测试环境脚本**，确保语法兼容性
- **生产脚本位置**: `modules/app/src/main/resources/db/migration/` (MySQL语法)
- **测试脚本位置**: `modules/app/src/test/resources/db/migration/h2/` (H2兼容语法)
- **迁移文件命名**: 使用 `V{版本号}__{描述}.sql` 格式（例如：`V1.0.1__Create_user_table.sql`）
- **版本控制**: 迁移文件一旦提交不可修改，新的变更需要创建新的迁移文件
- **环境一致性**: 确保开发、测试、生产环境的数据库结构功能上保持一致
- **回滚策略**: 对于不可逆操作，提供相应的回滚脚本
- **模块职责分离**: 业务模块负责领域设计和数据访问层实现，数据库结构变更由app module统一管理

### 7.1 数据库脚本环境分离规范

#### 7.1.1 脚本分离原则
- **生产脚本**：`src/main/resources/db/migration/` (MySQL语法)
- **测试脚本**：`src/test/resources/db/migration/h2/` (H2兼容语法)

#### 7.1.2 目录结构
```
modules/app/
├── src/main/resources/db/migration/    # 生产脚本 (MySQL)
│   ├── V1.0.1__Create_user_table.sql
│   └── V1.0.2__Create_order_table.sql
└── src/test/resources/db/migration/h2/ # 测试脚本 (H2)
    ├── V1.0.1__Create_user_table.sql
    ├── V1.0.2__Create_order_table.sql
    └── test-data.sql
```

#### 7.1.3 语法差异处理
**MySQL → H2 兼容性转换**：

| 功能 | MySQL语法 | H2处理方式 |
|------|-----------|------------|
| 时间戳更新 | `ON UPDATE CURRENT_TIMESTAMP` | 应用层处理 |
| 存储引擎 | `ENGINE=InnoDB` | 移除 |
| 字符集 | `CHARSET=utf8mb4` | 移除 |
| 注释 | `COMMENT` | 移除 |

**脚本适配示例**：
```sql
-- MySQL (生产)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- H2 (测试)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 7.1.4 配置规范
**生产环境**：
```yaml
spring:
  flyway:
    locations: classpath:db/migration  # MySQL脚本
```

**测试环境**：
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
  flyway:
    locations: classpath:db/migration/h2  # H2脚本
```

#### 7.1.5 维护要求
- **同步更新**：生产脚本更新时，必须同步更新测试脚本
- **功能一致性**：确保两个环境脚本功能完全一致
- **版本同步**：版本号必须保持同步
- **语法验证**：更新后必须在H2环境中验证

**Flyway配置示例：**
```properties
# application.yml
spring:
flyway:
enabled: true
locations: classpath:db/migration
baseline-on-migrate: true
validate-on-migrate: true
clean-disabled: true
```

**迁移文件示例：**
```sql
-- V1.0.1__Create_user_table.sql
CREATE TABLE users (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     username VARCHAR(50) NOT NULL UNIQUE,
                     email VARCHAR(100) NOT NULL UNIQUE,
                     password_hash VARCHAR(255) NOT NULL,
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                     is_deleted tinyint(1)    default 0                not null comment '1=已删除, 0=未删除'
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
```
