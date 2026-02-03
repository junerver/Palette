package xyz.junerver.compose.palette.components.table

/**
 * Defines the scrolling behavior of a Table component.
 *
 * This enum controls how the Table handles scrolling in different layout contexts.
 */
enum class TableScrollBehavior {
    /**
     * Default behavior. The Table manages its own scrolling using LazyColumn.
     * User can scroll within the Table independently.
     *
     * Use this when the Table is standalone or needs its own scroll container.
     * Supports large datasets efficiently through lazy rendering.
     */
    Scrollable,

    /**
     * Embedded mode. The Table uses a regular Column instead of LazyColumn,
     * allowing it to work seamlessly inside scrollable parent containers
     * without requiring height constraints.
     *
     * **Performance Note:** This mode renders all rows immediately (no lazy loading).
     * For large datasets (100+ rows), consider enabling pagination to maintain performance:
     * ```
     * PTable(
     *     data = data,
     *     columns = columns,
     *     scrollBehavior = TableScrollBehavior.Embedded,
     *     optionsOf = {
     *         enablePagination = true
     *         pageSize = 20
     *     }
     * )
     * ```
     *
     * Use this when:
     * - Placing the Table inside Column(Modifier.verticalScroll())
     * - The Table is part of a larger scrollable layout
     * - You want "just works" behavior without height constraints
     * - Dataset is small (<50 rows) OR pagination is enabled
     */
    Embedded,

    /**
     * Fixed height mode. The Table manages its own scrolling but expects
     * an explicit height constraint via Modifier.height() or Modifier.heightIn().
     *
     * Use this when you need precise control over the Table's dimensions
     * while maintaining internal scrolling.
     */
    FixedHeight
}
