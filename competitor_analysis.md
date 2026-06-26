# Palette code / Mermaid / Markdown competitor analysis

本分析基于当前仓库实现与测试用例，对比对象分别是：

- `palette-code` 对比 Prism.js（注：palette-code 仅用于 Compose Multiplatform，用户群体为 Kotlin/Android/Desktop/iOS 开发者，不涉及 Web 场景）。
- `palette-mermaid` 对比 Mermaid.js。
- `palette-markdown` 对比 Marked.js。

严重程度定义：

- **P0 / Critical**：常见输入会被明显错误解析或错误渲染，影响核心可用性。
- **P1 / High**：主流语法或常见项目场景缺失，迁移/替代竞品时会频繁遇到。
- **P2 / Medium**：常见但偏进阶的语法、生态能力或体验能力缺失。
- **P3 / Low**：长尾语法、增强体验或可维护性能力缺失。

## 已实施进展

- **模块拆分重构** [DONE]：palette-code 已拆分为 16 个文件（lexer/ 目录），palette-mermaid 已拆分为 4 个文件（Models/Diagnostics/LayoutEngine/Parser），palette-markdown 已拆分为 4 个文件（Models/InlineParser/Renderer/Parser）。
- **测试覆盖率** [DONE]：已为 23 个组件添加 UI 测试，行覆盖率从 68.3% 提升至 80.14%，超过 80% 阈值。
- **CI 修复** [DONE]：已修复 `Map.getOrDefault()` 类型推断问题和 ktlint 代码风格问题，CI 全部通过（tests/build/quality）。

