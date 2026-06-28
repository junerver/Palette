# Palette Markdown 模块演进路线图

> 创建日期：2026-06-28
> 对标对象：Marked.js、react-markdown、Markdown-it、EasyMDE、milkdown、Compose Markdown (Compose-Markdown / Markwon)
> 范围：`palette-markdown`（parser/render model）与 `palette/.../components/markdown`（Viewer / Editor UI）

## 1. 当前能力盘点（已确认）

| 能力域 | 状态 | 说明 |
| --- | --- | --- |
| Block parser | ✅ 完善 | ATX/Setext heading、thematic break、fenced/indented code、GFM 表格、task list、嵌套 list/blockquote、HTML block、reference definition |
| Inline parser | ✅ 完善 | strong/emphasis（含 `***` bold-italic）、code span、strikethrough、link/image（含 reference & 嵌套 bracket）、autolink、entity 解码、hard/soft break |
| 分派 | ✅ | fenced code → `palette-code`；`mermaid` → `palette-mermaid`；失败有 per-block fallback |
| Diagnostics | ✅ 模型层 | code highlighter / mermaid diagnostics 汇总到 `MarkdownRenderModel.diagnostics` |
| Source range | ⚠️ 部分 | 块级有 `MarkdownSourceRange`，但 thematic break、inline/token 级、跨行嵌套坐标未补齐 |
| Viewer | ✅ 基础 | 递归渲染、`onLinkClick`/`onAnchorClick`、`inlineImageContent` slot、task checkbox（只读态） |
| Editor | ⚠️ 薄弱 | 仅 `TextArea` + `PToggleGroup`(Edit/Preview/Split) + preview 内 task checkbox 双向同步 |

## 2. 与主流库的缺口（按优先级）

### P0 — 编辑器是当前最大短板

当前 `PMarkdownEditor` 实质是"文本框 + 预览开关"，对比 EasyMDE / milkdown / Markwon-editor 缺少：

1. **格式化工具栏**：加粗/斜体/删除线/标题/列表/引用/链接/代码/代码块/表格/分隔线等一键插入。目前用户必须手敲 markdown 语法。
2. **键盘快捷键**：`Ctrl/Cmd+B/I/K` 等格式化快捷键。
3. **粘贴净化 / 智能粘贴**：粘贴富文本/URL 时转为 markdown（至少 URL → `<url>` / `[text](url)`）。
4. **撤销/重做**：编辑器目前直接把 `value` 抛给上层，没有 history 栈，无法 `Ctrl+Z` 分步回退。
5. **Tab/Shift+Tab 缩进**：列表项与代码缩进的核心交互。
6. **自动续行**：回车时延续列表项 `- ` / 有序项 `1. ` / 引用 `> `。

### P1 — 渲染与可用性

7. **Viewer 自身滚动**：当前 `PMarkdownViewer` 不内置 `verticalScroll`，必须由外层 `Column(verticalScroll)` 承载；长文档在 Editor Split 预览里两栏滚动不同步，也没有滚动到锚点（`onAnchorClick` 给出了 slug 但无内置滚动到该 heading 的能力）。
8. **代码块内置复制按钮**：`PCodeBlock` 有 `showCopyAction`，但 Viewer 路径未暴露该参数，无法从 markdown 一键复制代码。
9. **TOC / 大纲**：heading 已有 `id` slug，但没有内置大纲组件或 `MarkdownRenderModel.toc`。
10. **Frontmatter（YAML）解析与剥离**：`---` frontmatter 当前会被当成 thematic break + paragraph，未作为独立块。

### P2 — 扩展语法

11. **Footnote**（`[^1]` / `[^1]: ...`）
12. **Admonition / Callout**（`:::note` / `> [!NOTE]`）
13. **Definition list**（`Term\n: definition`）
14. **Math / LaTeX**（`$inline$` / `$$block$$`，需分派到渲染占位）
15. **Autocomplete / emoji shortcode**（`:smile:`）

