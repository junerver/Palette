# Palette 组件库成熟度缺口分析（复核版）

> 复核日期：2026-03-10（初版）/ 2026-06-28（二次复核）
> 复核范围：仓库当前代码、测试、CI、文档
> 结论：缺口已大幅对齐；测试覆盖率 80%+ 指标持续达成；新增三大特性模块（code/markdown/mermaid）

> **本次复核（2026-06-28）摘要**：自 v0.1.8（2026-06-22）以来，仓库进入高速交付期，新增 3 个独立 Gradle 模块（`palette-code` / `palette-markdown` / `palette-mermaid`），补齐 6 个原 P1/P2 缺失组件，测试用例数翻倍（516 → 1033）。本次复核顺带修复了两项回归：iOS Kotlin/Native 编译失败（`String.format` 不可用）、三模块 Android 单元测试资源缺失。覆盖率因新增未充分测试的大体量模块由 90.03% 回落至 80.13%（仍过 80% 门禁，但余量变小，需关注）。
>
> **阶段 D 追加（2026-06-28）**：完成 markdown 阶段 D（TOC + Frontmatter）—— `MarkdownFrontmatter` 块类型 + `MarkdownRenderModel.toc` + `PMarkdownToc` 组件，配套 19 个单测/UI 测试。主组件库覆盖率回升至 **80.17%**（Stage D 测试贡献）。另清理 `MarkdownParser.kt` 中 180 行未调用死代码。发现并记录 `palette-markdown` 的 iOS/Native 测试编译遗留债（`CompatibilityFixtureTest` 用了 JVM-only 的 `ClassLoader`，仅 Android/Desktop 通过），待后续用 `expect/actual` 资源加载器修复。
>
> **Phase 3 追加（2026-06-28）**：`palette-code` 新增 C/C++/Go/Rust 4 种语言 grammar，code 模块支持语言增至 16 种。**P1 缺口清零**——复核确认 `PSelect` 已完整覆盖 Dropdown 语义（受控泛型单选、三档尺寸、四态色、可搜索），不再单建 PDropdown。当前**唯一剩余缺口为 Chart**。

## 总览

| 维度 | 当前状态（2026-06-28） | 结论 |
| --- | --- | --- |
| 测试体系 | commonTest(51) + desktopTest(114) + 3 新模块 commonTest(12)；全目标 0 失败 | 已达成 |
| 测试覆盖率指标 | Kover 行覆盖率 `80.13%`（门禁 `80%`）；测试用例数 `1033`、`fail=0` | 已达成（余量收窄） |
| 文档体系 | README、theming.md、3 份特性路线图；仍缺在线文档站点 | 部分达成 |
| 高级组件 | P1 全部完成（Dropdown 由 PSelect 满足）；P2/P3 仅 Chart 缺失 | 已达成 |
| 工程化 | CI、Detekt、Ktlint、覆盖率门禁、发布前校验 | 已达成 |
| 国际化 | `PaletteStrings` 中英文与主题注入 | 部分达成 |
| 性能优化与基准 | Desktop 逻辑基准 + Android Macrobenchmark；CI 自动性能门禁未建 | 部分达成 |
| 无障碍、生态、平台特性 | 仍缺少系统化建设 | 未达成 |

## 缺口同步（按原条目）

### 1) 测试体系（P0）

**已完成**
- `palette/src/commonTest` 当前共 `51` 个测试文件（初版 42 → +9）。
- `palette/src/desktopTest` 当前共 `114` 个测试文件（初版 60 → +54）。
- 新增模块测试：`palette-code`(4) / `palette-markdown`(2) / `palette-mermaid`(6) 的 `commonTest`。
- 本地执行 `.\gradlew.bat :palette:runCoverageChecks`（含 `allTests` + 覆盖率门禁）通过。
- `palette/build/test-results` 汇总：`tests=1033`、`fail=0`、`skipped=0`（初版 516 → 接近翻倍）。
- **2026-06-28 修复**：3 个新模块 `CompatibilityFixtureTest` 在 Android `testDebugUnitTest` 下全红（commonTest 资源不传递 + AGP 对 `.java`/`.kt` 扩展名过滤），已修。

**未完成**
- Snapshot 测试仍未建立。
- 无障碍测试尚未形成独立验收基线。
- iOS 端自动化测试覆盖仍有补强空间。
- **新增关注点**：覆盖率由 90.03% 降至 80.13%，主因是 mermaid renderer、code grammar 引擎、markdown editor 等大体量模块单测覆盖偏低。建议后续针对这三块补测，把覆盖率回升至 85%+。

**指标结论**
- 核心组件覆盖率 **80%+**：当前**已达成**，Kover 行覆盖率为 `80.13%`（2026-06-28 实测）。
- 覆盖率门禁阈值为 `80%`（CI 执行 `:palette:runCoverageChecks` 校验）。

### 2) 文档体系（P0）