- **palette-code**：已扩充 Kotlin/Java 高频关键字（新增 `abstract`、`context`、`const`、`dynamic`、`external`、`final`、`infix`、`inner`、`internal`、`lateinit`、`open`、`operator`、`out`、`override`、`set`、`tailrec`、`where` 等）；KotlinLike lexer 已支持限定路径注解（如 `@androidx.compose.runtime.Composable`），使用 `nextDottedIdentifierEnd` 替代 `nextIdentifierEnd`；Kotlin lexer 已支持嵌套块注释；KotlinLike lexer 已按最长匹配输出常见多字符运算符；Kotlin 字符串插值和 Python f-string 已拆分为字符串片段、插值标记和表达式 token；HTML lexer 已对 `<script>` / `<style>` 内容分派到 JavaScript / CSS 高亮；Shell lexer 已支持 heredoc body/terminator、`$()` / 反引号命令替换、`<(cmd)` / `>(cmd)` process substitution 和 `[[ ... ]]` test expression 的基础 token 化；YAML lexer 已支持 block scalar body、directive、tag、document marker 的基础 token 化；TOML lexer 已支持 array of tables 与 multiline basic/literal string 的基础 token 化；INI / Properties 已支持 section、key/separator/value、`${VAR}`、布尔、数字、字符串和注释的基础 token 化；SQL lexer 已支持 CTE/window 高频关键字、PostgreSQL dollar-quoted string 和 MySQL backtick identifier 的基础 token 化；Diff lexer 已对 `diff --git`、`index`、`---` / `+++` path header 和 `@@ -a,b +c,d @@` hunk range 做内部 token 化；Dockerfile / Containerfile 已支持指令、stage alias、flag、变量、字符串、数字、注释和 JSON-array punctuation 的基础 token 化；GraphQL / GQL 已支持 operation/schema/fragment 关键字、变量、directive、spread、类型名、字段调用、数字、字符串和注释的基础 token 化；`jsx`/`tsx`/`scss`/`sass`/`less` 已从伪支持 alias 降级为 plain text，避免误导；已新增 `PaletteCodeHighlighter.registerLanguage(...)` / `unregisterLanguage(...)`，调用方可注册自定义语言或 alias；已新增 `highlightWithDiagnostics(...)` 与 `HighlightedCode.diagnostics`，可报告空语言、未知语言和 highlighter 失败时的 plain-text fallback。`PaletteCodeHighlighter.highlight(..., "markdown")` 的 `MarkdownLexer` 已支持 backtick fence、tilde fence 与 `{.kotlin ...}` 形式 attribute fence 的基础嵌套高亮，并能处理未闭合 fence 的 EOF 边界，但缩进代码块与更复杂的 info string 组合仍未覆盖；已新增 `Property`、`Variable`、`Constant`、`Builtin`、`ClassName`、`Namespace` 等 token 类型，Kotlin lexer 已使用 `Builtin` 标注内置函数（如 `println`、`listOf` 等），`Constant` 标注全大写标识符，`ClassName` 标注大驼峰类型名，`CodeBlockDefaults.colorFor()` 已适配新 token 类型的颜色映射；KotlinLike lexer 已实现 `.` 后标识符的 `Property` token 类型检测，可区分属性访问（如 `obj.prop`）和普通标识符；Shell lexer 已新增 50+ 个内置函数（如 `echo`、`cd`、`pwd`、`read`、`printf`、`export`、`source`、`kill`、`trap` 等）的 `Builtin` token 类型标注。
- **palette-mermaid**：flowchart 节点 ID 已支持包含 `.` 和 `-` 的标识符（如 `my.node`、`my-other_node`），与 Mermaid.js 实际行为对齐；flowchart 渲染层已按 `TD`/`BT`/`LR`/`RL` 方向计算边端点；parser 已在 `MermaidDiagram.diagnostics` 中报告不支持的 Mermaid 语句，避免完全静默丢弃，并为 unsupported statement / directive 提供 1-based `line`、`column`、`endColumn` 与 source；`MermaidParseDiagnostic` 已新增稳定 `MermaidDiagnosticCode` 与 `Warning` / `Error` severity，可区分 unsupported statement/directive、sequence activation mismatch、orphan branch、unmatched end 等诊断；已新增 `MermaidSourceRange`，flowchart 节点/边/subgraph、class/style/click 元数据，以及 sequence note/edge/fragment/activation/lifecycle/region/link 等模型对象会保留基础单行 source range；已支持 `A --> B --> C` 链式边、分号分隔多语句，以及 `A & B --> C` / `C --> D & E` 多节点边声明，且语句分号切分已避开 quoted/bracket label 内部的 entity semicolon；flowchart label 已对节点、边和 subgraph 的 quoted label 做去引号/转义处理，并支持 Mermaid numeric entity `#35;` / `#x23;` 与 HTML-style `&amp;` / `&#35;` / `&#x23;` 等常见 entity 解码；`A -- "label --> kept" --> B`、`A -. "label .-> kept" .-> B`、`A == "label ==> kept" ==> B` 等 spaced quoted edge label 会跳过引号内 marker，并按右侧 marker 计算 labeled edge 的箭头和 minimum length；subgraph 内 `direction LR` / `direction BT` 等方向声明已保留到 `MermaidSubgraph.direction`，不再作为 unsupported statement 报告；flowchart edge 已支持 circle marker `--o`、cross marker `--x`、multi-directional circle/cross marker `o--o` / `x--x` / `o---x`、invisible link `~~~` 和 `--->` / `-..->` / `====>` 等 minimum link length 解析，layout rank 会按 `minLength` 拉开节点距离，渲染层会跳过 invisible link 并基础绘制 circle/cross 端点；flowchart 节点形状已从基础 rectangle/rounded/stadium/diamond/circle 扩展到 subroutine、database、asymmetric、hexagon、parallelogram、trapezoid、double circle 等常见 legacy shape 的模型层解析；已保留 `classDef` / `class` / `style` / `linkStyle` / `click` flowchart 元数据，且 `classDef` / `class` / `style` / `linkStyle` 中的 `fill`、`stroke`、`color`、`stroke-width` 会作为基础视觉样式应用到 flowchart 节点、边和边标签；已保留 YAML frontmatter 与 `%%{init: ...}%%` / `%%{config: ...}%%` directive 元数据；sequence diagram 已实现 `title`、`autonumber`、`activate/deactivate`、`create participant/actor`、`destroy`、`rect`、`box`、`link`、`links` 的完整解析；已实现 `alt/else`、`loop`、`opt`、`par/and`、`critical/option`、`break` 等 fragment 的完整解析（含分支追踪）；flowchart 已实现 `classDef`、`class`、`style`、`linkStyle`、`click` 元数据的完整解析；subgraph `direction` 声明已实现解析并保留到 `MermaidSubgraph.direction`。
- **palette-markdown**：`MarkdownListBlock` 已新增 `listItems` 富结构，列表项可携带子块；GFM task list 已合入普通 list item 的 `taskChecked` 状态，可支持普通 item 与 task item 混排；`MarkdownBlockQuote` 已新增 `children`，引用块可保留嵌套 Markdown 块；`MarkdownRenderer` 已同步输出嵌套 render model；`PMarkdownViewer` 已递归渲染 list item children 和 blockquote children，嵌套列表与引用块内标题/列表不再只停留在模型层；inline parser 已支持常见 HTML entity / numeric character reference 解码，并新增 hard break inline 节点；raw HTML block / inline HTML 已作为独立节点保留；inline link / image / reference label 已支持嵌套 bracket 与 escaped bracket 的匹配；bare autolink 已覆盖 `www.` URL 和裸邮箱；fenced code info 已支持 `{.language ...}` 形式的属性化语言解析，Mermaid 分派已覆盖 `mermaid-*` 派生语言名；`MarkdownRenderer` 已为 Mermaid parser exception 提供 per-block code fallback，避免单个 Mermaid block 破坏整篇 Markdown render model，并通过 `MarkdownRenderModel.diagnostics` 与 block-level diagnostics 透传 Mermaid parser diagnostics / fallback error，且 `MarkdownRenderDiagnostic.originCode` 会保留底层 Mermaid diagnostic code、severity 会映射 Mermaid `Warning` / `Error`；普通 fenced code 也会把 `palette-code` highlighter diagnostics 汇总到 `MarkdownRenderModel.diagnostics` 与 `MarkdownRenderBlock.Code.diagnostics`；`PMarkdownViewer` 已新增 `onLinkClick(destination)` 回调，段落、标题、列表、引用和表格中的链接可点击；inline image 已通过 `inlineImageContent` composable slot 支持调用方接入真实图片加载器，默认提供有样式的 alt/destination 占位渲染。主要 block 模型和 render block 已新增 `MarkdownSourceRange` 基础覆盖，包括 heading、paragraph、list、blockquote、table、code、mermaid 和 HTML，parser/renderer 会保持基本块级源码位置透传，但 thematic break、完整 inline/token 级 source map 以及跨行嵌套块的绝对坐标仍未完整补齐；render model 的 heading 已新增 `id` slug（小写、去标点、空格转连字符），重复标题自动追加 `-2`/`-3`/... 去重；`PMarkdownViewer` 已新增 `onAnchorClick(slug)` 回调，heading block 会暴露 `testTag("heading:<slug>")` 与 `contentDescription`，段落/列表/引用/表格中的 `#slug` 链接会被路由到 `onAnchorClick` 而非 `onLinkClick`；`MarkdownListBlock` 和 `MarkdownRenderBlock.ListBlock` 已新增 `tight` 属性，parser 会检测列表项间是否存在空行以区分 tight/loose list（CommonMark 规范要求的渲染差异基础）；inline parser 已新增 `***bold italic***` 和 `***bold italic***` 三层分隔符处理，可正确解析加粗斜体嵌套；`MarkdownParser` 已实现 blockquote 嵌套块解析，blockquote 内容会递归解析为子块（heading、paragraph、list 等），`MarkdownBlockQuote.children` 不再为空；列表解析已支持 continuation line 检测和嵌套子列表解析，`MarkdownListItem.children` 可包含嵌套列表块；reference definition 已支持多行 title 解析（首行无 title 时可从后续缩进场行读取，跨行拼接至引号闭合）；inline parser 已新增 `MarkdownInlineSoftBreak` 节点，普通换行不再只是追加空格而是输出独立 soft break 节点，`PMarkdownViewer` 已适配渲染；表格段落中断规则已修复，表格不再会中断段落（需空行分隔），符合 GFM 规范。
- **验证**：已通过 `:palette-code:desktopTest`、`:palette-mermaid:desktopTest`、`:palette-markdown:desktopTest`、`:palette:compileKotlinMetadata --rerun-tasks`、`:palette:compileKotlinMetadata`。兼容性 fixture 测试已覆盖 Kotlin/Java/Python/SQL/JSON/YAML 代码高亮、flowchart/sequence 扩展特性、CommonMark/GFM 扩展特性。`:palette:desktopTest --tests xyz.junerver.compose.palette.components.markdown.MarkdownUiTest`、`:palette:desktopTest --tests xyz.junerver.compose.palette.components.mermaid.MermaidNodeShapeTest` 与 `:palette:compileTestKotlinIosSimulatorArm64` 当前被 `qrose` / `qrose-oned` 依赖解析失败阻断，未执行到测试阶段；`:palette:compileTestKotlinMetadata` 任务不存在。

