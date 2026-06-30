package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.ParseResult
import xyz.junerver.compose.palette.mermaid.StateDefinition
import xyz.junerver.compose.palette.mermaid.StateNote
import xyz.junerver.compose.palette.mermaid.StateNotePosition
import xyz.junerver.compose.palette.mermaid.StateTransition

/**
 * State diagram parser. Extracted verbatim from the old monolithic `MermaidParser.parse()`
 * state branch — the matching rules, regexes and composite-state state machine are
 * unchanged so behaviour is identical.
 *
 * Registered under both `stateDiagram` and `stateDiagram-v2` headers.
 */
internal object StateDiagramParser : MermaidDiagramParser {
    override val keyword: String = "stateDiagram-v2"
    override val aliases: List<String> = listOf("stateDiagram")
    override val defaultDirection: MermaidDirection = MermaidDirection.TopDown

    private val transitionRegex = Regex("""^(\S+)\s*-->\s*(\S+)(?:\s*:\s*(.+))?$""")
    private val stateLabelRegex = Regex("""^state\s+"([^"]+)"\s+as\s+(\S+)$""")
    private val compositeRegex = Regex("""^state\s+(\S+)\s*\{$""")
    private val forkJoinRegex = Regex("""^state\s+(\S+)\s*<<(fork|join)>>$""")
    private val noteRegex = Regex("""^note\s+(right|left)\s+of\s+(\S+)\s*:\s*(.+)$""")
    private val simpleStateRegex = Regex("""^(\S+)$""")

    override fun parse(lines: List<String>): ParseResult.StateDiagram {
        val stateDefinitions = mutableListOf<StateDefinition>()
        val stateTransitions = mutableListOf<StateTransition>()
        val stateNotes = mutableListOf<StateNote>()
        var currentStateComposite: String? = null
        var currentStateChildren = mutableListOf<StateDefinition>()

        fun flushCurrentStateComposite() {
            val compositeId = currentStateComposite ?: return
            stateDefinitions.add(
                StateDefinition(
                    id = compositeId,
                    children = currentStateChildren.toList(),
                ),
            )
            currentStateComposite = null
            currentStateChildren = mutableListOf()
        }

        lines.forEach { line ->
            // Close brace ends composite state.
            if (line == "}" && currentStateComposite != null) {
                flushCurrentStateComposite()
                return@forEach
            }

            // Open brace continues composite state.
            if (line == "{" && currentStateComposite != null) {
                return@forEach
            }

            // Inside composite state.
            if (currentStateComposite != null) {
                val stateId = line.trim()
                if (stateId.isNotEmpty() && !stateId.startsWith("[*")) {
                    currentStateChildren.add(StateDefinition(id = stateId))
                }
                return@forEach
            }

            // Transition: state1 --> state2 [: event]
            transitionRegex.matchEntire(line)?.let { match ->
                val from = match.groupValues[1]
                val to = match.groupValues[2]
                val event = match.groupValues[3].ifEmpty { null }

                val fromId = if (from == "[*]") "start" else from
                val toId = if (to == "[*]") "end" else to

                if (from == "[*]" && stateDefinitions.none { it.id == "start" }) {
                    stateDefinitions.add(StateDefinition(id = "start", isStart = true))
                }
                if (to == "[*]" && stateDefinitions.none { it.id == "end" }) {
                    stateDefinitions.add(StateDefinition(id = "end", isEnd = true))
                }
                if (from != "[*]" && stateDefinitions.none { it.id == fromId }) {
                    stateDefinitions.add(StateDefinition(id = fromId))
                }
                if (to != "[*]" && stateDefinitions.none { it.id == toId }) {
                    stateDefinitions.add(StateDefinition(id = toId))
                }

                stateTransitions.add(StateTransition(from = fromId, to = toId, event = event))
                return@forEach
            }

            // State with label: state "label" as state_id
            stateLabelRegex.matchEntire(line)?.let { match ->
                stateDefinitions.add(
                    StateDefinition(
                        id = match.groupValues[2],
                        label = match.groupValues[1],
                    ),
                )
                return@forEach
            }

            // Composite state: state state_name {
            compositeRegex.matchEntire(line)?.let { match ->
                flushCurrentStateComposite()
                currentStateComposite = match.groupValues[1]
                currentStateChildren = mutableListOf()
                return@forEach
            }

            // Fork/Join: state state_name <<fork>> or <<join>>
            forkJoinRegex.matchEntire(line)?.let { match ->
                val kind = match.groupValues[2]
                stateDefinitions.add(
                    StateDefinition(
                        id = match.groupValues[1],
                        isFork = kind == "fork",
                        isJoin = kind == "join",
                    ),
                )
                return@forEach
            }

            // Note: note right/left of state_id : text
            noteRegex.matchEntire(line)?.let { match ->
                val position = if (match.groupValues[1] == "left") StateNotePosition.Left else StateNotePosition.Right
                stateNotes.add(
                    StateNote(
                        stateId = match.groupValues[2],
                        text = match.groupValues[3],
                        position = position,
                    ),
                )
                return@forEach
            }

            // Simple state declaration: state_id (if not already defined)
            simpleStateRegex.matchEntire(line)?.let { match ->
                val id = match.groupValues[1]
                if (id != "stateDiagram" && id != "stateDiagram-v2" && stateDefinitions.none { it.id == id }) {
                    stateDefinitions.add(StateDefinition(id = id))
                }
                return@forEach
            }
        }

        flushCurrentStateComposite()
        return ParseResult.StateDiagram(
            direction = defaultDirection,
            stateDefinitions = stateDefinitions,
            stateTransitions = stateTransitions,
            stateNotes = stateNotes,
        )
    }
}
