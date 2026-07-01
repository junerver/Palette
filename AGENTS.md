# Repository Guidelines

## 项目概述

Palette 是一个 Compose Multiplatform 组件库，支持 Android、Desktop (JVM)、iOS 平台，组件库（`:palette`）与示例应用（`:app`）均启用了 wasmJs 目标，因此 Web (Wasm) 端可运行同一套示例。示例应用目前覆盖 Android、Desktop、Web 三端，共享 `commonMain` 中同一套代码。

## 项目结构

```
Palette/
├── palette/                    # 核心组件库模块（发布产物）
│   └── src/
│       ├── commonMain/         # 跨平台共享代码
│       │   └── kotlin/xyz/junerver/compose/palette/
│       │       ├── core/           # 核心模块
│       │       │   ├── tokens/     # 设计令牌 (PaletteColors/ SemanticColors/Shapes/Spacing/
│       │       │   │               #   Typography/Elevation/Motion/Opacity/ControlTokens/
│       │       │   │               #   ComponentThemes, FormTokens)
│       │       │   ├── theme/      # 主题 (PaletteTheme, PaletteMaterialTheme)
│       │       │   ├── spec/       # 组件规约 (ComponentSpec, ComponentInteraction)
│       │       │   ├── i18n/       # 国际化 (PaletteStrings)
│       │       │   └── util/       # 工具类 (ModifierExtensions, PaletteDefaults)
│       │       ├── foundation/     # 基础组件 (border/BorderContainer, layout/CenterVerticallyRow)
│       │       ├── components/     # UI 组件（90+，按功能分目录，分类见下文）
│       │       │   └── chart/      # 图表子系统 PChart（多文件架构，见下文）
│       │       └── Palette.kt      # 统一导出文件（typealias）
│       ├── commonJvmAndroid/   # JVM+Android 共享
│       ├── androidMain/        # Android 特定实现
│       ├── desktopMain/        # Desktop 特定实现
│       ├── iosMain/            # iOS 特定实现 (x64/arm64/simulatorArm64)
│       ├── wasmJsMain/         # Web (Wasm) 特定实现
│       ├── commonTest/         # 跨平台单元/逻辑测试
│       ├── desktopTest/        # Desktop UI 测试（Compose 测试框架）
│       ├── desktopBenchmark/   # Desktop 逻辑基准测试（kotlinx-benchmark）
│       └── androidTest/        # Android 仪器测试
├── palette-code/               # 语法高亮子模块（Prism 对齐：C/C++/Go/Rust 等 cFamilyGrammar）
├── palette-markdown/           # Markdown 渲染子模块（含 LaTeX/TOC/YAML frontmatter）
├── palette-mermaid/            # Mermaid 渲染子模块（19 种图例，注册制 parser）
├── palette-latex/              # LaTeX 公式渲染子模块
├── app/                        # 示例应用模块（Android/Desktop/Web 三端共享 commonMain）
│   └── src/
│       ├── commonMain/         # 三端共享：App 入口 + demo/（每组件一 Demo）+ navigation/ + ui/
│       ├── commonJvmAndroid/   # JVM+Android 共享
│       ├── androidMain/        # Android 入口 + 资源（含 Noto Sans SC 字体）
│       ├── desktopMain/        # Desktop 入口
│       ├── wasmJsMain/         # Web (Wasm) 入口
│       └── test/               # JVM 单元测试
├── benchmark/                  # Android 宏基准模块（com.android.test，targetProject = :app）
└── docs-site/                  # 在线文档站点（MkDocs Material + WASM 交互预览）
```

### 组件分类（`components/`）

90+ 组件按功能分目录，与 `docs-site/docs/components/` 分类一致：