### P3 — 工程/质量

16. **Android `testDebugUnitTest` 资源加载失败**：`CompatibilityFixtureTest` 在 Android 单元测试里因 commonTest 资源不传递而全红（8 fail），desktop 通过。需修资源配置或迁测试源集。
17. **增量解析**：当前 `useCreation(renderModel, markdown)` 整篇重解析；超大文档（>10k 行）编辑器每次按键会重算。
18. **inline/token 级 source map**：补齐以支持编辑器"点击预览定位源码"双向定位。

## 3. 实施路线图（分阶段，每阶段独立可发布）

### 阶段 A：编辑器格式化工具栏（P0-1,3,5,6）— 本次实施
- 新增 `MarkdownToolbarActions`：基于光标选区的文本变换纯函数（`wrapSelection`、`toggleLinePrefix`、`insertPrefix` 等），**逻辑全部可单测**。
- `PMarkdownEditor` 顶部新增可选 `MarkdownFormatToolbar`，按钮调用上述变换并通过 `onValueChange` 回写。
- TextArea 接入 `Tab` 缩进、回车自动续行（通过新增的 `onBackspace/Enter/Tab` 钩子或包装层）。
- TDD：先写工具栏动作函数的单元测试，再接 UI。

### 阶段 B：编辑器撤销/重做与快捷键（P0-2,4）— ✅ 已完成（2026-06-28）
- 新增 `MarkdownHistory`（纯逻辑类，past/present/future + 连续输入合并），`useRef` 持有、`useState` 镜像 present 驱动重组；外部受控值同步走 `sync`（不入历史）。
- 打字走 `pushTyping`（阈值窗口内合并为单条历史项 → Ctrl+Z 按词/段回退），工具栏/快捷键/任务勾选走 `commit`。
- 快捷键：`Ctrl/Cmd+B`加粗、`+I`斜体、`+K`链接、`+Shift+K`删除线、`+E`行内代码、`+Shift+E`代码块、`+U`无序列表、`+O`有序列表、`+Shift+O`引用、`+Z`撤销、`+Shift+Z`/`+Y`重做。
- 顺手修复预存的 `MermaidDefaults` token 审计违规（note 颜色上提到 `PaletteUtilityTokens.mermaidNoteColor/mermaidNoteBorderColor`）。

> 注：调研发现 `compose-hooks` 已自带 `useUndo`（PersistentList），但因其无法在 `commonTest` 单测、且不提供输入合并，最终采用自建可测类方案。

### 阶段 C：Viewer 滚动 + 锚点跳转 + 代码复制（P1-7,8）
- Viewer 内置 `verticalScroll`（可通过参数关闭）+ `LazyColumn` 化大文档。
- `onAnchorClick` 默认实现：滚动到 `testTag("heading:<slug>")`。
- 透传 `showCopyAction` 到 fenced code。

### 阶段 D：TOC + Frontmatter（P1-9,10）
- `MarkdownRenderModel.toc: List<TocEntry>`、`MarkdownFrontmatter` 块类型。
- `PMarkdownToc` 组件。

### 阶段 E：扩展语法（P2-11..15）
- Footnote / Admonition / Definition list / Math 占位节点 + 渲染 slot。

### 阶段 F：测试基础设施修复 + 增量解析（P3-16,17,18）
- 修复 Android commonTest 资源；引入块级增量解析。

---

## 本次实施范围（阶段 A）

**目标**：把 `PMarkdownEditor` 从"裸文本框"升级为具备格式化工具栏的可用编辑器，逻辑可单测、不破坏现有 API。

**交付物**：
1. `MarkdownToolbarActions.kt`（commonMain，纯函数 + 默认 toolbar 配置）
2. `MarkdownFormatToolbar.kt`（composable）
3. `PMarkdownEditor` 接入工具栏、Tab 缩进、回车续行
4. 单元测试 + 现有测试全绿