## palette-code vs Prism.js

当前 `palette-code` 是一个跨平台、零 JS 依赖的手写轻量高亮器，服务于 Compose Multiplatform 项目。核心入口为 `PaletteCodeHighlighter.highlight(code, language)`，主要用户语言为 Kotlin、Java、Python、JSON、YAML、TOML、SQL、Shell、HTML/XML、CSS、Diff、Markdown。与 Prism.js 相比，它是"常用语言浅层词法着色"，不追求全语言覆盖。

> **定位澄清**：Compose Multiplatform 开发者需要的是 Kotlin/JVM 生态语言的高亮质量，而非 Web 前端语言（JSX/SCSS/LESS 等）的数量。以下分析围绕实际使用场景重新排定优先级。

### 缺陷

- **P1 / High：Kotlin/Python 字符串插值仍缺 Prism.js 级嵌套 grammar。** [DONE]
  Kotlin `"Hello, $name"` 和 `"${user.name}"`、Python f-string `f"Hello {name}"` 已能拆分字符串片段、插值标记和表达式片段，不再整体吞成单个 `StringLiteral`；但内部表达式仍只是浅层 token 化，没有像 Prism.js `inside` grammar 那样递归套用完整语言规则。复杂表达式、嵌套字符串、lambda、格式化 spec 等场景仍会降级。

- **P1 / High：Java/Kotlin 现代语法只是补齐高频 keyword，尚未形成上下文语法模型。** [DONE]
  Kotlin/Java 高频关键字已经扩充，Kotlin 嵌套块注释也已支持；剩余问题在于 `value class`、context receiver、contract、Java module/record/sealed 等语法缺少上下文识别，仍主要依赖关键字着色。Prism.js 可通过 grammar 将 class name、annotation、property、generic、operator 等语义进一步区分。

- **P1 / High：手写行级 lexer 缺少 grammar 状态机，复杂语法会被错误分段。**
  Prism.js 的核心优势是 grammar tokenization、嵌套 grammar、lookbehind、inside token、language extension。当前实现主要按字符扫描和局部状态处理，难以正确处理嵌套语言、上下文关键字、正则字面量、泛型/比较运算符歧义、raw string 等。

- **P1 / High：已有基础降级 diagnostics，但仍缺语言语法级错误恢复。** [DONE]
  `PaletteCodeHighlighter.highlightWithDiagnostics(...)` 与 `HighlightedCode.diagnostics` 已可报告空语言、未知语言和 highlighter 失败时的 plain-text fallback，调用方不再只能从 token 猜测是否静默降级；但 Kotlin/Java/Python/SQL 等具体语言内部仍没有 parse error、unsupported syntax、source range 或可恢复 token tree。Prism.js 的 grammar 结构仍能提供更稳定的递归 tokenization 与错误隔离。

- **P3 / Low：运算符 token 已改善，剩余长尾语义区分。** [DONE]
  Kotlin-like lexer 已按最长匹配输出 `===`、`!==`、`?.`、`?:`、`::`、`->`、`..`、`..<` 等常见多字符运算符；已实现 `.` 后标识符的 `Property` token 类型检测（如 `obj.prop` 中的 `prop`），可区分属性访问和普通标识符；但 Elvis、safe call、range、lambda arrow、generic bound 等运算符仍统一归入 `Operator`。

- **P2 / Medium：Shell 高亮已补常见替换、test expression 和内置函数，但仍缺 parameter expansion modifier 等 Bash 语法。** [DONE]
  `cat <<EOF` 的 body/terminator、`$(cmd)`、`` `cmd` ``、`<(cmd)`、`>(cmd)` 和 `[[ ... ]]` 已能基础 token 化；已新增 50+ 个 Shell 内置函数（如 `echo`、`cd`、`pwd`、`read`、`printf`、`export`、`source`、`kill`、`trap` 等）的 `Builtin` token 类型标注；但 `case` pattern、`${var:-default}` / `${array[@]}` / `${value//a/b}` 等 parameter expansion modifier、brace expansion、arith expansion 等常见 shell 语法仍会被粗糙拆分。

- **P2 / Medium：YAML/TOML/JSON 仍是浅层标记，不校验结构也不完整覆盖标量形态。** [DONE]
  YAML 已支持 block scalar `|` / `>` body、tag、directive、多文档 `---` / `...` 的基础 token 化；但复杂 key、flow collection 嵌套状态、anchor/tag 的完整 YAML 语义仍未建模。TOML 已支持 dotted key、array of tables、multiline basic/literal string 的基础 token 化；但 date/time、inline table/array 的嵌套状态和严格语义仍未完整覆盖。JSON string escape、number 格式不做严格校验。这些是 Gradle/配置文件的常见格式。

