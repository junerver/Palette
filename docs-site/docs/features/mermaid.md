# Mermaid Diagrams

The `palette-mermaid` module parses and renders Mermaid diagrams with Compose-native Canvas — no WebView. All **19 diagram types** are supported.

## Supported diagrams

| | | | |
| --- | --- | --- | --- |
| Flowchart | Sequence | Class | ER |
| State | Pie | Gantt | GitGraph |
| Mindmap | Timeline | QuadrantChart | XYChart |
| Requirement | Block | C4 (Person/System/Container/Component/Boundary) | Journey |
| Packet | Sankey | Architecture | |

## Usage

```kotlin
import xyz.junerver.compose.palette.components.mermaid.PMermaidDiagram

PMermaidDiagram(
    source = """
        graph LR
            A --> B --> C
    """.trimIndent(),
)
```

Inside Markdown fenced blocks (```` ```mermaid ````), `PMarkdownViewer` automatically dispatches to the mermaid renderer.

## Architecture

Diagrams register through a `MermaidDiagramParser` — adding a diagram type = a new parser + registration. Colors derive from `PaletteTheme` (the `utility` component theme holds the mermaid tokens). See [the roadmap](https://github.com/junerver/Palette/blob/master/docs/compose/plans/mermaid-diagram-support-roadmap.md).
