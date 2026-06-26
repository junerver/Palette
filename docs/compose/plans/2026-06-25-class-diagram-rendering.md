# ClassDiagram Rendering Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use compose:subagent (recommended) or compose:execute to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a dedicated ClassDiagramMermaidDiagram composable that renders class members, annotations, visibility markers, and relationship styles.

**Architecture:** The classDiagram parsing and layout already exist. This plan focuses on creating a specialized renderer that displays class structure (members, annotations) and distinguishes relationship types visually.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform

## Current State

- **Models** ✅: `MermaidClassDefinition`, `MermaidClassMember`, `MermaidClassRelationship` exist
- **Parser** ✅: `parseClassMember()`, `parseClassRelationship()` exist
- **Layout** ✅: `layoutClassDiagram()` exists
- **Renderer** ⚠️: Currently reuses `FlowchartMermaidDiagram` (no member display)
- **Tests** ✅: Basic parsing tests exist

## Global Constraints

- Follow existing code style (no comments unless asked)
- Use `PaletteTheme` tokens for styling
- Use `compose-hooks` for state management
- All existing tests must continue to pass

---

## Task 1: Create ClassDiagramMermaidDiagram composable

**Covers:** Class diagram rendering with members, annotations, visibility

**Files:**
- Modify: `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/mermaid/MermaidDiagram.kt`

**Interfaces:**
- Consumes: `MermaidLayout`, `MermaidColors`, `MermaidClassDefinition` (from layout.diagram)
- Produces: `ClassDiagramMermaidDiagram` composable

- [ ] **Step 1: Add ClassDiagramMermaidDiagram composable**

Add after `SequenceMermaidDiagram` (around line 544):

```kotlin
@Composable
private fun ClassDiagramMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    layout: MermaidLayout,
    classDefinitions: List<xyz.junerver.compose.palette.mermaid.MermaidClassDefinition>,
) {
    val nodeWidth = 180.dp
    val memberHeight = 20.dp
    val headerHeight = 32.dp
    val padding = 8.dp

    val nodeRight = (layout.nodes.values.maxOfOrNull { it.x } ?: 0f) + 204f
    val nodeBottom = (layout.nodes.values.maxOfOrNull { it.y } ?: 0f) + 120f
    val width = nodeRight.dp
    val height = nodeBottom.dp

    Box(
        modifier = modifier.width(width).height(height),
    ) {
        // Draw edges first (behind nodes)
        Canvas(modifier = Modifier.matchParentSize()) {
            layout.edges.forEach { edge ->
                val from = layout.nodes[edge.from] ?: return@forEach
                val to = layout.nodes[edge.to] ?: return@forEach
                val startX = from.x.dp.toPx() + nodeWidth.toPx() / 2f
                val startY = from.y.dp.toPx() + 44f
                val endX = to.x.dp.toPx() + nodeWidth.toPx() / 2f
                val endY = to.y.dp.toPx()
                val pathEffect = if (edge.style == MermaidEdgeStyle.Dotted) {
                    PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                } else {
                    null
                }
                drawLine(
                    color = colors.edgeColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f,
                    pathEffect = pathEffect,
                )
            }
        }

        // Draw class nodes
        classDefinitions.forEach { cls ->
            val positioned = layout.nodes[cls.id] ?: return@forEach
            val memberCount = cls.members.size
            val nodeHeight = headerHeight + memberCount.dp * memberHeight + padding * 2

            Box(
                modifier = Modifier
                    .absoluteOffset(x = positioned.x.dp, y = positioned.y.dp)
                    .width(nodeWidth)
                    .height(nodeHeight)
                    .background(colors.nodeContainerColor, RoundedCornerShape(4.dp))
                    .border(1.dp, colors.nodeBorderColor, RoundedCornerShape(4.dp)),
            ) {
                Column {
                    // Class header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                            .background(colors.nodeContainerColor.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Column {
                            if (cls.annotation != null) {
                                Text(
                                    text = "<<${cls.annotation}>>",
                                    color = colors.nodeContentColor.copy(alpha = 0.7f),
                                    style = PaletteTheme.typography.label,
                                )
                            }
                            Text(
                                text = cls.label,
                                color = colors.nodeContentColor,
                                style = PaletteTheme.typography.body,
                            )
                        }
                    }

                    // Members
                    cls.members.forEach { member ->
                        val visibility = when (member.visibility) {
                            xyz.junerver.compose.palette.mermaid.MermaidClassVisibility.Public -> "+"
                            xyz.junerver.compose.palette.mermaid.MermaidClassVisibility.Private -> "-"
                            xyz.junerver.compose.palette.mermaid.MermaidClassVisibility.Protected -> "#"
                            xyz.junerver.compose.palette.mermaid.MermaidClassVisibility.Package -> "~"
                        }
                        val prefix = buildString {
                            append(visibility)
                            if (member.isAbstract) append("*")
                            if (member.isStatic) append("$")
                        }
                        val memberText = if (member.kind == xyz.junerver.compose.palette.mermaid.MermaidClassMemberKind.Method) {
                            "$prefix${member.name}()${member.type?.let { ": $it" } ?: ""}"
                        } else {
                            "$prefix${member.name}${member.type?.let { ": $it" } ?: ""}"
                        }
                        Text(
                            text = memberText,
                            color = colors.nodeContentColor,
                            style = PaletteTheme.typography.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(memberHeight)
                                .padding(horizontal = 8.dp),
                        )
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 2: Update PMermaidDiagram to use ClassDiagramMermaidDiagram**

Find the `when` block around line 346 and change:

```kotlin
MermaidDiagramType.ClassDiagram ->
    FlowchartMermaidDiagram(
        modifier = modifier,
        colors = colors,
        layout = resolvedLayout,
    )
