# 开发规范 (Development Standards)

## 🚨 强制性开发规范检查

**重要：在开始任何开发工作前，AI助手必须主动完整的查看以下规范文档：**

### 前端开发规范
- **新手指南**:
    - 环境搭建、第一个功能开发、常见问题解答
    - [前端新手指南](./frontend/fe-getting-started.md)
- **架构设计**:
    - 系统架构设计、模块职责划分、依赖关系说明
    - [前端架构设计](./frontend/fe-architecture.md)
- **编码规范**:
    - 代码风格、文件结构、组件设计、性能优化规范
    - [前端编码规范](./frontend/fe-standards.md)
- **模板库**:
    - 标准代码模板、最佳实践、完整开发流程示例
    - [前端模板库](./frontend/fe-templates.md)
- **快速参考**:
    - 技术栈版本、开发命令、配置信息速查
    - [前端快速参考](./frontend/fe-quick-reference.md)
- **测试规范**:
    - 单元测试、集成测试和 E2E 测试规范
    - [前端测试指南](./frontend/fe-testing-standard.md)
    
### 后端开发规范
- **架构设计**: [Clean Architecture 规范](backend/clean-architecture-standards1.md) - 清洁架构和 DDD 设计模式
- **测试驱动**: [TDD 开发规范](./backend/tdd-standards.md) - 测试驱动开发流程和规范
- **基础规范**: [后端基础规范](./backend/basic-standards.md) - 代码风格、命名规范等基础要求
- **数据库规范**: [数据库规范](./backend/database_standards.md) - 数据库设计和操作规范

### API 接口规范

- **接口设计**: [API 标准规范](./api-standards.md) - API 设计原则和接口规范

### 强制执行规则

- ✅ **TDD优先**: 必须遵守测试驱动开发，先写测试再写实现代码
- ✅ **规范检查**: 开发前必须完整的查看相关规范文档
- ✅ **测试覆盖**: 前端测试覆盖率≥90%，后端单元测试≥90%
- ✅ **架构遵循**: 后端必须遵循Clean Architecture + DDD模式
- ✅ **代码质量**: 遵循ESLint、TypeScript严格模式等代码规范

### 开发流程检查清单

在开始任何开发任务时，AI助手应该：

1. [ ] 查看 `docs/development-standards.md` 了解总体规范
2. [ ] 根据开发类型查看对应的详细规范文档
3. [ ] 确认是否需要遵循TDD流程
4. [ ] 检查现有代码结构和模式
5. [ ] 按照规范进行开发

## 项目技术栈概览

### 前端技术栈
- **前端框架**: Vue 3 + Composition API
- **构建工具**: Vite
- **类型系统**: TypeScript（严格模式）
- **包管理**: Yarn Workspaces
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **国际化**: Vue I18n
- **样式**: Sass 支持


### 后端技术栈
- Java 11 + Spring Boot 2.7.x
- Spring Cloud 2021.0.x (Jubilee) - 微服务框架
    - Spring Cloud Config - 配置中心
    - Spring Cloud Netflix Eureka - 服务注册与发现
    - Spring Cloud Gateway - API网关
    - Spring Cloud OpenFeign - 服务间调用
    - Spring Cloud Circuit Breaker - 熔断器
- Gradle 构建工具
- MySQL 8.0 数据库
- JUnit 5 + Mockito + AssertJ 测试框架
