package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.GitBranch
import xyz.junerver.compose.palette.mermaid.GitCommit
import xyz.junerver.compose.palette.mermaid.GitCommitType
import xyz.junerver.compose.palette.mermaid.GitMerge
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * GitGraph parser. Mermaid syntax:
 * ```
 * gitGraph
 *    commit [id: "x"] [tag: "y"] [type: NORMAL|REVERSE|HIGHLIGHT]
 *    branch <name>
 *    checkout <name> | switch <name>
 *    merge <name> [id: "x"] [tag: "y"] [type: ...]
 * ```
 *
 * Starts on the implicit `main` branch. Each `commit` advances the timeline; `branch`
 * creates + switches; `checkout`/`switch` switches; `merge` creates a merge commit on the
 * current branch and records the [GitMerge]. Commit IDs default to a numeric counter.
 */
internal object GitGraphParser : MermaidDiagramParser {
    override val keyword: String = "gitGraph"
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight
    override val consumesHeaderLine: Boolean = true

    private const val MAIN = "main"

    override fun parse(lines: List<String>): ParseResult.GitGraphDiagram {
        val commits = mutableListOf<GitCommit>()
        val branchesOrder = mutableListOf(MAIN)
        val commitsByBranch = linkedMapOf(MAIN to mutableListOf<GitCommit>())
        val merges = mutableListOf<GitMerge>()
        var currentBranch = MAIN
        var seq = 0
        var autoId = 0

        fun nextAutoId(): String {
            autoId += 1
            return "c-$autoId"
        }

        lines.forEachIndexed { index, line ->
            if (index == 0) return@forEachIndexed // skip `gitGraph` header
            val tokens = line.trim().split(Regex("\\s+"), limit = 2).filter { it.isNotEmpty() }
            if (tokens.isEmpty()) return@forEachIndexed
            val cmd = tokens[0].lowercase()
            val rest = tokens.getOrNull(1)?.trim().orEmpty()

            when (cmd) {
                "commit" -> {
                    val (type, id, tag) = parseAttrs(rest)
                    val commit = GitCommit(
                        id = id ?: nextAutoId(),
                        seq = seq,
                        branch = currentBranch,
                        type = type,
                        tag = tag,
                    )
                    commits.add(commit)
                    commitsByBranch.getValue(currentBranch).add(commit)
                    seq += 1
                }
                "branch" -> {
                    val name = rest.substringBefore(' ').trim()
                    if (name.isNotEmpty() && name !in commitsByBranch) {
                        commitsByBranch[name] = mutableListOf()
                        branchesOrder.add(name)
                        currentBranch = name
                    }
                }
                "checkout", "switch" -> {
                    val name = rest.substringBefore(' ').trim()
                    if (name in commitsByBranch) currentBranch = name
                }
                "merge" -> {
                    val fromBranch = rest.substringBefore(' ').trim()
                    val attrsSource = rest.substringAfter(' ', "").trim()
                    if (fromBranch in commitsByBranch && fromBranch != currentBranch) {
                        val (type, id, tag) = parseAttrs(attrsSource)
                        val mergeId = id ?: nextAutoId()
                        val commit = GitCommit(
                            id = mergeId,
                            seq = seq,
                            branch = currentBranch,
                            type = type,
                            tag = tag,
                            isMerge = true,
                        )
                        commits.add(commit)
                        commitsByBranch.getValue(currentBranch).add(commit)
                        merges.add(GitMerge(from = fromBranch, into = currentBranch, mergeCommitId = mergeId))
                        seq += 1
                    }
                }
            }
        }

        val branches = branchesOrder.map { name ->
            GitBranch(name = name, commits = commitsByBranch.getValue(name))
        }
        return ParseResult.GitGraphDiagram(
            direction = defaultDirection,
            branches = branches,
            commits = commits,
            merges = merges,
        )
    }

    /** Parse the optional `id:"x" tag:"y" type:NORMAL` attribute tail of commit/merge. */
    private fun parseAttrs(source: String): Triple<GitCommitType, String?, String?> {
        var type = GitCommitType.Normal
        var id: String? = null
        var tag: String? = null
        val typeMatch = Regex("""type:\s*(\w+)""", RegexOption.IGNORE_CASE).find(source)
        if (typeMatch != null) {
            type = when (typeMatch.groupValues[1].uppercase()) {
                "REVERSE" -> GitCommitType.Reverse
                "HIGHLIGHT" -> GitCommitType.Highlight
                else -> GitCommitType.Normal
            }
        }
        Regex("""id:\s*"([^"]*)\"""", RegexOption.IGNORE_CASE).find(source)?.let { id = it.groupValues[1] }
        Regex("""tag:\s*"([^"]*)\"""", RegexOption.IGNORE_CASE).find(source)?.let { tag = it.groupValues[1] }
        return Triple(type, id, tag)
    }
}