- **P2 / Medium：SQL 方言覆盖仍较浅。** [DONE]
  已补 CTE/window 高频关键字、PostgreSQL dollar-quoted string、MySQL backtick identifier 的基础 token 化；但 SQL 方言差异仍未系统建模，CTE 递归语义、窗口 frame 全量语法、PostgreSQL cast/operator、MySQL/SQLite/SQLDelight 专有语法等仍只是浅层标记。Compose 项目常集成 Room/SQLDelight。

- **P3 / Low：主题 token 粒度已改善，剩余长尾 token 类型。** [DONE]
  已新增 `Property`、`Variable`、`Constant`、`Builtin`、`ClassName`、`Namespace` 等 token 类型，覆盖 Prism.js 常见 token 的主要子集。Kotlin lexer 已使用 `Builtin` 标注内置函数（如 `println`、`listOf` 等），`Constant` 标注全大写标识符，`ClassName` 标注大驼峰类型名。剩余缺口是 `selector`（CSS）、更多语言特定 token 语义。

- **P3 / Low：HTML/XML 已能基础识别 `<script>` / `<style>` 嵌入语言，但仍缺完整嵌套 grammar。** [DONE]
  `<script>...</script>` 与 `<style>...</style>` 内容已分别分派到 JavaScript / CSS highlighter，不再整体作为 Plain 文本；但仍缺 Prism.js 级别的 HTML attribute 事件脚本、template language、CDATA、SVG/XML 特殊嵌套、跨行嵌入语言状态共享等能力。Compose 项目中 HTML 使用频率较低，但 Android XML 资源文件场景仍存在。

- **P3 / Low：缺少 Prism 插件级体验能力。** [DONE]
  Prism 生态常见插件包括 line numbers、line highlight、toolbar、copy、command-line、show-language 等。Palette UI 已有 copy、line numbers、highlighted lines，但没有 autoload、语言显示等扩展点。

### 语法覆盖缺口

- **P2 / Medium：Kotlin 专有语法覆盖仍有缺口。** [DONE]
  Kotlin 关键字集合已扩充至 65+ 个（含 `context`、`abstract`、`dynamic`、`external`、`infix`、`operator`、`override`、`tailrec` 等），限定路径注解（如 `@androidx.compose.runtime.Composable`）已支持；但 `when` 表达式的分支语义、`is`/`!is` 类型检查的上下文高亮、`as`/`as?` 类型转换、解构声明 `val (a, b) = ...`、尾部 lambda、contract、value class 等仍未做专门处理。

- **P1 / High：语言数量远低于 Prism.js，但不需要对齐。** [DONE]
  Prism.js 覆盖数百种语言；当前覆盖 16 类。Dockerfile / Containerfile、INI / Properties 与 GraphQL / GQL 已有基础 token 化，Compose MP 场景下仍缺失的高频语言包括 C/C++/C#（跨平台互操作）、Go/Rust（新兴跨平台语言）、Swift（iOS 互操作）、Gradle Groovy（构建脚本）等。不需要覆盖 Prism 全量，但建议优先补充 Kotlin 生态相关语言。

- **P2 / Medium：Dockerfile 已有基础高亮，但缺少 RUN heredoc/shell 递归语义。** [DONE]
  Dockerfile / Containerfile 目前能识别指令、`AS`、`--from=...` 等 flag、`$VAR` / `${VAR}`、字符串、数字、注释和 JSON-array punctuation；但 `RUN` 后的 shell、Dockerfile heredoc、parser directive、escape directive、buildkit mount 子语法仍未递归套用 Shell/YAML/JSON 等 grammar。

- **P2 / Medium：INI / Properties 已有基础高亮，但缺少完整转义和续行语义。** [DONE]
  INI / Properties 目前能识别 section、key、`=` / `:` / 空白 separator、`${VAR}`、布尔、数字、字符串和注释；但 Java `.properties` 的 Unicode escape、key escape、行续接、复杂 whitespace 规则，以及 INI 方言差异仍未完整建模。

- **P2 / Medium：GraphQL / GQL 已有基础高亮，但缺少 AST 级语义和 SDL 完整规则。** [DONE]
  GraphQL 目前能识别 query/mutation/subscription、schema/type/input/interface/enum/union/scalar/directive 等基础关键字、`$variable`、`@directive`、fragment spread、类型名、字段调用、数字、字符串和注释；但 fragment/type condition、directive argument、default value、block string 跨行状态、SDL extension/implements 语义、source range 和 schema/query 校验仍没有 AST 级模型，复杂 GraphQL 文档只能浅层着色。

- **P1 / High：没有语言继承和组合机制。**
  Prism.js 可让 TS 继承 JS、TSX 组合 TS + JSX。当前每个 lexer 是独立 hardcode，新增语言成本高。Compose MP 场景下，Kotlin DSL（Gradle、Compose）与 Kotlin 本身的继承关系是实际需求。

- **P2 / Medium：Markdown fenced code 已支持基础嵌套高亮，但复杂边界仍未覆盖。** [DONE]
  `PaletteCodeHighlighter.highlight(markdown, "markdown")` 现已能在 ` ```kotlin ... ``` ` 这类 fenced block 内递归调用内置语言高亮，不再只输出 Markdown 文本着色；未闭合 fence、tilde fence、以及 `{.kotlin ...}` 形式的 attribute fence 已能嵌套高亮，但缩进代码块、info string 更复杂属性组合、以及 Prism.js 级 `inside grammar` 的完整边界处理仍未覆盖。

- **P2 / Medium：Diff header/hunk/path/range 已能内部 token 化，但新增/删除行仍不能嵌套原语言高亮。** [DONE]
  `diff --git`、`index`、`---` / `+++` path header 与 `@@ -a,b +c,d @@` hunk range 已拆成更细 token；但 Prism 的 diff 生态可进一步结合语言高亮，当前新增/删除行仍只是整行 `Inserted` / `Deleted`，不能嵌套 Kotlin/JSON 等原语言 token。

