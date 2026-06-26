# palette-code 与 Prism 对齐路线图

> 目标：将 palette-code 从"逐语言手写 scanner"演进到"声明式 grammar + 通用 tokenize 引擎"，
> 对齐 PrismJS 的主要能力（嵌套语法、标准 token 类型、语言可扩展性、pattern 能力）。
> 本文件是进度真相来源，每完成一项即更新状态。

参考：
- [PrismJS/prism 仓库](https://github.com/PrismJS/prism)
- Prism 类型定义 `src/types.d.ts`（`StandardTokenName`、`GrammarToken`、`Grammar`）

---

## 现状评估

palette-code 已覆盖 17 种语言（Kotlin/Java/JS/TS/JSON/CSS/Python/HTML/XML/YAML/TOML/INI/GraphQL/Diff/Markdown/Dockerfile/SQL），渲染层有完整的 `CodeTokenType → Color` 主题映射（派生自 `PaletteTheme`）。

### 与 Prism 的核心差异

| 维度 | palette-code 现状 | Prism 基准 | 差距 |
|------|-------------------|------------|------|
| **tokenization 引擎** | 每语言一个手写逐字符 scanner 类 | 通用 `tokenize(text, grammar)` 引擎 + 声明式 grammar | 🔴 架构性差距：新语言成本高 |
| **grammar 模型** | 无 | `{pattern, lookbehind, greedy, alias, inside}` | 🔴 缺声明式 grammar |
| **嵌套语法** | 手写递归（HtmlLexer→CSS/JS，Markdown→任意） | `inside` 字段声明式嵌套，任意层级 | 🟡 能力有但非通用 |
| **标准 token 类型** | 18 种，`Variable`/`Namespace` 声明但未产生 | 30 种 `StandardTokenName` | 🟡 缺 `boolean`/`char`/`regex`/`symbol`/`url`/`selector`/`tag`/`attr-*`/`doctype`/`entity`/`prolog`/`cdata` 等 |
| **pattern 能力** | 逐字符 `when` | `lookbehind`（后顾）/`greedy`（贪婪回溯）/`alias` | 🔴 缺通用 pattern 能力 |
| **token 别名** | 无 | `token.addAlias()`，单 token 多类型 | 🔴 缺 |
| **hook/插件系统** | 无 | `before-tokenize`/`after-tokenize`/`wrap`/`complete` | 🟡 缺（但 KMP 场景优先级低） |
| **语言扩展性** | 新语言 = 手写几百行 lexer 类 + 注册 | 新语言 = 声明 grammar 对象 | 🔴 Prism 一个 grammar 通常 20-80 行 |
| **行内高亮渲染** | 单色 SpanStyle，无 bold/italic 区分 | CSS class，主题可加粗/斜体 | 🟡 缺字体样式维度 |

### 做得好的（保留）
- ✅ 模块拆分：`palette-code`（纯逻辑、KMP、无 UI）与 `palette`（渲染）分离，边界清晰
- ✅ 主题映射：`CodeTokenType → Color` 经 `PaletteTheme` 派生，可在顶层统一调色
- ✅ 诊断系统：`PaletteCodeDiagnostic`（BlankLanguage/UnsupportedLanguage/HighlighterFailure）
- ✅ 语言注册表：`registerLanguage` + alias，自定义高亮器优先于内置
- ✅ 测试覆盖：1064 行行为测试 + fixture 回归

---

## 分期计划

### 第一期：声明式 grammar 引擎核心（最高优先级，架构基础）

这是对齐 Prism 的根基。建立与 Prism 兼容的 grammar 模型和通用 tokenize 引擎，让现有手写 lexer 可以渐进式迁移，新语言可零手写代码接入。

| 项 | 说明 | 难度 |
|----|------|------|
| **1.1 Grammar 模型** | 定义 `GrammarToken(pattern, lookbehind, greedy, alias, inside)`、`Grammar`、`Token` 类，对齐 Prism `types.d.ts` | ⭐⭐ |
| **1.2 通用 tokenize 引擎** | `tokenize(text, grammar): List<Token>`，实现 pattern 匹配 + `lookbehind` + `greedy` 回溯 + `inside` 递归 | ⭐⭐⭐ |
| **1.3 扩展 CodeTokenType** | 补齐 Prism 标准 token：`Boolean`/`Char`/`Regex`/`Symbol`/`Url`/`Selector`/`Tag`/`AttrName`/`AttrValue`/`Doctype`/`Entity`/`Prolog`/`Cdata`；渲染层补对应颜色 | ⭐ |
| **1.4 GrammarRegistry** | 按 keyword/alias 注册 grammar，`highlight(code, lang)` 查表分发，与现有 lexer 注册表统一 | ⭐ |
| **1.5 两个样板 grammar** | 用 JSON + Markdown 两个最规则的语法验证引擎（JSON 纯 token，Markdown 演示 `inside` 嵌套），作为迁移范本 | ⭐⭐ |

第一期完成后：**新语言接入 = 声明一个 grammar 对象**（无需手写 scanner）。

### 第二期：迁移高频语言到 grammar + 补 token 维度

把高频语言的 lexer 迁移为声明式 grammar，同时补齐渲染层的字体样式维度（bold/italic），对齐 Prism 主题表现。

| 项 | 说明 | 难度 |
|----|------|------|
| **2.1 YAML/TOML/INI** | 数据配置型，grammar 化最直接 | ⭐ |
| **2.2 Python/SQL** | 中等复杂度，含多行字符串/注释状态 | ⭐⭐ |
| **2.3 CSS/HTML/XML** | 迁移并验证 `inside` 嵌套（HTML 内 CSS/JS 互嵌） | ⭐⭐ |
| **2.4 Kotlin/Java/JS/TS（KotlinLike）** | 最复杂的启发式（function/class/constant 推断），grammar 化需保留语义推断 | ⭐⭐⭐ |
| **2.5 字体样式维度** | `CodeTokenStyle` 增加 bold/italic，`CodeBlockColors` → `CodeBlockStyles`（color + fontStyle），对齐 Prism `bold`/`italic`/`important` token | ⭐⭐ |

### 第三期：扩展语言覆盖（grammar 声明即可，成本低）

引擎就绪后，新语言只需声明 grammar。

| 语言 | keyword/alias | 说明 |
|------|---------------|------|
| **C/C++** | `c`/`cpp`/`c++` | C-like，复用 grammar 基础 |
| **C#** | `csharp`/`cs`/`dotnet` | |
| **Go** | `go`/`golang` | |
| **Rust** | `rust`/`rs` | |
| **PHP** | `php` | 含 HTML 嵌套 |
| **Ruby** | `ruby`/`rb` | |
| **Swift** | `swift` | |
| **Scala** | `scala` | |
| **Shell 扩展** | `powershell`/`ps1` | |
| **SCSS/SASS/LESS** | `scss`/`sass`/`less` | 现显式不支持，grammar 化后可支持 |
| **JSX/TSX** | `jsx`/`tsx` | 现显式不支持 |

### 第四期：高级能力（按需）

| 项 | 说明 |
|----|------|
| **行号/高亮行/差异标记** | 渲染增强，已有部分基础（diff token） |
| **自动语言检测** | 基于 token 分布的启发式推断 |
| **增量 tokenization** | 大文件性能，仅重新高亮变更行 |
| **插件/hook 系统** | 对齐 Prism `before-tokenize`/`wrap`，用于行号、复制按钮等 |
| **Grammar 自动加载** | 懒加载 grammar，减少初始体积 |

---

## 架构落地原则

1. **grammar 与 lexer 并存**：第一期不删除现有 lexer，grammar 引擎作为并行能力。新语言优先用 grammar，旧语言分批迁移。`highlight()` 同时支持两种（grammar 优先，fallback 到 lexer）。
2. **外部 API 不变**：`PaletteCodeHighlighter.highlight(code, language): HighlightedCode` 签名不变，`CodeToken`/`CodeTokenType` 对消费者兼容扩展。
3. **TDD**：每个新 grammar 用 Prism 同名语言的样例做 fixture 测试；tokenize 引擎用边界用例（嵌套、greedy 回溯、lookbehind）。
4. **主题派生**：新增 token 类型/字体样式从 `PaletteTheme` 派生，不硬编码。

---

## 已完成

| 日期 | 项 | 说明 |
|------|----|------|
| （历史） | 17 语言手写 lexer | Kotlin/Java/JS/TS/JSON/CSS/Python/HTML/XML/YAML/TOML/INI/GraphQL/Diff/Markdown/Dockerfile/SQL |
| （历史） | 主题映射 | CodeTokenType → Color（PaletteTheme 派生） |
| （历史） | 诊断 + 注册表 | PaletteCodeDiagnostic + registerLanguage |
| 2026-06-26 | 第一期 1.1 Grammar 模型 | GrammarToken(pattern/lookbehind/greedy/alias/inside)、Grammar、GrammarTokenValue，对齐 Prism types.d.ts |
| 2026-06-26 | 第一期 1.2 tokenize 引擎 | GrammarTokenizer.tokenize，TDD 覆盖 pattern/lookbehind/greedy/inside/alias/empty 边界 |
| 2026-06-26 | 第一期 1.3 扩展 CodeTokenType | 追加 18 种 Prism 标准 token（Boolean/Char/Regex/Symbol/Url/Selector/Tag/AttrName/AttrValue/Doctype/Entity/Prolog/Cdata/Atrule/Bold/Italic/Important），渲染层补颜色映射 |
| 2026-06-26 | 第一期 1.4 GrammarRegistry | highlight() grammar 优先 fallback lexer；GrammarHighlighter 桥接行式接口；GrammarTokenTypeMapping |
| 2026-06-26 | 第一期 1.5 JSON 样板 grammar | 声明式 JSON grammar，分类与原 JsonLexer 一致，零回归验证引擎 |
| 2026-06-26 | 第二期：字体样式维度 | Bold/Italic/Important token 在渲染层产生 fontWeight/fontStyle，对齐 Prism 主题表现 |
| 2026-06-26 | 第二期：TOML grammar 迁移 | 声明式 TomGrammar 替代 TomlLexer；表名→type、键→keyword、多行字符串 `(?s)` 跨行、`inside` 递归分类括号与名字，零回归 |
| 2026-06-27 | 第二期：CSS grammar 迁移 | 声明式 CssGrammar 替代 CssLexer；@at→annotation、.class/#id→type、#hex→number、属性/值关键字→keyword，块注释 `(?s)` 跨行，零回归 |
| 2026-06-27 | 第二期：动态语言嵌入引擎能力 | GrammarToken 新增 `languageResolver` 回调 + GrammarRegistry.grammarOrNull；tokenizer 命中后按回调返回的 grammar 重新分词，解锁 HTML/Markdown 的动态嵌入（替代 lexer 的 embeddedHighlighter 回调）|
| 2026-06-27 | 第二期：HTML/XML/SVG grammar 迁移 + KotlinLike(JS) | HtmlGrammar（tag via inside、style→css/script→js 经 languageResolver 动态嵌入）；KotlinLikeGrammar 作为 javascript/js 注册供嵌入；零回归 |
| 2026-06-27 | 第二期：SQL grammar 迁移 | 声明式 SqlGrammar 替代 SqlLexer；keyword/type 大小写不敏感、function 识别（关键字优先于 function，OVER 保持 keyword）、$tag$…$tag$ 用反向引用 `\1` 处理跨行 dollar-quote、`/* */`/`--`/反引号各归其类，零回归 |

## 待办

- [x] 第一期：grammar 引擎核心（模型/tokenize/token 扩展/registry/JSON 样板）✅
- [ ] 第二期：高频语言迁移 + 字体样式维度
  - [x] 字体样式维度（Bold/Italic/Important 渲染）✅
  - [x] TOML/INI/properties 迁移（最简单，无嵌入）✅
  - [x] CSS 迁移 ✅
  - [x] 动态语言嵌入引擎能力（languageResolver）✅
  - [x] HTML/XML/SVG 迁移（含 style/script 嵌入，验证 `inside` + 动态嵌入）✅
  - [x] KotlinLike(JS 子集) grammar（供 HTML 嵌入）✅
  - [x] SQL 迁移（dollar-quote 反向引用、关键字优先于函数）✅
  - [ ] Markdown grammar 接入：动态嵌入能力已具备，但 fenced-code 嵌入需调度**完整 highlighter**（含 lexer fallback，如 kotlin 仍在 lexer），languageResolver 仅返回 Grammar，暂阻塞
  - [ ] Python 迁移：f-string 精确分词（`f"Hello, "`+`{`+`name`+`}`+`"`）+ 三引号跨行，lexer 有专门状态机，纯 grammar 风险高，暂缓
  - [ ] KotlinLike 完整迁移（kotlin/java/typescript，需覆盖字符串模板状态 + 三引号字符串）
  - [ ] YAML 迁移（block scalar 状态机，需 grammar 引擎支持跨行状态或保留 lexer）
- [ ] 第三期：扩展语言覆盖（C/C++/C#/Go/Rust/PHP/Ruby/Swift/Scala/SCSS/JSX）
- [ ] 第四期：高级能力（行号增强/语言检测/增量/hook）