- **通用 (general)**：`button`、`text`、`image`、`tag`、`badge`、`avatar`、`statistic`、`descriptions`、`tour`、`watermark`、`barcode`、`qrcode`、`commandpalette`、`floatbutton`、`affix`、`backtop` 等
- **表单 (form)**：`checkbox`、`radio`、`switch`、`toggle`、`slider`、`rate`、`select`、`cascader`、`treeselect`、`datepicker`、`daterangepicker`、`datetimerange`、`timepicker`、`inputnumber`、`inputotp`、`textfield`、`form`、`autocomplete`、`mentions`、`colorpicker`、`transfer`、`upload`、`searchbar` 等
- **数据展示 (data-display)**：`table`、`datagrid`、`list`、`tree`、`card`、`carousel`、`collapse`、`timeline`、`progress`、`skeleton`、`tooltip`、`calendar`、`chart`、`virtuallist`、`sortable`、`infinitescroll`、`steps` 等
- **反馈 (feedback)**：`alert`、`dialog`、`drawer`、`message`、`notification`、`toast`、`popup`、`popover`、`popconfirm`、`contextmenu`、`empty`、`result`、`loading` 等
- **导航 (navigation)**：`menu`、`breadcrumb`、`pagination`、`tabs`、`bottomnavigation`、`pageheader` 等
- **布局 (layout)**：`grid`、`space`、`container`、`scaffold`、`screen`、`toolbar` 等
- **内容渲染 (feature)**：`markdown`、`mermaid`、`code`、`latex`（核心模块内置包装，重度实现拆分到对应 `palette-*` 子模块）

### 图表子系统（`components/chart/`）

`PChart` 为零第三方依赖、Compose 原生 Canvas 自渲染的可扩展图表，采用「数据 + 渲染器注册制」：

- `Chart.kt` — `PChart` 组件入口，接入 `awaitPointerEventScope`（mouse hover + touch drag）驱动 tooltip/高亮；含图例点击切换显隐、dataZoom 缩放与图表联动（`controlledZoomRange`/`onZoomChange`）
- `ChartModels.kt` — `ChartSpec`（`Pie`/`Bar`/`Line`/`Scatter`/`Radar`）、`ChartSeries`（含 `yAxisIndex` 双轴绑定）、`ChartOptions`（含 `markLines`/`dataZoom`/`showTooltip`）、`MarkLine`/`DataZoom` 模型
- `ChartLogic.kt` — 纯函数：刻度/缩放（`niceTicks`/`evenTickFractions`）、范围（`deriveYRange`/`deriveDualYRanges`）、命中检测（`hitTestPoint`/`hitTestPie`/`hitTestScatter`）、`applyZoomSlice` 切片、`computeZoom` 缩放数学、`scatterPairs`/`radarAxisAngle`/`radarVertex`
- `ChartRenderer.kt` — 渲染器分发；`BarChartRenderer`/`LineChartRenderer`/`PieChartRenderer`/`ScatterChartRenderer`/`RadarChartRenderer` 分类型绘制
- `ChartAxisRenderer.kt` — 轴/网格/双 Y 轴刻度对齐；`ChartTooltip.kt` — `ChartTooltipOverlay` + 悬浮态；`ChartAnimation.kt` — 入场动画（spring 0→1）；`DataZoom.kt` — 缩放滑块（绝对位移追踪，跟手）
- `ChartDefaults.kt` — 默认值 + `PaletteChartTokens`（含 `categoricalColors` 色板、tooltip/legend/highlight 样式，全部从语义 token 派生）

**当前能力**：柱状（分组/堆叠/横向）、折线（平滑/面积/数据点）、饼图（扇形/donut/百分比）、散点图、雷达图；tooltip 命中看数（mouse hover + touch drag）、图例点击切换显隐、入场动画、markLine 标注（平均线/目标线）、双 Y 轴、dataZoom 数据缩放、图表联动。

## 构建命令

**Windows 环境下 agent 执行 Gradle 命令时必须使用 `.\gradlew.bat`，禁止使用 `./gradlew`（会导致进程卡住）。**