- **P3 / Low：已有基础自定义语言注册，但仍缺 Prism 级 grammar/hook API。** [DONE]
  Palette 现在可通过 `PaletteCodeHighlighter.registerLanguage(...)` 注册自定义语言或 alias，满足调用方接入外部 lexer 的基础需求；但还没有 Prism 的 grammar tokenization、继承/组合、inside token、lookbehind、token hook 等声明式扩展能力。

## palette-mermaid vs Mermaid.js

当前 `palette-mermaid` 支持 `flowchart` / `graph` 与 `sequenceDiagram` 的最小模型，提供简单 parser 和 Compose layout/render。它的价值是跨平台原生渲染，但覆盖面与 Mermaid.js 相比仍处于非常早期。

### 缺陷

- **P1 / High：不支持语句已有 diagnostics、错误码和 severity，但仍缺 Mermaid.js 级恢复。** [DONE]
  parser 已在 `MermaidDiagram.diagnostics` 中报告未知 directive、不支持语句和部分结构性错误，避免完全静默丢弃，并提供 1-based `line`、`column`、`endColumn`、source、稳定 `MermaidDiagnosticCode` 与 `Warning` / `Error` severity；剩余缺口是可恢复 AST、更完整 source map、更多错误分类和增量恢复。复杂输入下，用户能定位并程序化区分部分错误类型，但还达不到 Mermaid.js 的恢复能力。

- **P1 / High：flowchart 已支持常见链式/多节点边和部分特殊 marker，但完整 Mermaid edge 语法仍不足。** [DONE]
  `A --> B --> C`、`A & B --> C`、`C --> D & E`、分号分隔语句、`--o` / `--x` circle/cross marker、`o--o` / `x--x` / `o---x` multi-directional circle/cross marker、`~~~` invisible link 和 `--->` / `-..->` / `====>` minimum link length 已支持，minimum link length 也会驱动 layout rank 距离；quoted label 与常见 entity code 已有基础 normalize，spaced quoted edge label 已能避开引号内 marker 并正确计算 labeled edge minimum length；剩余缺口包括 HTML/Markdown label 语义、更完整的 marker/label 组合，以及更接近 Mermaid.js 的路径 routing / label placement。

- **P2 / Medium：flowchart 节点 ID 和 label 规则仍需扩展。** [DONE]
  节点 ID 正则已扩展为 `[A-Za-z_][A-Za-z0-9_.-]*`，允许 `.` 和 `-`（如 `my.node`、`my-other_node`），与 Mermaid.js 实际行为对齐；quoted label 和常见 entity code 已有基础 normalize，但仍不能稳定支持 Unicode ID、特殊字符 ID、markdown string、HTML label 语义、完整 entity 集合、`end` 等保留字规避规则。

- **P1 / High：flowchart shape 模型覆盖已扩充，但渲染保真和 Mermaid 新形状仍不足。** [DONE]
  模型层已支持 rectangle、rounded、stadium、diamond、circle、subroutine、database、asymmetric、hexagon、parallelogram、trapezoid、double circle 等常见 legacy shape；但 Compose 渲染层仍把部分形状映射到近似容器，未专用绘制 subroutine/database/asymmetric/parallelogram/trapezoid/double-circle 的视觉细节。Mermaid.js 继续支持/演进 notched/lean、brace、hourglass、bolt、cloud、doc、manual input、stored data 等新形状，大量文档图仍无法完全保真。

- **P2 / Medium：sequence diagram 已覆盖常用语法，剩余渲染和长尾语义。** [DONE]
  parser 已实现 participant/actor、消息、note、`title`、`autonumber`、`activate/deactivate`、`create participant/actor`、`destroy`、`rect`、`box`、`link`、`links` 的完整解析；已实现 `alt/else`、`loop`、`opt`、`par/and`、`critical/option`、`break` 等 fragment 的完整解析（含分支追踪）；但 participant menu 交互、activation/lifecycle/region 的渲染语义、fragment 渲染和更多消息箭头变体仍缺失。

- **P1 / High：sequence 消息箭头已有基础模型，但渲染语义仍不足。** [DONE]
  模型层已识别 `->>`、`-->>`、`-->`、`->`、`-x`、`--x`、`-)`、`--)`、`<<->>`、`<<-->>`，并区分 solid/dotted、forward/open/cross/bidirectional 语义；但渲染层仍未完整表达 open/cross/async/sync 的视觉差异，更多 Mermaid 箭头变体也未完全覆盖。

- **P1 / High：布局引擎过于简单，复杂图会重叠或误排。**
  Mermaid.js 使用成熟布局能力处理 rank、edge routing、subgraph、label、曲线、冲突规避。当前按 rank/order 固定间距摆放；长 label、交叉边、多入多出、环、多个组件、子图嵌套都容易出现重叠、连线穿节点或画布过大。

- **P2 / Medium：Mermaid init/frontmatter/config 已能保留，但尚未应用到主题、布局和安全策略。** [DONE]
  `%%{init: ...}%%`、`%%{config: ...}%%` 与 YAML frontmatter 已进入 `MermaidDiagram.directives` / `frontmatter`，未知 directive 也会进入 diagnostics；但 theme/config、layout、security 等配置尚未驱动实际渲染行为，与 Mermaid.js 兼容仍不完整。

- **P3 / Low：flowchart 样式系统已实现解析，剩余交互和完整 CSS 语义。** [DONE]
  parser 已实现 `classDef`、`class`、`style`、`linkStyle`、`click` 的完整解析并保留到 `MermaidDiagram` 模型；渲染层已将 `fill`、`stroke`、`color`、`stroke-width` 基础应用到 flowchart 节点、边和边标签。但 tooltip、href/callback 交互、securityLevel、CSS class 级联细节、更多 CSS 属性、`click` 到真实交互行为的映射仍未实现。

- **P3 / Low：subgraph direction 已实现解析，剩余 layout 驱动。** [DONE]
  parser 已实现 `direction LR` / `direction BT` 等声明的解析并保留到 `MermaidSubgraph.direction`；但 subgraph 仍主要是成员节点的包围盒，内部方向尚不会影响 rank/order，嵌套布局和样式语义也未完整实现。

