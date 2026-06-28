# Changelog

## Unreleased

### Added

#### `palette` — `PChart` 图表组件（饼图/折线图/柱状图）
- 新增 `PChart`：零第三方依赖、Compose 原生 Canvas 自渲染的可扩展图表组件（与 mermaid 自渲染理念一致）。
- **架构**：面向数据 + 渲染器注册制 —— 新增图表类型仅需新增 `ChartSpec` 子类 + renderer 分支，零改动旧代码。
- **首期图表**：Pie（扇形/donut/百分比标签）、Bar（分组/堆叠/横向/圆角）、Line（折线/Catmull-Rom 平滑/数据点/面积填充）。
- **主题层**：新增 `PaletteChartTokens`（含 `categoricalColors` 色板，填补 Palette 原无 categorical/data-color 的缺口），全部从语义 token 派生，深色模式自适应。
- 数据模型 `ChartData`/`ChartSeries` + 纯逻辑 `ChartLogic`（`deriveYRange`/`resolveSeriesColor`/`resolveCategories`/`normalizeValue`，commonTest 单测）。
- 17 个逻辑测试 + 6 个 UI 测试。

#### `palette-mermaid` — Mermaid 图表渲染模块（19/19 图例）
- 新增独立模块 `palette-mermaid`，支持 Mermaid 19 种图例的解析与 Compose 原生渲染：
  - **核心图**：Flowchart、Sequence、ClassDiagram、ErDiagram、StateDiagram。
  - **数据图**：Pie、Gantt、GitGraph、Mindmap、Timeline、QuadrantChart、XYChart。
  - **架构/系统图**：RequirementDiagram、BlockDiagram、C4Diagram（Person/System/Container/Component/Boundary）。
  - **其他**：Journey、Packet、Sankey、Architecture（`architecture-beta`）。
- 解析器重构为 `MermaidDiagramParser` 注册制：新增图例仅需实现 parser 并注册，零侵入旧代码。
- 渲染细节对标 mermaid.live：边/标签/样式指令、activation、fragment、crow's foot、BFS 分层、tidy-tree、贝塞尔带等。

#### `palette-code` — 语法高亮模块（Prism 对齐，Phase 1-2）
- 新增独立模块 `palette-code`，提供声明式 grammar 引擎（对标 Prism.js）。
- Phase 1：grammar 优先 + lexer 兜底的双层架构，外部 API 不变。
- Phase 2：已迁移 11 种语言到声明式 grammar —— Markdown、SQL、CSS、HTML、XML、TOML、INI/properties、Java、TypeScript、Python、Kotlin。
- Phase 3：新增 C/C++/Go/Rust 4 种系统语言（基于 `cFamilyGrammar` 工厂；Rust 因 `#[attr]` 与 raw string 自定义 grammar），注册别名 c/h、cpp/c++/cxx/cc/hpp、go/golang、rust/rs。code 模块支持语言增至 16 种。
- 新增 matcher 引擎原语与 `embeddedTokens` 嵌入钩子，支持动态语言嵌入（HTML 内 CSS/JS、Markdown 内代码块）。
- 渲染支持 bold/italic/important 字体样式标记。

#### `palette-markdown` — Markdown 解析与渲染模块
- 新增独立模块 `palette-markdown`（block/inline parser）+ `palette/.../components/markdown`（Viewer/Editor UI）。
- 完整 CommonMark + GFM 支持：ATX/Setext heading、thematic break、fenced/indented code、GFM 表格、task list、嵌套 list/blockquote、HTML block、reference definition；strong/emphasis（含 `***`）、code span、strikethrough、link/image、autolink、entity 解码。
- 分派机制：fenced code → `palette-code`；`mermaid` → `palette-mermaid`；失败有 per-block fallback。Diagnostics 汇总到 `MarkdownRenderModel`。
- **编辑器阶段 A-C**：格式化工具栏（加粗/斜体/删除线/标题/列表/引用/链接/代码/代码块/表格/分隔线）、Tab/Shift+Tab 缩进、回车自动续行、`MarkdownHistory` 撤销/重做（输入合并）、Ctrl/Cmd 快捷键。
- **Viewer 阶段 C**：内置 `verticalScroll`、`onAnchorClick` 滚动到 `heading:<slug>`、透传 `showCopyAction` 代码复制。

#### `palette-markdown` 阶段 D — TOC + Frontmatter
- **Frontmatter**：新增 `MarkdownFrontmatter` 块类型；解析器在 `index==0` 识别首尾 `---`/`+++`（简易 `key: value` 解析，支持引号剥离，不引入 YAML 依赖）。渲染层将其从正文剥离，仅作为 `MarkdownRenderModel.frontmatter` 结构化元数据暴露。未闭合时回退为 thematic break。
- **TOC**：`MarkdownRenderModel.toc: List<MarkdownTocEntry>`，复用 slug 去重逻辑，id 与标题 `testTag("heading:<id>")` 一致。
- **`PMarkdownToc` 组件**：按层级缩进、点击 `onNavigate(id)` 桥接到 Viewer 锚点滚动；`MarkdownTocDefaults` 全部从 `PaletteTheme` 派生。
- 清理 `MarkdownParser.kt` 中未调用的重复 `toRenderBlock` 死代码。

#### `palette` 核心库
- 新增 `PDateRangePicker`：日期/时间区间选择器（双月日历 + 时间页脚 + 预设范围）。
- 补齐 6 个组件：ColorPicker、Transfer、Calendar、Grid、Tree、Tour、Upload（关闭原 maturity P1/P2 缺口，仅剩 Dropdown、Chart 待补）。

### Fixed

- 修复 iOS/Kotlin-Native 编译回归：饼图图例百分比格式化使用了 `String.format`（Kotlin/Native 不可用），改为平台无关的算术格式化 `formatPercentage`，并补单元测试覆盖四舍五入边界。
- 修复 3 个新模块在 Android 单元测试下 `CompatibilityFixtureTest` 全红：`commonTest/resources` 未传递到 `testDebugUnitTest`，通过注册 `src/commonTest/resources` 到 Android test 源集解决；`.java`/`.kt` fixture 重命名为 `.txt` 以规避 AGP 源码扩展名过滤。

### Changed

- 路由组件 Defaults 通过 `PaletteTheme.componentThemes` 接入顶层主题（仅当其为主要视觉样式面时）。

### Breaking Changes

- None.

---

## v0.1.8 — 2026-06-22

### Added

- Added root-level component theme overrides through `PaletteComponentThemes`.
- Added semantic state, opacity, motion, elevation, and control-density tokens for global style customization.
- Added component token coverage across action, selection, form, navigation, data display, feedback, overlay, progress, media, utility, layout, floating action, upload, pagination, and screen components.
- Added `docs/theming.md` with precedence rules, token customization examples, component-token mapping, and migration guidance.
- Added desktop theme override tests and Defaults static audit tests for component token adoption.

### Changed

- Routed component Defaults through `PaletteTheme.componentThemes` where the value is a major visual style surface.
- Preserved existing public Defaults constants as compatibility aliases; use the new token-backed Defaults functions for root-theme-aware values.

### Breaking Changes

- None.
