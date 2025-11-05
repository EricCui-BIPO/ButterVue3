
# 🧭 Report Module DDD Architecture — Java Code Generation Prompt

## 🎯 设计目标
实现一个基于领域驱动设计（DDD）的报表模块后端架构，支持：
- 直接访问 MySQL 表（无需 DataSource 层）；
- 数据集（Dataset）抽象与指标（Indicator）定义；
- 基于指标生成可配置的图表（Chart）；
- 多个图表聚合成报表（Report）；
- 支持全局过滤（Filter）；
- 使用 **Java + Spring Boot** 实现。

---

## 🧩 领域分层结构

数据流层级：

```
Dataset → Indicator → Chart → Report
            ↑          ↑          ↑
          Filter层     Filter层   Filter层
```

---

## 🏗️ 领域对象定义与职责

### 1️⃣ Dataset（数据集）
定义 SQL 查询、过滤条件与更新策略。

```java
class Dataset {
    private String id;
    private String name;
    private String sql; // 原始 SQL
    private List<Filter> filters;
}
```

---

### 2️⃣ Indicator（指标）
定义业务指标计算逻辑，引用 Dataset 并指定聚合方式。

```java
class Indicator {
    private String id;
    private String name;
    private String datasetId;
    private String calculation; // SUM(order_count)
    private List<String> dimensions; // [date, service_type]
    private List<Filter> filters;
}
```

---

### 3️⃣ Chart（图表）
每个图表绑定一个指标，定义展示类型、维度与样式。

```java
class Chart {
    private String id;
    private String name;
    private String type; // bar, line, pie
    private String dimension;
    private String indicatorId;
    private List<Filter> filters;
}
```

---

### 4️⃣ Report（报表聚合根）
聚合多个 Chart，并管理布局、过滤、刷新周期。

```java
class Report {
    private String id;
    private String name;
    private List<Chart> charts;
    private List<Filter> filters;
    private String layout;
    private String refreshInterval;
}
```

---

### 5️⃣ Filter（横切领域）
统一定义过滤条件（权限、默认、动态）。

```java
class Filter {
    private String field;
    private String operator; // =, >, <, IN, LIKE
    private Object value;
    private boolean mandatory;
}
```

---

## 🧩 核心服务层设计

### 1️⃣ DatasetRepository
- 执行 SQL 并返回结果集。

### 2️⃣ IndicatorService
- 调用 DatasetRepository 执行指标计算。

### 3️⃣ ChartService
- 根据指标输出构建图表数据。

### 4️⃣ ReportService
- 聚合多个 Chart，输出完整报表。

### 5️⃣ FilterEngine
- 负责将不同层级 Filter 合并并注入 SQL。

---

## ⚙️ 执行流程

```
用户请求 → ReportService
   ↓
加载 Report → 获取关联 Chart
   ↓
ChartService → 调用 IndicatorService
   ↓
IndicatorService → 查询 Dataset → SQL + Filter 执行
   ↓
结果汇总成 ReportView(JSON)
```

---

## 📦 输出要求

请生成以下内容：
1. Java 类定义：
   - `Dataset`, `Indicator`, `Chart`, `Report`, `Filter`
2. Service 层：
   - `DatasetRepository`, `IndicatorService`, `ChartService`, `ReportService`, `FilterEngine`
3. 示例：**订单分析报表（Order Analysis Report）**
   - 指标：每日订单数（order_count）
   - 图表：折线图（chart_daily_orders）
   - 报表：包含一个图表（report_order_summary）
4. 提供一个 `Main.java` 示例，调用 `ReportService.getReport("report_order_summary")` 并打印输出 JSON。

---

## 💡 项目结构建议

```
/domain/dataset/
  Dataset.java
/domain/indicator/
  Indicator.java
/domain/chart/
  Chart.java
/domain/report/
  Report.java
/domain/filter/
  Filter.java
/application/service/
  DatasetRepository.java
  IndicatorService.java
  ChartService.java
  ReportService.java
  FilterEngine.java
/Main.java
```

---

## ⚙️ 使用说明
1. 复制本文件内容到 Claude Code 或 Cursor 中。
2. 指定语言环境为 **Java**。
3. AI 将自动生成符合该领域设计的代码结构。
4. 可扩展为 Spring Boot 应用或直接运行示例 Main 方法。
