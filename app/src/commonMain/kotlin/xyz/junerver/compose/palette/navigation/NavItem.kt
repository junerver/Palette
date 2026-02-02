package xyz.junerver.compose.palette

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val category: ComponentCategory
) {
    data object Button : NavItem("button", "按钮", Icons.Default.SmartButton, ComponentCategory.FORM)
    data object Checkbox : NavItem("checkbox", "复选框", Icons.Default.CheckBox, ComponentCategory.FORM)
    data object Radio : NavItem("radio", "单选框", Icons.Default.RadioButtonChecked, ComponentCategory.FORM)
    data object Switch : NavItem("switch", "开关", Icons.Default.ToggleOn, ComponentCategory.FORM)
    data object Slider : NavItem("slider", "滑块", Icons.Default.LinearScale, ComponentCategory.FORM)
    data object TextField : NavItem("textfield", "输入框", Icons.Default.Edit, ComponentCategory.FORM)
    data object Rate : NavItem("rate", "评分", Icons.Default.Star, ComponentCategory.FORM)
    data object Loading : NavItem("loading", "加载", Icons.Default.Refresh, ComponentCategory.FEEDBACK)
    data object Progress : NavItem("progress", "进度条", Icons.Default.TrendingUp, ComponentCategory.FEEDBACK)
    data object Badge : NavItem("badge", "徽章", Icons.Filled.Label, ComponentCategory.FEEDBACK)
    data object Dialog : NavItem("dialog", "对话框", Icons.Filled.Message, ComponentCategory.FEEDBACK)
    data object Toast : NavItem("toast", "轻提示", Icons.Default.Notifications, ComponentCategory.FEEDBACK)
    data object Skeleton : NavItem("skeleton", "骨架屏", Icons.Default.ViewModule, ComponentCategory.FEEDBACK)
    data object Toolbar : NavItem("toolbar", "工具栏", Icons.Default.Menu, ComponentCategory.NAVIGATION)
    data object RowLayout : NavItem("row", "行布局", Icons.Default.ViewColumn, ComponentCategory.LAYOUT)
    data object BorderBox : NavItem("borderbox", "边框容器", Icons.Default.ViewColumn, ComponentCategory.LAYOUT)

    companion object {
        val all = listOf(
            Button, Checkbox, Radio, Switch, Slider, TextField, Rate,
            Loading, Progress, Badge, Dialog, Toast, Skeleton,
            Toolbar,
            RowLayout, BorderBox
        )
        fun fromRoute(route: String?) = all.find { it.route == route }
    }
}

enum class ComponentCategory(val label: String) {
    FORM("表单组件"),
    FEEDBACK("反馈组件"),
    NAVIGATION("导航组件"),
    LAYOUT("布局组件")
}