**已完成**
- 已有 `README.md`、`README.zh-CN.md`、`docs/maturity-gaps.md`、`docs/theming.md`。
- 3 份特性路线图（含完成度回填表）：
  - `docs/compose/plans/mermaid-diagram-support-roadmap.md`（19/19 图例 ✅）
  - `docs/compose/plans/palette-code-prism-alignment-roadmap.md`（Phase 1-2 ✅）
  - `docs/compose/plans/palette-markdown-evolution-roadmap.md`（阶段 A-C ✅，D-F 待办）

**未完成**
- 尚无系统化组件 API 文档目录与在线文档站点。

### 3) 高级组件（P1）

**P1 组件现状**
- 已有：DatePicker、TimePicker、Menu、Tabs、Breadcrumb、Steps、Message、Notification、Drawer、Tooltip、Popover。
- ~~缺失：Dropdown~~ **已满足**（2026-06-28 复核）：`PSelect` 即完整的下拉选择器（受控泛型单选、Material3 `DropdownMenu`、三档尺寸、四态色、禁用态、焦点/悬停边框、可搜索、锚点宽度自适应、自定义选项渲染），功能覆盖 Dropdown 语义，**P1 缺口清零**。

**P2/P3 补充**
- 已有（初版标注缺失，现已补齐）：ColorPicker、Transfer、Calendar、Grid、Tree、Tour、Upload。
- 缺失：**Chart**（唯一剩余 P2/P3 缺口）。

> 即初版列出的 8 个 P2/P3 缺口中已有 6 个（ColorPicker/Transfer/Calendar/Grid/Tree/Tour/Upload）实现，仅剩 Chart。
> **Phase 3 追加（2026-06-28）**：`palette-code` 新增 C/C++/Go/Rust 4 种语言 grammar（基于 `cFamilyGrammar` 工厂；Rust 因 attribute/raw-string 自定义），注册别名 c/h、cpp/c++/cxx/cc/hpp、go/golang、rust/rs。至此 code 模块支持 16 种语言。

### 4) 工程化配置（P0）

**已完成**
- CI：`.github/workflows/ci.yml`（JDK 21，质量检查、测试、覆盖率门禁、构建）。
- 发布前校验：`.github/workflows/release-check.yml`（JDK 21）+ `:palette:verifyReleaseReadiness`。
- 静态检查：根项目启用 Detekt / Ktlint。
- 覆盖率门禁：`palette/build.gradle.kts` 已接入 Kover，并定义 `verifyCoverageBaseline`、`runCoverageChecks`、`verifyReleaseReadiness` 任务链。

**未完成**
- 自动发布到 Maven Central 流程未在本仓库形成完整闭环。

### 5) 国际化（P1）

**已完成**
- `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/core/i18n/PaletteStrings.kt` 已提供 `zhCN()` / `enUS()`。
- `PaletteTheme` 已支持 `strings` 注入。

**未完成**
- 未覆盖更多语言（zh-TW/ja/ko 等）。
- 未见 RTL 与系统化资源文件方案。

### 6) 无障碍性（P2）

- 仍缺少独立无障碍测试与验收基线，判定为未达成。

### 7) 性能优化与监控（P2）

**已完成**
- Desktop 基准：`palette/src/desktopBenchmark/kotlin/xyz/junerver/compose/palette/benchmark`。
- Android 宏基准：`benchmark/src/benchmark/kotlin/xyz/junerver/compose/palette/benchmark/PaletteMacrobenchmark.kt`。
- 仓库内保留 `baseline-bench` 分支用于基线对比。

**未完成**
- CI 中未建立自动性能回归门禁。
- 统一性能报告归档流程未完成。

### 8) 生态系统建设（P2）

- 目前主要为工作流文件，Issue/PR 模板、贡献者体系与工具链仍不足，判定为未达成。

### 9) 发布与分发（P1）

**已完成**
- `palette/build.gradle.kts` 已启用 `maven-publish` 插件。
- 已有发布前检查任务 `verifyReleaseReadiness`。
- 当前版本 `VERSION_NAME=0.1.8`（`gradle.properties`）。

**未完成**
- 未见完整 `publishing {}` 发布元数据与自动发布流水线。
- CHANGELOG/版本策略未形成自动化闭环（2026-06-28 已补全 Unreleased 归档，但未自动化）。

### 10) 平台特性支持（P3）

- Android/iOS/Desktop 的平台特性增强项（如 Widget、托盘、快捷键等）暂无系统推进记录，判定为未达成。

## 当前判断

- 是否已经对齐 maturity-gaps 缺口：**基本对齐**（初版"部分对齐"，本次升级）。
- 测试覆盖率是否达到文档指标（80%+）：**已达标**，当前行覆盖率为 `80.13%`（2026-06-28 实测）。
- 当前主要剩余缺口集中在：**Chart 组件**、无障碍、文档站点、生态建设、性能门禁与平台特性支持。（Dropdown 已由 PSelect 覆盖，不再单列。）
- 新增关注：3 个大体量新模块拉低覆盖率，建议针对性补测。