```bash
# 核心构建
.\gradlew.bat build                              # 构建全项目（含子模块）
.\gradlew.bat :palette:build                     # 仅构建组件库
.\gradlew.bat :palette-code:build                # 构建 code 高亮子模块
.\gradlew.bat :palette-markdown:build             # 构建 markdown 子模块
.\gradlew.bat :palette-mermaid:build              # 构建 mermaid 子模块
.\gradlew.bat :palette-latex:build                # 构建 latex 子模块
.\gradlew.bat :palette:publishToMavenLocal        # 发布组件库到本地 Maven

# 运行示例应用
.\gradlew.bat :app:run                            # 运行 Desktop 应用
.\gradlew.bat :app:hotRunDesktop                  # 通过 hotrun 插件运行支持热更新的 Desktop 应用
.\gradlew.bat :app:wasmJsBrowserDevelopmentRun     # 运行 Web (Wasm) 示例，启动 webpack dev server 并打开浏览器 (http://localhost:8080)
.\gradlew.bat :app:installDebug                    # 安装 Android Debug 包

# 测试
.\gradlew.bat :palette:allTests                    # 运行组件库全部测试（commonTest + desktopTest + androidTest）
.\gradlew.bat :palette:desktopTest                 # 运行 Desktop UI 测试
.\gradlew.bat :palette:testDebugUnitTest           # 运行 Android 单元测试
.\gradlew.bat :palette:desktopBenchmarkBenchmark   # 运行 Desktop 逻辑基准测试（kotlinx-benchmark，出 jmx/html 报告）

# Android 宏基准（benchmark 模块，targetProject = :app）
.\gradlew.bat :benchmark:connectedBenchmarkAndroidTest  # 运行 Android 宏基准测试（需连接设备或模拟器）

# 在线文档（docs-site，需 Python 环境）
cd docs-site && pip install -r requirements-docs.txt && mkdocs serve    # 启动本地文档预览（http://127.0.0.1:8000）
cd docs-site && mkdocs build                                          # 构建静态站点到 site/ 目录
```

## 编码规范

### 命名规范

- **包名**: `xyz.junerver.compose.palette`
- **文件名**: PascalCase（如 `PaletteTheme.kt`, `BorderContainer.kt`）
- **组件函数**: PascalCase（如 `PBadge`, `BorderTextField`）
- **Defaults 对象**: `XxxDefaults`（如 `BadgeDefaults`, `TextFieldDefaults`）

### 组件结构

每个组件目录应包含：

- `Xxx.kt` - 组件实现
- `XxxDefaults.kt` - 默认值定义

```kotlin
// XxxDefaults.kt 示例
object XxxDefaults {
    val Size: Dp = 10.dp

    @Composable
    fun color(): Color = PaletteDefaults.colors.primary
}

// Xxx.kt 示例
@Composable
fun Xxx(
    modifier: Modifier = Modifier,
    size: Dp = XxxDefaults.Size,
    color: Color = XxxDefaults.color(),
    // ...
) {
    // 实现
}
```

### 主题与 Tokens

- 使用 `PaletteTheme` 访问主题 tokens：
  - `PaletteTheme.colors` - 颜色
  - `PaletteTheme.spacing` - 间距
  - `PaletteTheme.shapes` - 形状
  - `PaletteTheme.typography` - 字体
  - `PaletteTheme.isDark` - 深色模式状态