```

To:

```kotlin
MermaidDiagramType.ClassDiagram ->
    ClassDiagramMermaidDiagram(
        modifier = modifier,
        colors = colors,
        layout = resolvedLayout,
        classDefinitions = diagram.classDefinitions,
    )
```

Note: Need to pass `diagram` to the composable. Check if `diagram` is accessible in scope.

- [ ] **Step 3: Run tests**

Run: `.\gradlew.bat :palette:desktopTest --no-daemon`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/mermaid/MermaidDiagram.kt
git commit -m "feat: add dedicated classDiagram rendering with member display"
```

---

## Task 2: Add classDiagram rendering tests

**Covers:** Visual verification of classDiagram rendering

**Files:**
- Create: `palette/src/desktopTest/kotlin/xyz/junerver/compose/palette/components/mermaid/ClassDiagramUiTest.kt`

- [ ] **Step 1: Create ClassDiagramUiTest**

```kotlin
package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ClassDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun classDiagram_rendersClassesWithMembers() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        classDiagram
                            class Animal {
                                +String name
                                +int age
                                +isMammal() bool
                            }
                            class Dog {
                                +bark() void
                            }
                            Animal <|-- Dog
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("Animal").assertIsDisplayed()
        rule.onNodeWithText("Dog").assertIsDisplayed()
    }

    @Test
    fun classDiagram_rendersAnnotations() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        classDiagram
                            class IShape {
                                <<interface>>
                                +area() double
                            }
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("IShape").assertIsDisplayed()
        rule.onNodeWithText("<<interface>>").assertIsDisplayed()
    }
}
```

- [ ] **Step 2: Run tests**

Run: `.\gradlew.bat :palette:desktopTest --tests "xyz.junerver.compose.palette.components.mermaid.ClassDiagramUiTest" --no-daemon`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add palette/src/desktopTest/kotlin/xyz/junerver/compose/palette/components/mermaid/ClassDiagramUiTest.kt
git commit -m "test: add classDiagram rendering tests"
```

---

## Task 3: Add more classDiagram parsing edge case tests

**Covers:** Comprehensive parsing coverage

**Files:**
- Modify: `palette-mermaid/src/commonTest/kotlin/xyz/junerver/compose/palette/mermaid/MermaidParserTest.kt`

- [ ] **Step 1: Add edge case tests**

```kotlin
@Test
fun parsesClassDiagramWithAbstractAndStaticMembers() {
    val diagram = MermaidParser.parse(
        """
        classDiagram
            class AbstractShape {
                <<abstract>>
                *calculate() double
                $PI double
            }
        """.trimIndent()
    )

    val cls = diagram.classDefinitions.single()
    assertEquals("abstract", cls.annotation)
    assertTrue(cls.members.any { it.isAbstract && it.name == "calculate" })
    assertTrue(cls.members.any { it.isStatic && it.name == "PI" })
}

@Test
fun parsesClassDiagramWithCardinality() {
    val diagram = MermaidParser.parse(
        """
        classDiagram
            class Customer
            class Order
            Customer "1" --> "*" Order : places
        """.trimIndent()
    )

    val rel = diagram.classRelationships.single()
    assertEquals("1", rel.fromCardinality)
    assertEquals("*", rel.toCardinality)
    assertEquals("places", rel.label)
}
```

- [ ] **Step 2: Run tests**

Run: `.\gradlew.bat :palette-mermaid:desktopTest --no-daemon`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add palette-mermaid/src/commonTest/kotlin/xyz/junerver/compose/palette/mermaid/MermaidParserTest.kt
git commit -m "test: add classDiagram parsing edge case tests"
```

---

## Final Verification

- [ ] **Step 1: Run all tests**

```bash
.\gradlew.bat :palette:desktopTest :palette-mermaid:desktopTest --no-daemon
```

Expected: ALL PASS

- [ ] **Step 2: Run coverage checks**

```bash
.\gradlew.bat :palette:runCoverageChecks --no-daemon
```

Expected: Coverage >= 80%

- [ ] **Step 3: Final commit**

```bash
git add -A
git commit -m "feat: complete classDiagram rendering implementation"
```