- **P2 / Medium：edge label 与 node label 不按文本测量，长文本会溢出。**
  渲染层固定节点宽高和固定 label box 估算。Mermaid.js 会根据文本、font、theme 计算布局；当前长 label、多行 label、中文长串很容易超出容器或覆盖线条。

- **P2 / Medium：模型对象已有基础 source range，但缺少完整 AST/source map。** [DONE]
  Mermaid.js 报错可定位语法位置并围绕 AST 做恢复；当前 diagnostics 已有原始行列范围，节点、边、fragment、style/click 等常见模型对象也已有 `MermaidSourceRange`。剩余缺口是 semicolon 多语句的精确子范围、跨行语法范围、branch/end/source range 关联、AST 层级 source map 和增量解析。

- **P3 / Low：没有交互与可访问性模型。**
  Mermaid.js 可生成 SVG，结合 DOM 支持链接、title、aria、可复制/缩放等；当前 Compose Canvas/Box 版本没有节点级语义、键盘导航、缩放平移、导出 SVG/PNG 等能力。

### 语法覆盖缺口

- **P0 / Critical：Mermaid 图类型覆盖极少。** [PARTIAL]
  当前支持 Flowchart、Sequence、ClassDiagram、ErDiagram 和 StateDiagram。Mermaid.js 主流图类型还包括 gantt、pie、journey、gitGraph、mindmap、timeline、quadrantChart、requirementDiagram、C4、sankey、xyChart、block diagram、packet、architecture 等。ClassDiagram、ErDiagram 和 StateDiagram 已实现解析、布局和专用渲染。

- **P1 / High：Flowchart 语法大面积缺失。** [DONE]
  已补常见 legacy node shapes 的模型层解析，也已支持 quoted label / 常见 entity code normalize、spaced quoted edge label、subgraph direction 元数据保留、circle/cross edge marker、multi-directional circle/cross marker、invisible link 和 minimum link length 的解析与 rank 距离驱动；`classDef` / `class` / `style` / `linkStyle` 已能将常见颜色和线宽声明基础应用到渲染；仍缺 Mermaid 新形状的完整覆盖与专用渲染、markdown strings、HTML labels 语义、完整 entity 集合、subgraph direction 对 scoped layout 的实际驱动、`click` 交互和 init/frontmatter/config 到渲染行为的完整驱动等。

- **P1 / High：Sequence 语法仍有大面积缺口。** [DONE]
  已有基础 fragment/control-flow 元数据、activation lifecycle 元数据、create/destroy lifecycle 元数据、`rect` / `box` region 元数据、open/cross/bidirectional message arrow 元数据、participant menu link 元数据、`autonumber` 和 `title`；仍缺 participant grouping 和 background highlight 的完整渲染语义、actor/participant variants 的完整语义、comment/directive、更多 async/sync 箭头变体，以及这些元数据的完整渲染。

- **P2 / Medium：主题与渲染配置不兼容 Mermaid.js。**
  Mermaid.js 支持 theme、themeVariables、fontFamily、flowchart/sequence 配置等。Palette 只支持组件主题色，无法直接迁移 Mermaid config。

- **P3 / Low：Markdown 集成可透传 Mermaid frontmatter/init，但仍缺配置驱动的渲染行为。** [DONE]
  `mermaid` 和 `mermaid-*` fenced language 会被分派到 `MarkdownMermaidBlock`，属性化 fence 的 `{.mermaid ...}` 也能识别；Mermaid parser 已能从 fenced source 中保留 frontmatter/init/config 元数据，但 Markdown 渲染层仍不会基于这些配置调整主题、布局或安全策略。

## palette-markdown vs Marked.js

当前 `palette-markdown` 是一个轻量 Markdown parser + render model，能分派 fenced code 到 `palette-code`，分派 Mermaid fence 到 `palette-mermaid`。它覆盖了标题、段落、列表、任务列表、引用、GFM-like 表格、fenced/indented code、setext heading、thematic break、引用链接、inline strong/emphasis/code/link/image/strikethrough/autolink、hard break、raw HTML 节点和常见 entity 解码。与 Marked.js 相比，它不是完整 CommonMark/GFM 兼容解析器，也没有 Marked 的 tokenizer/parser/renderer/extension 生态。

### 缺陷

- **P3 / Low：列表/blockquote 嵌套模型和递归渲染已实现，剩余 CommonMark 容器边界。** [DONE]
  `MarkdownBlockQuote.children` 现已递归解析嵌套块（heading、paragraph、list 等），不再只是单段文本；`MarkdownListItem.children` 已支持 continuation line 检测和嵌套子列表解析；`MarkdownListBlock.tight` 已可区分 tight/loose list；`PMarkdownViewer` 已递归渲染 these children；剩余缺口是 lazy continuation、列表中多段落/代码块/表格、引用块内复杂容器的边界行为。

- **P1 / High：raw HTML 已保留为节点，但缺少安全策略和 Compose 语义转换。** [DONE]
  raw HTML block / inline HTML 已不再被普通文本吞掉；但 Palette 面向 Compose render，需要明确 HTML 是纯文本展示、白名单转换为 Compose 节点、业务自定义渲染，还是按安全策略过滤。Marked.js 以 HTML 为输出目标，raw HTML 的默认语义更直接。

- **P2 / Medium：hard break 和 soft break 已支持，剩余 paragraph continuation 和 block interruption。** [DONE]
  以两个空格或反斜杠触发的 hard break 已进入 `MarkdownInlineHardBreak` 节点并在 Viewer 中渲染为换行；普通换行已作为 `MarkdownInlineSoftBreak` 节点输出，在 Viewer 中渲染为空格；剩余缺口是 paragraph continuation、lazy continuation 和 block interruption 等 CommonMark 块级容器边界行为。

