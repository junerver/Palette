# PChart 图表组件路线图

> 创建：2026-06-28
> 状态：**首期完成**（饼图 / 折线图 / 柱状图）

## 设计原则

1. **零第三方依赖、Compose 原生 Canvas 自渲染** —— 与 `palette-mermaid` 一致，不引入 WebView / Vico / KoalaPlot，避免主题 token 无法接入 `PaletteTheme` 的硬约束冲突。
2. **面向数据 + 渲染器注册制** —— 新增图表类型 = 新增 `ChartSpec` 子类 + renderer 分支，零改动旧代码（对标 mermaid parser 注册制）。
3. **顶层主题 token 全覆盖** —— 新增 `PaletteChartTokens`，含 `categoricalColors` 色板（填补 PaletteColors 原无 categorical/data-color 概念的缺口），全部从语义 token 派生，深色模式自适应。

## 架构

```
PChart(spec, data, modifier, options, colors)
├─ ChartSpec       sealed: Pie / Bar / Line（首期）
├─ ChartData       series: List<ChartSeries>, categories: List<String>
├─ ChartSeries     label, values, color?
├─ ChartOptions    title / showAxes / showGrid / showLegend / legendPosition / animation / yRange
├─ ChartColors     token-backed（axis/grid/tick/legend/categoricalColors/…）
└─ ChartRenderer   sealed when(spec) → Pie/Bar/Line renderer（每个纯 Compose Canvas）
```

### 纯逻辑（`ChartLogic.kt`，commonTest 单测覆盖）
- `deriveYRange(series, override?)` —— 空→(0,1)；全非负→min 钳 0；否则真实 min/max；override 永远胜出。
- `resolveSeriesColor(series, index, palette, fallback)` —— 显式色 > 色板循环 > 兜底。
- `resolveCategories(data)` —— 显式 > 派生 1..n。
- `normalizeValue(value, yMin, yMax)` —— 归一化到 [0,1]，零 span 安全。

## 已完成（首期）

| 图表 | 能力 |
|---|---|
| **Pie** | 扇形 / donut（中空）/ 百分比标签（平台无关格式化，复用 mermaid 修复）/ 起始角 |
| **Bar** | 分组 / 堆叠 / 横向；圆角；坐标轴 + 网格线；主题色 |
| **Line** | 折线 / 平滑（Catmull-Rom→Bézier）/ 数据点 / 面积填充；坐标轴 + 网格线 |

- 主题层：`PaletteChartTokens`（颜色 + 维度 + 色板 + 文本样式）
- 数据模型 + 纯逻辑 + ChartColors/ChartDefaults
- `Palette.kt` 导出（Pattern B，保留具名参数默认值）
- 测试：`ChartLogicTest`（17 例）+ `ChartUiTest`（6 例）

## 待办（后续阶段）

### 阶段 A：交互与命中检测
- [ ] 柱/扇区点击命中检测（坐标范围判定），`onPointClick(seriesIndex, pointIndex)` 完整回调
- [ ] Tooltip 悬停浮层（hover 显示数值）
- [ ] 图例点击切换系列显隐

### 阶段 B：更多图表类型（注册制扩展）
- [ ] Area（独立于 Line.areaFill 的一等类型）
- [ ] Scatter / Bubble
- [ ] Radar（雷达）
- [ ] Gauge / Dial（仪表盘）

### 阶段 C：渲染增强
- [ ] 坐标轴 tick 文本（当前仅网格线，无数值刻度标签）
- [ ] 多 Y 轴 / 双轴
- [ ] 进出场动画（`animateFloatAsState` → 复杂序列动画）
- [ ] 堆叠百分比模式

### 阶段 D：数据与性能
- [ ] 大数据集 LazyColumn 化（仅 Line/Area 关键路径）
- [ ] 数据绑定 DSL / 流式更新
- [ ] 缺失值（null）处理

## 风险/边界记录
- Canvas drawText 文本非语义节点，UI 测试只能验证 composition 不崩溃 + 标题/图例（用 Text composable 渲染），扇区/刻度标签无法 node 断言。
- categorical 色板固定从语义 token 派生（primary/success/warning/error/info + 透明度变体，10 色）；自定义色由 `ChartSeries.color` 覆盖。
- 平滑曲线用 Catmull-Rom→Bézier（张力 1/6），与 mermaid flowchart 一致。
