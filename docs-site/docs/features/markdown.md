# Markdown

The `palette-markdown` module parses and renders CommonMark + GFM, with an editor toolbar, TOC, and frontmatter support.

## Viewer

```kotlin
import xyz.junerver.compose.palette.components.markdown.PMarkdownViewer

val md = """
    # Title
    Some **bold** text and a `code span`.
"""
PMarkdownViewer(markdown = md, showCopyAction = true)
```

- `onLinkClick` / `onAnchorClick` — link & heading-anchor navigation
- `inlineImageContent` — custom image rendering slot
- `verticalScroll` — built-in scrolling
- Fenced code blocks dispatch to `palette-code`; ```` ```mermaid ```` blocks dispatch to `palette-mermaid`

## Editor

```kotlin
import xyz.junerver.compose.palette.components.markdown.PMarkdownEditor

var text by remember { mutableStateOf("") }
PMarkdownEditor(
    value = text,
    onValueChange = { text = it },
)
```

The editor includes a format toolbar (bold/italic/headings/lists/quote/link/code/table), Tab indentation, auto-continuation, and `MarkdownHistory` undo/redo with Ctrl/Cmd shortcuts.

## TOC & Frontmatter

`MarkdownRenderModel` exposes:

- `toc: List<MarkdownTocEntry>` — derived headings, with ids matching the viewer's anchor scroll
- `frontmatter: Map<String, String>` — parsed YAML frontmatter (stripped from the rendered body)

Render a TOC separately with `PMarkdownToc`, bridging clicks to the viewer's `onAnchorClick`.

See [the roadmap](https://github.com/junerver/Palette/blob/master/docs/compose/plans/palette-markdown-evolution-roadmap.md) for the evolution plan.