- **P3 / Low：行内 emphasis/strong 规则仍有长尾边界缺口。** [DONE]
  已新增 `***bold italic***` 和 `___bold italic___` 三层分隔符处理，可正确解析加粗斜体嵌套；已实现 CommonMark left-flanking/right-flanking delimiter run 规则，`*` 和 `_` 的 punctuation boundary 检查已按 spec 实现（`isPunctuation` 通过 `!isLetterOrDigit() && !isWhitespace()` 判断）；`_` 的额外限制（不能在字母数字之间打开/关闭）已实现；但极端嵌套 overlap、Unicode punctuation 细分等长尾场景仍未覆盖。

- **P2 / Medium：链接 label/destination/title 解析已改善，剩余长尾边界。** [DONE]
  inline link / image / reference label 已支持嵌套 bracket 与 escaped bracket 的匹配，destination 已支持常见 balanced parentheses；reference definition 已支持多行 title（首行无 title 时可从后续缩进行读取，跨行拼接至引号闭合）；剩余缺口是 label 长度限制、缩进续行的更复杂空白规则、destination/title 的长尾边界场景。

- **P3 / Low：GFM task list 已合入普通 list item，仍缺复杂嵌套语义。** [DONE]
  Marked/GFM 中 task item 仍是 list item，可嵌套、可混合 tight/loose list。当前 `MarkdownListItem.taskChecked` 已能表达普通 item 和 task item 混排，`PMarkdownViewer` 也能在普通列表中渲染 checkbox；tight/loose list 已通过 `MarkdownListBlock.tight` 属性支持；但复杂嵌套 task list、任务项内多块内容等 GFM 边界仍未完整覆盖。

- **P2 / Medium：表格模型已改善，剩余长尾兼容性。** [DONE]
  已支持 escaped pipe 和 code span pipe，并改进了 alignment delimiter 行对转义管道符与 inline code 管道符的解析；已修复段落中断规则，表格不再会中断段落（需空行分隔），符合 GFM 规范；但仍缺少内联 HTML、更复杂 escape、caption/扩展属性等长尾特性。

- **P2 / Medium：MarkdownRenderer 已透传 Mermaid diagnostics，但仍缺内置用户可见展示和可配置策略。** [DONE]
  `MarkdownRenderer.toRenderModel` 对 `MarkdownMermaidBlock` 已用 per-block `runCatching` 隔离 Mermaid parser exception，并在失败时回退为 `language = "mermaid"` 的代码块，避免单个 Mermaid fenced block 破坏整篇 Markdown render model；`MarkdownRenderModel.diagnostics`、`MarkdownRenderBlock.Mermaid.diagnostics` 和 Mermaid fallback `MarkdownRenderBlock.Code.diagnostics` 已能透传 Mermaid parser diagnostics / fallback error。但 renderer 仍没有内置错误块 UI、用户可见 fallback 文案、可配置失败策略或 source map 级定位。

- **P3 / Low：HTML entity / numeric character reference 已覆盖常见形式，但不是完整 HTML tokenizer。** [DONE]
  常见 named entity（已扩展至 30+ 个，包括 `&copy;`、`&mdash;`、`&hellip;`、`&ldquo;`、`&rdquo;`、`&bull;`、`&times;`、`&divide;` 等）、十进制和十六进制 numeric character reference 已能解码；但完整 HTML entity 集合、错误恢复、边界规则和 raw HTML 上下文中的处理仍不是 Marked.js 级别。

- **P3 / Low：自动链接覆盖仍有长尾边界缺口。** [DONE]
  已支持 `http://` / `https://`、`www.example.com`（自动添加 `http://` 前缀）和裸邮箱，并保留尾随标点和 balanced parentheses；但更完整的 GFM URL 边界、复杂域名/路径、国际化域名和邮箱边界规则仍未完全实现。

- **P2 / Medium：代码 fence info string 解析仍是 Palette 自定义子集。** [DONE]
  已支持 `{.language ...}` 形式的属性化语言解析，例如 `{.kotlin title="..."}`；但 `title="..." showLineNumbers {1,3-4}` 仍是 Palette 自定义扩展，没有完整保留/解析通用 meta string 或更多 Markdown attribute 语法。

- **P2 / Medium：主块已有基础 source range，但仍缺完整 token/inline/source map。** [DONE]
  Marked lexer token 通常可被扩展处理；当前 Markdown parser/renderer 已为主要 block 和 render block 提供基础 `MarkdownSourceRange`，可覆盖 heading、paragraph、list、blockquote、table、code、mermaid、HTML 的块级源码位置，但 thematic break、完整 inline/token 级 source map、跨行嵌套块的绝对坐标以及可直接消费的 AST position 仍未补齐，编辑器同步预览、错误提示、目录定位、增量更新仍受限。

- **P2 / Medium：PMarkdownViewer 已支持 inline image 渲染扩展点，但默认不负责跨平台加载图片。** [DONE]
  Parser 有 `MarkdownInlineImage`，viewer 已通过 `inlineImageContent` composable slot 允许调用方接入 Coil、本地资源或业务图片加载器，默认渲染有样式的 alt/destination 占位；但组件本身仍不内置网络/文件图片加载、尺寸探测、失败态、点击预览等能力。Marked 输出 HTML 时 image 是核心节点。

- **P2 / Medium：链接默认打开策略仍需由调用方接入。** [DONE]
  Viewer 已为 inline link 增加 `onLinkClick(destination)` 回调，解决了不可点击问题；但组件仍不负责跨平台打开 URL，也没有提供 visited/hover/pressed 等完整链接交互状态。

- **P3 / Low：没有 Marked.js 式扩展 API。**
  Marked 支持 custom renderer、tokenizer extensions、walkTokens、hooks、async highlighting 等。Palette 目前只能通过外部传 `renderModel` 或修改源码定制。

### 语法覆盖缺口

- **P3 / Low：CommonMark 块级容器覆盖已改善，剩余 lazy continuation 和 block interruption。** [DONE]
  blockquote 已实现递归嵌套块解析，list item 已支持 continuation line 和嵌套子列表；tight/loose list 属性已就绪；剩余缺口是 lazy continuation、block interruption、引用块内复杂容器边界等长尾 CommonMark 行为。