- 使用 `PaletteDefaults` 访问全局默认值
- 新增组件或重构组件时，必须先明确该组件的“主要可调整样式面”（类似主流组件库暴露的主要颜色、字号、圆角、间距、尺寸、边框、阴影、动效、透明度等），不要把所有内部实现细节都暴露为 API。
- 组件默认样式必须优先接入顶层主题 token，使项目方可以在 `PaletteTheme` / `PaletteMaterialTheme` 顶层统一调整。局部参数只能作为实例级覆盖，不能成为唯一的定制入口。
- 若现有 `PaletteColors`、`PaletteSpacing`、`PaletteShapes`、`PaletteTypography`、`FormTokens` 或组件级 token 无法表达新增组件的主要样式，需同步新增或维护相应顶层 token，并在评审中说明 token 命名、语义和适用组件范围。
- `XxxDefaults` 中的默认颜色、字号、圆角、间距、边框、阴影、动效时长、禁用透明度等主要样式，不应直接硬编码为 `Color(...)` / `dp` / `sp` 常量后长期作为最终方案；应从 `PaletteTheme`、`PaletteDefaults` 或组件级顶层 token 派生。确属算法常量、协议常量、图形编码常量或不可主题化实现细节时，需要用简短注释说明原因。
- 推荐覆盖优先级：组件显式参数 > `XxxDefaults` 参数化默认值 > 组件级顶层 token > 全局语义 token > 受控兜底值。
- 新增或调整 token 时，需要同步更新统一导出、示例用法、主题相关测试，并确保深色模式、禁用态、悬停/聚焦/选中/错误等主要状态可由顶层主题稳定控制。

### 状态管理

项目使用 `compose-hooks` 库进行状态管理：

```kotlin
import xyz.junerver.compose.hooks.useState

val (value, setValue) = useState(initialValue)
```

- 新增组件与重构组件时，状态与重组逻辑默认优先使用 `compose-hooks`（如 `useState`、`useGetState`、`useBoolean`、`useCreation`、`useLatestState`）。
- 非必要场景不要直接使用原生 `remember { mutableStateOf(...) }` / `mutableStateOf(...)`。
- 如必须使用原生状态 API（例如接口实现类中的稳定状态持有），需在代码中标注原因并在评审中说明。

### 测试与性能基准守则

- 组件逻辑改动必须先补或先写测试（TDD），至少覆盖：正常路径、边界条件、回归场景。
- 提交前至少通过对应测试任务（如 `:palette:desktopTest` / `:palette:allTests`）。
- 涉及性能相关改动时，必须提供基准验证，禁止只凭体感结论。
- 基准验证必须包含基线对比：从已知提交（可用 `git worktree`）切出基线，在同机同参数下对比。
- 基准结果至少跑 3 轮，优先看 `median` 与波动（`std`/误差），不要仅用单次结果判断。
- Compose Multiplatform 项目默认优先 Desktop 或跨平台基准；Android 端基准作为补充而非唯一依据。
- 若基准显示关键路径回归，应继续优化或回退该优化，不得以功能通过替代性能验收。

### 统一导出

新增组件需在 `Palette.kt` 中添加 typealias 导出：

```kotlin
// 导入
import xyz.junerver.compose.palette.components.xxx.Xxx as XxxImpl
import xyz.junerver.compose.palette.components.xxx.XxxDefaults as XxxDefaultsImpl

// 导出
typealias XxxDefaults = XxxDefaultsImpl
```

## 关键依赖

### 核心依赖
- **compose-hooks** (`xyz.junerver.compose:hooks2`): React 风格状态管理
- **Material3**: UI 组件基于 Material Design 3
- **kotlinx-datetime / kotlinx-collections-immutable**: 跨平台工具库

### 子模块依赖
- **palette-code**: 语法高亮引擎（Prism.js 对齐，支持 C/C++/Go/Rust 等）
- **palette-markdown**: Markdown 渲染（含 LaTeX/TOC/YAML frontmatter）
- **palette-mermaid**: Mermaid 图表解析与渲染（19 种图例）
- **palette-latex**: LaTeX 公式渲染

### 测试与基准
- **kotlinx-benchmark**: Desktop 逻辑基准测试框架
- **androidx.benchmark**: Android 宏基准（benchmark 模块，测量 app 运行时性能）
- **Compose Testing**: Desktop UI 测试框架
- **JUnit**: 单元测试框架

## 平台目标

- Android: minSdk 24, targetSdk 35
- Desktop: JVM (Kotlin/JVM)
- iOS: x64, arm64, simulatorArm64

## 提交规范

提交信息使用常见前缀：`feat:`, `fix:`, `refactor:`, `chore:`, `docs:`
