package xyz.junerver.compose.palette.components.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import xyz.junerver.compose.palette.mermaid.MermaidLayoutEngine
import xyz.junerver.compose.palette.mermaid.MermaidParser

class ArchitectureVisibleEdgesTest {
    @Test
    fun hidesJunctionHelperEdgesFromRendering() {
        val diagram = MermaidParser.parse(
            """
            architecture-beta
                group api(cloud)[API Gateway]
                service db(database)[Database] in api
                service server(server)[Server] in api
                service cache(disk)[Cache] in api
                junction jc
                db:L -- R:server
                cache:T -- B:server
                server:R --> L:jc
            """.trimIndent(),
        )
        val layout = MermaidLayoutEngine.layout(diagram)
        val nodeById = layout.archNodes.associateBy { it.id }
        val visibleEdges = visibleArchitectureEdges(layout.archEdges, nodeById)

        assertEquals(2, visibleEdges.size)
        assertFalse(visibleEdges.any { it.from == "server" && it.to == "jc" })
    }
}