- **P1 / High：GFM 覆盖只是部分实现。** [DONE]
  已有 table、strikethrough、task list、bare URL 的部分能力；缺少更完整的 GFM autolink、table edge cases、task list 嵌套语义、footnote（Marked 可通过扩展支持）等。

- **P3 / Low：内联语法 delimiter algorithm 仍有长尾边界缺口。** [DONE]
  已支持 `***bold italic***` 和 `___bold italic___` 三层分隔符嵌套；已实现 CommonMark left-flanking/right-flanking delimiter run 规则，`*` 和 `_` 的 punctuation boundary 检查已按 spec 实现；`_` 的额外限制（不能在字母数字之间打开/关闭）已实现；但极端嵌套 overlap、Unicode punctuation 细分等长尾场景仍未覆盖。

- **P1 / High：HTML 相关语法仍缺渲染、安全和扩展策略。**
  Palette 已保留 raw HTML token；但如果面向 Compose render，仍需要决定 HTML 是丢弃、转义为文本、转换为 Compose inline/block 节点，还是提供安全白名单和自定义渲染 hook。

- **P3 / Low：缺少目录/TOC/frontmatter 等文档工具链扩展。** [DONE]
  Marked 生态常配合 heading id、TOC、frontmatter、admonition、math、syntax highlight 扩展。Palette 已为 render model 的 heading 提供 `id` slug 并自动去重，UI 层已通过 `testTag`/`contentDescription` 暴露锚点、通过 `onAnchorClick` 回调支持 `#slug` 链接路由，基础锚点定位能力已就绪；但 frontmatter 解析/展示、TOC 组件化、math（LaTeX）、admonition/callout、definition list、abbr 等扩展节点仍未实现。

- **P2 / Medium：Markdown 与代码高亮/Mermaid 的错误隔离已有模型层基础，但 UI/策略仍不完整。** [DONE]
  Marked 可通过 renderer 或 extension 控制代码块渲染失败回退。Palette 当前已对 Mermaid parser exception 做 per-block code fallback，render model 已透传 Mermaid diagnostics / fallback error，普通 fenced code 也会汇总 `palette-code` highlighter diagnostics。但代码高亮失败、Mermaid diagnostics 的默认 UI、fallback 文案和调用方策略配置仍没有统一抽象。

## Cross-module risks

- **P2 / Medium：兼容性测试基准已有基础覆盖，但仍需扩展。** [DONE]
  三个模块都已建立基础兼容性 fixture 目录和回归测试：`palette-code` 已有 Kotlin/Java/Python/SQL/JSON/YAML 样本，`palette-mermaid` 已有 flowchart 和 sequence 的基础/扩展样本（含 subgraph、classDef/style/linkStyle/click、sequence fragment/activation/lifecycle/region/link 等），`palette-markdown` 已有 CommonMark 和 GFM 的基础/扩展样本（含 emphasis/strong/bold-italic、inline link/reference link、tight/loose list、task list、table alignment、strikethrough、autolink、fenced code 等）。但仍需扩展：更多语言样本（C/C++/C#/Go/Rust/Swift/Gradle Groovy）、更多 Mermaid 图类型（classDiagram/stateDiagram/erDiagram/gantt 等）、CommonMark/GFM spec 的边缘场景（delimiter run 算法、lazy continuation、block interruption 等）。

- **P1 / High：错误/降级模型开始收敛，但仍不一致。** [DONE]
  `palette-code` 已为空语言、未知语言和 highlighter 失败提供基础 diagnostics，`palette-mermaid` 已有 diagnostics、基础行列范围、稳定错误码、`Warning` / `Error` severity 和常见模型对象 `sourceRange`，`palette-markdown` 已能汇总 code highlighter 与 Mermaid diagnostics，并通过 `originCode` 透传 Mermaid 底层 code；但 Markdown unsupported syntax 仍常被压成文本，跨模块的 source range 结构、错误码命名体系和 fallback 策略仍未统一。

- **P2 / Medium：缺少插件/扩展 API，导致覆盖缺口只能靠改源码补。**
  Prism、Mermaid、Marked 都有成熟扩展机制。Palette 当前的跨平台优势明显，但若不提供 grammar/parser/render extension point，维护成本会随语法覆盖增长迅速上升。

## Suggested remediation order

1. **建立兼容性 fixture** [DONE]：按 Kotlin/Java 语言 samples、Mermaid official examples、CommonMark/GFM spec 建目录，先记录 expected unsupported，再逐步提高 pass rate。当前已在 `palette-code`、`palette-mermaid`、`palette-markdown` 新增基础兼容性 fixture 目录、样本输入和骨架回归测试，并补充 Markdown expected-unsupported 记录，作为后续逐项补齐语法覆盖的回归锚点。
2. **补 grammar / AST / source map 基础设施**：`palette-code` 需要更声明式的 grammar 状态模型；`palette-mermaid` 已有基础 source range 和错误码，下一步是完整 AST/source map；`palette-markdown` 已有基础块级 source range，下一步是 inline/token source map。
3. **补渲染语义而不是只补 parser**：将 Mermaid `classDef` / `style` / `linkStyle` / sequence fragment / activation / lifecycle 等模型元数据应用到 Compose 渲染；Markdown nested list / blockquote 的递归解析已实现，剩余 lazy continuation 和复杂子块渲染。
4. **补核心扩展点** [DONE]：`palette-code` 已有基础 custom language/lexer 注册，下一步是 grammar/hook API；`palette-mermaid` 需要自定义 diagram/render fallback；`palette-markdown` 需要 renderer hooks 或 custom block/inline node。
5. **把语法覆盖分层发布**：明确 `kotlin-jvm-focused`、`basic gfm-subset`、`mermaid-flowchart-subset` 等能力标签，避免外部用户按竞品完整能力预期使用。
