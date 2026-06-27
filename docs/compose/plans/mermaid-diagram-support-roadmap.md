# Mermaid 图例支持路线图

> 目标：逐步实现对 Mermaid 官方主要图例的支持。本文件是唯一的进度真相来源（source of truth），
> 每完成一种图即更新对应状态与实现说明。

参考：[Mermaid 官方文档 — Diagram Syntax](https://mermaid.js.org/intro/syntax-reference.html)

## 现状

已支持 5 种基础图例（解析 + 布局 + 渲染），但渲染质量与官方仍有差距：

| 图例 | 状态 | 备注 |
|------|------|------|
| Flowchart | ✅ 已支持 | 边分离、subgraph、样式指令 |
| Sequence | ✅ 已支持 | 自环、fragment、activation、region |
| ClassDiagram | ✅ 已支持 | UML 语义箭头（继承/组合/聚合） |
| ErDiagram | ✅ 已支持 | crow's foot、PK/FK 标记 |
| StateDiagram | ✅ 已支持 | BFS 分层、双向边扇形 |
| PieChart | ✅ 已支持 | 扇形 + 图例 |
| GanttChart | ✅ 已支持 | section/task/status/依赖 |
| GitGraph | ✅ 已支持 | commit/branch/merge |
| Mindmap | ✅ 已支持 | 缩进树 + tidy-tree + S 曲线 |
| Timeline | ✅ 已支持 | section + 续行多事件 + 左→右时间轴 |
| QuadrantChart | ✅ 已支持 | 四象限 + 点（含 color/radius 样式 + classDef） |
| XYChart | ✅ 已支持 | bar/line 双系列 + 数值/分类轴（`xychart` 与 `xychart-beta` 双关键词） |
| RequirementDiagram | ✅ 已支持 | 6 需求类型 + element + 7 关系（contains/satisfies/verifies/...） + 双向语法 |
| BlockDiagram | ✅ 已支持 | columns 网格 + 列跨度 + `block:` 嵌套 + 多形状 + 中箭头标签边 |
| C4Diagram | ✅ 已支持 | 5 子关键词 + Person/System/Container/Component 宏 + Boundary 嵌套 + Rel/BiRel/Rel_Back |

架构基础（2026-06 重构）：`MermaidDiagramParser` 注册制 + `ParseResult` 密封类型，
新增图例 = 新建一个 parser + 注册，无需改动现有代码。

---

## 架构落地步骤（每种新图的标准流程）

新增一种图 `XxxDiagram` 的固定改动清单：

1. **模型** `MermaidModels.kt`
   - `MermaidDiagramType` 枚举新增 `XxxDiagram`
   - 新增领域 data class（如 `XxxSection`、`XxxTask`）
   - `MermaidDiagram` 新增对应字段（`val xxxItems: List<...> = emptyList()`）

2. **解析** `parsers/XxxDiagramParser.kt`（新建）
   - 实现 `MermaidDiagramParser`，`keyword`/`aliases`/`parse()`
   - `ParseResult` 新增 `data class XxxDiagram(...) : ParseResult`
   - `ParseResult.toMermaidDiagram()` 新增分支

3. **布局** `MermaidLayoutEngine.kt`
   - `layout()` 的 `when(type)` 新增分支 → `layoutXxxDiagram()`
   - 复杂图若超出"rank/坐标"模型，`MermaidLayout` 可携带 `xxxRows`/`xxxSlices` 等专用字段（参考 `stateEdgeOffsets`）

4. **渲染** `MermaidDiagram.kt`
   - `PMermaidDiagram` 的 `when(type)` 新增分支 → `XxxDiagramMermaidDiagram()`
   - 所需新 token 派生自 `PaletteUtilityTokens`（颜色/字号/圆角）

5. **导出 & 测试**
   - parser commonTest（正常/边界/回归）
   - 渲染 desktopTest（节点显示/不崩）
   - 视觉截图对照 mermaid.live

6. **Demo** `MermaidDemo.kt` 增加示例页

---

## 分期计划

按 **复用度 + 用户价值 + 实现复杂度** 排序分四期。

### 第一期：基础图表型（高价值、模型简单，复用现有 box/axis 渲染）

| 图例 | keyword | 难度 | 说明 |
|------|---------|------|------|
| **Pie Chart** | `pie` | ⭐ 简单 | 饼图。解析 `key : value` 对；渲染算扇形角度。无复杂布局，纯几何 |
| **Gantt** | `gantt` | ⭐⭐ 中 | 甘特图。按 section 分组任务，含日期/时长/依赖/状态。布局=时间轴行，渲染=水平条 |
| **GitGraph** | `gitGraph` | ⭐⭐ 中 | Git 分支图。commit/branch/checkout/merge。布局=分支列+提交行 |

第一期共同特点：**布局是规则的行列网格**，不涉及节点-边拓扑，渲染可直接基于 `Canvas` 画矩形/扇形/时间轴，与现有 5 种图的节点-边模型解耦。

### 第二期：层级/时间线型

| 图例 | keyword | 难度 | 说明 |
|------|---------|------|------|
| **Mindmap** | `mindmap` | ⭐⭐ 中 | 思维导图。树形缩进节点，递归布局（径向或左右）。复用节点盒渲染 |
| **Timeline** | `timeline` | ⭐⭐ 中 | 时间线。时间段 → 事件列表，垂直时间轴布局 |
| **Quadrant Chart** | `quadrant-chart` | ⭐⭐ 中 | 四象限。x/y 轴 + 点；纯几何布局 |
| **XYChart** | `xychart-beta` | ⭐⭐⭐ 中高 | 柱状/折线图。坐标系 + 数据序列 |

### 第三期：关系/结构型

| 图例 | keyword | 难度 | 说明 |
|------|---------|------|------|
| **Requirement Diagram** | `requirementDiagram` | ⭐⭐⭐ 中高 | 需求图。需求/元素/关系，类 UML 盒+连线，可复用 class diagram 模式 |
| **C4 Diagram**（含 context/container/component） | `c4`/`C4Context`/... | ⭐⭐⭐⭐ 高 | C4 架构图。子类型多、DSL 复杂、含 person/boundary/relationship |
| **Block Diagram** | `block` | ⭐⭐ 中 | 矩形分块图。嵌套 block，含列数布局 |

### 第四期：专用/低频型（按需，可暂缓）

| 图例 | keyword | 难度 | 说明 |
|------|---------|------|------|
| **User Journey** | `journey` | ⭐⭐ 中 | 用户旅程。任务+满意度分数，表格+表情渲染 |
| **Sankey** | `sankey-beta` | ⭐⭐⭐ 中高 | 桑基图。节点-流量，流宽编码数据量，需 SVG 路径或贝塞尔 |
| **Packet** | `packet` | ⭐⭐ 中 | 网络包字段图。位字段表格 |
| **Architecture** | `architecture` | ⭐⭐⭐ 中高 | 架构部署图。节点/边+分组 |
| **Entity Relationship（新版 v2）** | `erDiagram` 增强 | — | 在现有 ER 基础上补 v2 语法 |

---

## 已完成

| 日期 | 图例 | 提交 | 说明 |
|------|------|------|------|
| 2026-06-25 | Flowchart | — | 初版 |
| 2026-06-25 | Sequence | — | 初版 |
| 2026-06-25 | ClassDiagram | — | 初版 |
| 2026-06-25 | ErDiagram | — | 初版（含 crow's foot） |
| 2026-06-25 | StateDiagram | — | 初版（含 BFS 分层） |
| 2026-06-25 | PieDiagram | — | 新增（扇形+图例+showData） |
| 2026-06-25 | GanttDiagram | — | 新增（核心版：section/task/status/duration/after，日期解析待后续） |
| 2026-06-26 | GitGraphDiagram | — | 新增（commit/branch/checkout/merge + id/tag/type，cherry-pick 待后续） |
| 2026-06-26 | MindmapDiagram | — | 新增（缩进树解析：相对缩进层级、6 种形状、parent 解析、tidy-tree 布局、S 曲线连接） |

---

## 待办（按期推进）

- [x] 第一期-1：Pie Chart ✅（2026-06-25）
- [x] 第一期-2：Gantt ✅（2026-06-25，核心版：section/task/status/duration/after 依赖，日期解析待后续）
- [x] 第一期-3：GitGraph ✅（2026-06-26，commit/branch/checkout/merge + id/tag/type，cherry-pick 待后续）
- [x] 第二期-1：Mindmap ✅（2026-06-26，缩进树解析 + tidy-tree 布局 + S 曲线连接；timeline/quadrant/xychart 待后续）
- [x] 第二期-2：Timeline ✅（2026-06-27，section 上下文 + `:` 续行多事件 + inline 链式事件，左→右水平时间轴）
- [x] 第二期-3：QuadrantChart ✅（2026-06-27，x/y 轴 `-->` 双端标签 + 四象限文本 + 点 `[x,y]` clamp 到 [0,1] + inline `color`/`radius` 样式 + `classDef` 类样式）
- [x] 第二期-4：XYChart ✅（2026-06-27，`xychart`/`xychart-beta` 双关键词 + 数值轴(`min-->max`)/分类轴(`[...]`) + bar/line 多系列 + 自动 y 轴范围）
- [x] 第二期全部完成 🎉（2026-06-27）
- [x] 第三期-1：BlockDiagram ✅（2026-06-27，columns 网格 + 列跨度 + `block:` 嵌套 + 多形状 + 中箭头标签边）
- [x] 第三期-2：RequirementDiagram ✅（2026-06-27，6 需求类型 + element + 7 关系双向语法 + DAG 拓扑布局）
- [x] 第三期-3：C4Diagram ✅（2026-06-27，5 子关键词 + 宏调用语法 + Boundary 嵌套 + Rel/BiRel/Rel_Back + 声明顺序布局）
- [x] 第三期全部完成 🎉（2026-06-27）
- [ ] 第四期：Journey / Sankey / Packet / Architecture
