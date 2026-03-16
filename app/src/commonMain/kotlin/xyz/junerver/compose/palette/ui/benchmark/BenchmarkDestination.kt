package xyz.junerver.compose.palette.ui.benchmark

enum class BenchmarkDestination(val route: String) {
    CAROUSEL("carousel"),
    LIST("list"),
    SELECT("select"),
    DATAGRID("datagrid"),
    COMMAND_PALETTE("command_palette"),
    VIRTUAL_LIST("virtual_list"),
    ;

    companion object {
        fun fromRoute(route: String?): BenchmarkDestination =
            when (route) {
                CAROUSEL.route -> CAROUSEL
                LIST.route -> LIST
                SELECT.route -> SELECT
                DATAGRID.route -> DATAGRID
                COMMAND_PALETTE.route -> COMMAND_PALETTE
                VIRTUAL_LIST.route -> VIRTUAL_LIST
                else -> LIST
            }
    }
}
