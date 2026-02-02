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
    
    // Data Display Components
    data object Table : NavItem("table", "表格", Icons.Default.TableChart, ComponentCategory.DATA_DISPLAY)
    data object List : NavItem("list", "列表", Icons.Default.List, ComponentCategory.DATA_DISPLAY)
    data object Descriptions : NavItem("descriptions", "描述列表", Icons.Default.Description, ComponentCategory.DATA_DISPLAY)
    data object Statistic : NavItem("statistic", "统计数值", Icons.Default.BarChart, ComponentCategory.DATA_DISPLAY)
    data object Timeline : NavItem("timeline", "时间轴", Icons.Default.Timeline, ComponentCategory.DATA_DISPLAY)
    data object Tree : NavItem("tree", "树形控件", Icons.Default.AccountTree, ComponentCategory.DATA_DISPLAY)
    data object Image : NavItem("image", "图片", Icons.Default.Image, ComponentCategory.DATA_DISPLAY)
    data object Carousel : NavItem("carousel", "轮播图", Icons.Default.ViewCarousel, ComponentCategory.DATA_DISPLAY)
    data object Pagination : NavItem("pagination", "分页器", Icons.Default.Pages, ComponentCategory.DATA_DISPLAY)
    data object Empty : NavItem("empty", "空状态", Icons.Default.HourglassEmpty, ComponentCategory.DATA_DISPLAY)
    
    // Layout Components (additional)
    data object Card : NavItem("card", "卡片", Icons.Default.CreditCard, ComponentCategory.LAYOUT)
    data object Avatar : NavItem("avatar", "头像", Icons.Default.AccountCircle, ComponentCategory.LAYOUT)
    data object Collapse : NavItem("collapse", "折叠面板", Icons.Default.ExpandMore, ComponentCategory.LAYOUT)
    
    // Feedback Components (additional)
    data object Tag : NavItem("tag", "标签", Icons.Default.Label, ComponentCategory.FEEDBACK)

    companion object {
        val all = listOf(
            Button, Checkbox, Radio, Switch, Slider, TextField, Rate,
            Loading, Progress, Badge, Dialog, Toast, Skeleton,
            Toolbar,
            RowLayout, BorderBox,
            Table, List, Descriptions, Statistic, Timeline, Tree, Image, Carousel, Pagination, Empty,
            Card, Avatar, Collapse,
            Tag
        )
        fun fromRoute(route: String?) = all.find { it.route == route }
    }
}

enum class ComponentCategory(val label: String) {
    FORM("表单组件"),
    FEEDBACK("反馈组件"),
    NAVIGATION("导航组件"),
    LAYOUT("布局组件"),
    DATA_DISPLAY("数据展示")
}

