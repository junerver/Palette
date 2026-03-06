package xyz.junerver.compose.palette.components.datagrid

enum class DataGridSortDirection {
    Asc,
    Desc,
}

data class DataGridSortSpec<T>(
    val selector: (T) -> Comparable<*>?,
    val direction: DataGridSortDirection = DataGridSortDirection.Asc,
)

fun <T> filterRows(
    rows: List<T>,
    keyword: String,
    searchableText: (T) -> List<String>,
): List<T> {
    val normalized = keyword.trim()
    if (normalized.isEmpty()) return rows

    val filtered = ArrayList<T>(rows.size)
    for (row in rows) {
        val values = searchableText(row)
        for (index in values.indices) {
            if (values[index].contains(normalized, ignoreCase = true)) {
                filtered.add(row)
                break
            }
        }
    }
    return filtered
}

fun <T> sortRows(
    rows: List<T>,
    specs: List<DataGridSortSpec<T>>,
): List<T> {
    if (specs.isEmpty()) return rows
    return rows.sortedWith { left, right ->
        for (spec in specs) {
            val compared = compareNullableComparable(
                left = spec.selector(left),
                right = spec.selector(right),
            )
            if (compared != 0) {
                return@sortedWith if (spec.direction == DataGridSortDirection.Asc) compared else -compared
            }
        }
        0
    }
}

fun <T> paginateRows(
    rows: List<T>,
    pageIndex: Int,
    pageSize: Int,
): List<T> {
    if (pageSize <= 0) return rows
    if (pageIndex < 0) return emptyList()
    val start = pageIndex * pageSize
    if (start >= rows.size) return emptyList()
    val end = minOf(start + pageSize, rows.size)
    return rows.subList(start, end)
}

fun resolvePageCount(
    totalRows: Int,
    pageSize: Int,
): Int {
    if (totalRows <= 0) return 0
    if (pageSize <= 0) return 1
    val quotient = totalRows / pageSize
    val remainder = totalRows % pageSize
    return if (remainder == 0) quotient else quotient + 1
}

private fun compareNullableComparable(
    left: Comparable<*>?,
    right: Comparable<*>?,
): Int {
    if (left == null && right == null) return 0
    if (left == null) return 1
    if (right == null) return -1

    return runCatching {
        @Suppress("UNCHECKED_CAST")
        (left as Comparable<Any>).compareTo(right as Any)
    }.getOrElse {
        left.toString().compareTo(right.toString())
    }
}
