package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.mermaid.PMermaidDiagram
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun MermaidDemo() {
    val text = mermaidDemoText()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.flowchartTitle) {
            PMermaidDiagram(source = text.flowchartSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.sequenceTitle) {
            PMermaidDiagram(source = text.sequenceSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.classDiagramTitle) {
            PMermaidDiagram(source = text.classDiagramSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.erDiagramTitle) {
            PMermaidDiagram(source = text.erDiagramSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.stateDiagramTitle) {
            PMermaidDiagram(source = text.stateDiagramSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.pieTitle) {
            PMermaidDiagram(source = text.pieSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.ganttTitle) {
            PMermaidDiagram(source = text.ganttSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.gitGraphTitle) {
            PMermaidDiagram(source = text.gitGraphSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.mindmapTitle) {
            PMermaidDiagram(source = text.mindmapSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.timelineTitle) {
            PMermaidDiagram(source = text.timelineSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.quadrantTitle) {
            PMermaidDiagram(source = text.quadrantSource)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.xychartTitle) {
            PMermaidDiagram(source = text.xychartSource)
        }
    }
}

@Composable
@ReadOnlyComposable
private fun mermaidDemoText(): MermaidDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            MermaidDemoText(
                title = "Mermaid",
                subtitle = "Mermaid 图表渲染，支持流程图、时序图、类图、ER 图、状态图、饼图、甘特图、Git 图和思维导图。",
                flowchartTitle = "流程图 (Flowchart)",
                flowchartSource =
                    """
                    flowchart TD
                        subgraph 输入层 [输入]
                            A[Markdown] --> B[Parser]
                        end
                        subgraph 处理层 [处理]
                            B --> C{类型判断}
                            C -->|代码| D[CodeBlock]
                            C -->|Mermaid| E[MermaidParser]
                        end
                        subgraph 输出层 [输出]
                            D --> F[Viewer]
                            E --> F
                            F --> G[Preview]
                        end
                    """.trimIndent(),
                sequenceTitle = "时序图 (Sequence Diagram)",
                sequenceSource =
                    """
                    sequenceDiagram
                        participant User
                        participant Editor
                        participant Parser
                        participant Viewer
                        User->>Editor: 输入内容
                        Editor->>Parser: 解析 Markdown
                        Note right of Parser: 生成 AST
                        Parser->>Viewer: 渲染模型
                        Note over User,Viewer: 实时预览
                    """.trimIndent(),
                classDiagramTitle = "类图 (Class Diagram)",
                classDiagramSource =
                    """
                    classDiagram
                        class Component {
                            +String name
                            +render() void
                        }
                        class Button {
                            +String text
                            +onClick() void
                        }
                        class TextField {
                            +String value
                            +onValueChange() void
                        }
                        Component <|-- Button
                        Component <|-- TextField
                    """.trimIndent(),
                erDiagramTitle = "ER 图 (Entity Relationship)",
                erDiagramSource =
                    """
                    erDiagram
                        USER {
                            string name
                            string email
                        }
                        ORDER {
                            int id
                            date created
                        }
                        PRODUCT {
                            string name
                            float price
                        }
                        USER ||--o{ ORDER : places
                        ORDER ||--|{ PRODUCT : contains
                    """.trimIndent(),
                stateDiagramTitle = "状态图 (State Diagram)",
                stateDiagramSource =
                    """
                    stateDiagram-v2
                        [*] --> Idle
                        Idle --> Loading : 请求数据
                        Loading --> Success : 请求成功
                        Loading --> Error : 请求失败
                        Success --> Idle : 重置
                        Error --> Loading : 重试
                        Error --> Idle : 取消
                        Success --> [*]
                    """.trimIndent(),
                pieTitle = "饼图 (Pie Chart)",
                pieSource =
                    """
                    pie title 组件库模块占比
                        "core" : 40
                        "components" : 35
                        "mermaid" : 15
                        "code" : 10
                    """.trimIndent(),
                ganttTitle = "甘特图 (Gantt)",
                ganttSource =
                    """
                    gantt
                        title 发版计划
                        dateFormat YYYY-MM-DD
                        section 开发
                            设计   :des, 2024-01-01, 5d
                            编码   :after des, 10d
                        section 测试
                            测试   :crit, active, 3d
                            修复   :2d
                    """.trimIndent(),
                gitGraphTitle = "Git 图 (GitGraph)",
                gitGraphSource =
                    """
                    gitGraph
                       commit id: "初始化"
                       commit
                       branch develop
                       checkout develop
                       commit id: "开发" tag: "v0.1"
                       checkout main
                       merge develop
                       commit type: HIGHLIGHT
                    """.trimIndent(),
                mindmapTitle = "思维导图 (Mindmap)",
                mindmapSource =
                    """
                    mindmap
                      root((Palette))
                        组件
                          基础
                          表单
                          布局
                        主题
                          明色
                          暗色
                        多平台
                          Android
                          Desktop
                          iOS
                    """.trimIndent(),
                timelineTitle = "时间线 (Timeline)",
                timelineSource =
                    """
                    timeline
                        title Palette 发展历程
                        section 2024
                            Q1 : 项目立项
                            Q2 : 核心组件 : 主题系统
                        section 2025
                            Q1 : Mermaid 支持
                            Q2 : 类图 : 思维导图
                        section 2026
                            Q1 : 时间线 : 象限图 : XY 图表
                    """.trimIndent(),
                quadrantTitle = "四象限图 (Quadrant Chart)",
                quadrantSource =
                    """
                    quadrantChart
                        title 功能优先级评估
                        x-axis 投入低 --> 投入高
                        y-axis 价值低 --> 价值高
                        quadrant-1 立即执行
                        quadrant-2 值得投资
                        quadrant-3 暂缓
                        quadrant-4 重新评估
                        登录页: [0.2, 0.85]
                        暗色模式: [0.4, 0.7]
                        图表导出: [0.8, 0.5]
                        动画系统: [0.75, 0.3] color: #ff3300, radius: 10
                    """.trimIndent(),
                xychartTitle = "XY 图表 (XYChart)",
                xychartSource =
                    """
                    xychart-beta
                        title "季度下载量"
                        x-axis [Q1, Q2, Q3, Q4]
                        y-axis 下载量(万) 0 --> 500
                        bar [120, 180, 240, 310]
                        line [100, 150, 210, 290]
                    """.trimIndent(),
            )

        Language.EN_US ->
            MermaidDemoText(
                title = "Mermaid",
                subtitle = "Mermaid diagram rendering, supporting flowcharts, sequence diagrams, class diagrams, ER diagrams, state diagrams, pie charts, gantt charts, git graphs, and mindmaps.",
                flowchartTitle = "Flowchart",
                flowchartSource =
                    """
                    flowchart TD
                        subgraph Input [Input]
                            A[Markdown] --> B[Parser]
                        end
                        subgraph Process [Processing]
                            B --> C{Type Check}
                            C -->|Code| D[CodeBlock]
                            C -->|Mermaid| E[MermaidParser]
                        end
                        subgraph Output [Output]
                            D --> F[Viewer]
                            E --> F
                            F --> G[Preview]
                        end
                    """.trimIndent(),
                sequenceTitle = "Sequence Diagram",
                sequenceSource =
                    """
                    sequenceDiagram
                        participant User
                        participant Editor
                        participant Parser
                        participant Viewer
                        User->>Editor: Enter content
                        Editor->>Parser: Parse Markdown
                        Note right of Parser: Generate AST
                        Parser->>Viewer: Render model
                        Note over User,Viewer: Live preview
                    """.trimIndent(),
                classDiagramTitle = "Class Diagram",
                classDiagramSource =
                    """
                    classDiagram
                        class Component {
                            +String name
                            +render() void
                        }
                        class Button {
                            +String text
                            +onClick() void
                        }
                        class TextField {
                            +String value
                            +onValueChange() void
                        }
                        Component <|-- Button
                        Component <|-- TextField
                    """.trimIndent(),
                erDiagramTitle = "Entity Relationship",
                erDiagramSource =
                    """
                    erDiagram
                        USER {
                            string name
                            string email
                        }
                        ORDER {
                            int id
                            date created
                        }
                        PRODUCT {
                            string name
                            float price
                        }
                        USER ||--o{ ORDER : places
                        ORDER ||--|{ PRODUCT : contains
                    """.trimIndent(),
                stateDiagramTitle = "State Diagram",
                stateDiagramSource =
                    """
                    stateDiagram-v2
                        [*] --> Idle
                        Idle --> Loading : fetch data
                        Loading --> Success : success
                        Loading --> Error : failed
                        Success --> Idle : reset
                        Error --> Loading : retry
                        Error --> Idle : cancel
                        Success --> [*]
                    """.trimIndent(),
                pieTitle = "Pie Chart",
                pieSource =
                    """
                    pie title Library module share
                        "core" : 40
                        "components" : 35
                        "mermaid" : 15
                        "code" : 10
                    """.trimIndent(),
                ganttTitle = "Gantt",
                ganttSource =
                    """
                    gantt
                        title Release Plan
                        dateFormat YYYY-MM-DD
                        section Dev
                            Design :des, 2024-01-01, 5d
                            Code   :after des, 10d
                        section QA
                            Test   :crit, active, 3d
                            Fix    :2d
                    """.trimIndent(),
                gitGraphTitle = "GitGraph",
                gitGraphSource =
                    """
                    gitGraph
                       commit id: "init"
                       commit
                       branch develop
                       checkout develop
                       commit id: "feature" tag: "v0.1"
                       checkout main
                       merge develop
                       commit type: HIGHLIGHT
                    """.trimIndent(),
                mindmapTitle = "Mindmap",
                mindmapSource =
                    """
                    mindmap
                      root((Palette))
                        Components
                          Foundation
                          Forms
                          Layout
                        Theme
                          Light
                          Dark
                        Platforms
                          Android
                          Desktop
                          iOS
                    """.trimIndent(),
                timelineTitle = "Timeline",
                timelineSource =
                    """
                    timeline
                        title Palette Roadmap
                        section 2024
                            Q1 : Project kickoff
                            Q2 : Core components : Theme system
                        section 2025
                            Q1 : Mermaid support
                            Q2 : Class diagrams : Mindmaps
                        section 2026
                            Q1 : Timeline : Quadrant chart : XY chart
                    """.trimIndent(),
                quadrantTitle = "Quadrant Chart",
                quadrantSource =
                    """
                    quadrantChart
                        title Feature prioritization
                        x-axis Low effort --> High effort
                        y-axis Low value --> High value
                        quadrant-1 Do now
                        quadrant-2 Invest
                        quadrant-3 Hold
                        quadrant-4 Reconsider
                        Login page: [0.2, 0.85]
                        Dark mode: [0.4, 0.7]
                        Chart export: [0.8, 0.5]
                        Animations: [0.75, 0.3] color: #ff3300, radius: 10
                    """.trimIndent(),
                xychartTitle = "XY Chart",
                xychartSource =
                    """
                    xychart-beta
                        title "Quarterly downloads"
                        x-axis [Q1, Q2, Q3, Q4]
                        y-axis Downloads(k) 0 --> 500
                        bar [120, 180, 240, 310]
                        line [100, 150, 210, 290]
                    """.trimIndent(),
            )
    }

private data class MermaidDemoText(
    val title: String,
    val subtitle: String,
    val flowchartTitle: String,
    val flowchartSource: String,
    val sequenceTitle: String,
    val sequenceSource: String,
    val classDiagramTitle: String,
    val classDiagramSource: String,
    val erDiagramTitle: String,
    val erDiagramSource: String,
    val stateDiagramTitle: String,
    val stateDiagramSource: String,
    val pieTitle: String,
    val pieSource: String,
    val ganttTitle: String,
    val ganttSource: String,
    val gitGraphTitle: String,
    val gitGraphSource: String,
    val mindmapTitle: String,
    val mindmapSource: String,
    val timelineTitle: String,
    val timelineSource: String,
    val quadrantTitle: String,
    val quadrantSource: String,
    val xychartTitle: String,
    val xychartSource: String,
)
