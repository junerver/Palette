package xyz.junerver.compose.palette

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val category: ComponentCategory
) {
    data object Checkbox : NavItem("checkbox", "复选框", Icons.Default.CheckBox, ComponentCategory.FORM)
    data object TextField : NavItem("textfield", "输入框", Icons.Default.Edit, ComponentCategory.FORM)
    data object Badge : NavItem("badge", "徽章", Icons.Default.Label, ComponentCategory.FEEDBACK)
    data object Toolbar : NavItem("toolbar", "工具栏", Icons.Default.Menu, ComponentCategory.NAVIGATION)
    data object RowLayout : NavItem("row", "行布局", Icons.Default.ViewColumn, ComponentCategory.LAYOUT)
    data object BorderBox : NavItem("borderbox", "边框容器", Icons.Default.ViewColumn, ComponentCategory.LAYOUT)

    companion object {
        val all = listOf(Checkbox, TextField, Badge, Toolbar, RowLayout, BorderBox)
        fun fromRoute(route: String?) = all.find { it.route == route }
    }
}

enum class ComponentCategory(val label: String) {
    FORM("表单组件"),
    FEEDBACK("反馈组件"),
    NAVIGATION("导航组件"),
    LAYOUT("布局组件")
}
