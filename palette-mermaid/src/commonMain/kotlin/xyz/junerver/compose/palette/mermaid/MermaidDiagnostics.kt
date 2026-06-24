package xyz.junerver.compose.palette.mermaid

internal data class MermaidSequenceFragmentBuilder(
    val kind: MermaidSequenceFragmentKind,
    val label: String? = null,
    val branches: MutableList<MermaidSequenceBranchBuilder> = mutableListOf(),
    val startSequenceIndex: Int,
    var endSequenceIndex: Int = -1,
) {
    fun toFragment(): MermaidSequenceFragment = MermaidSequenceFragment(
        kind = kind,
        label = label,
        branches = branches.map { it.toBranch() },
        startSequenceIndex = startSequenceIndex,
        endSequenceIndex = if (endSequenceIndex == -1) startSequenceIndex else endSequenceIndex,
    )
}

internal data class MermaidSequenceBranchBuilder(
    val kind: String,
    val label: String? = null,
    val startSequenceIndex: Int,
    var endSequenceIndex: Int = -1,
) {
    fun toBranch(): MermaidSequenceBranch = MermaidSequenceBranch(
        kind = kind,
        label = label,
        startSequenceIndex = startSequenceIndex,
        endSequenceIndex = if (endSequenceIndex == -1) startSequenceIndex else endSequenceIndex,
    )
}

internal data class MermaidSequenceActivationBuilder(
    val participant: String,
    val startSequenceIndex: Int,
    var endSequenceIndex: Int = -1,
    val edgeIndexes: MutableList<Int> = mutableListOf(),
) {
    fun close(endIndex: Int) {
        endSequenceIndex = endIndex
    }

    fun toActivation(): MermaidSequenceActivation = MermaidSequenceActivation(
        participant = participant,
        startSequenceIndex = startSequenceIndex,
        endSequenceIndex = if (endSequenceIndex == -1) startSequenceIndex else endSequenceIndex,
    )
}

internal data class MermaidSequenceRegionBuilder(
    val kind: MermaidSequenceRegionKind,
    val label: String? = null,
    val color: String? = null,
    val participants: List<String> = emptyList(),
    val startSequenceIndex: Int,
    var endSequenceIndex: Int = -1,
) {
    fun toRegion(): MermaidSequenceRegion = MermaidSequenceRegion(
        kind = kind,
        label = label,
        color = color,
        participants = participants,
        startSequenceIndex = startSequenceIndex,
        endSequenceIndex = if (endSequenceIndex == -1) startSequenceIndex else endSequenceIndex,
    )
}
