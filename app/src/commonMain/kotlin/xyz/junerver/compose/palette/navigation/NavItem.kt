package xyz.junerver.compose.palette

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val category: ComponentCategory,
) {
    data object Button : NavItem("button", "按钮", Icons.Default.SmartButton, ComponentCategory.FORM)

    data object Checkbox : NavItem("checkbox", "复选框", Icons.Default.CheckBox, ComponentCategory.FORM)

    data object Radio : NavItem("radio", "单选框", Icons.Default.RadioButtonChecked, ComponentCategory.FORM)

    data object Switch : NavItem("switch", "开关", Icons.Default.ToggleOn, ComponentCategory.FORM)

    data object Slider : NavItem("slider", "滑块", Icons.Default.LinearScale, ComponentCategory.FORM)

    data object TextField : NavItem("textfield", "输入框", Icons.Default.Edit, ComponentCategory.FORM)

    data object Rate : NavItem("rate", "评分", Icons.Default.Star, ComponentCategory.FORM)

    data object Form : NavItem("form", "表单", Icons.AutoMirrored.Filled.Assignment, ComponentCategory.FORM)

    data object Loading : NavItem("loading", "加载", Icons.Default.Refresh, ComponentCategory.FEEDBACK)

    data object Progress : NavItem("progress", "进度条", Icons.AutoMirrored.Filled.TrendingUp, ComponentCategory.FEEDBACK)

    data object Badge : NavItem("badge", "徽章", Icons.AutoMirrored.Filled.Label, ComponentCategory.FEEDBACK)

    data object Dialog : NavItem("dialog", "对话框", Icons.AutoMirrored.Filled.Message, ComponentCategory.FEEDBACK)

    data object Toast : NavItem("toast", "轻提示", Icons.Default.Notifications, ComponentCategory.FEEDBACK)

    data object Skeleton : NavItem("skeleton", "骨架屏", Icons.Default.ViewModule, ComponentCategory.FEEDBACK)

    data object Toolbar : NavItem("toolbar", "工具栏", Icons.Default.Menu, ComponentCategory.NAVIGATION)

    data object RowLayout : NavItem("row", "行布局", Icons.Default.ViewColumn, ComponentCategory.LAYOUT)

    data object BorderBox : NavItem("borderbox", "边框容器", Icons.Default.ViewColumn, ComponentCategory.LAYOUT)

    // Data Display Components
    data object Table : NavItem("table", "表格", Icons.Default.TableChart, ComponentCategory.DATA_DISPLAY)

    data object List : NavItem("list", "列表", Icons.AutoMirrored.Filled.List, ComponentCategory.DATA_DISPLAY)

    data object Descriptions : NavItem(
        "descriptions",
        "描述列表",
        Icons.Default.Description,
        ComponentCategory.DATA_DISPLAY,
    )

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
    data object Tag : NavItem("tag", "标签", Icons.AutoMirrored.Filled.Label, ComponentCategory.FEEDBACK)

    data object Popup : NavItem("popup", "弹出层", Icons.Default.CallToAction, ComponentCategory.FEEDBACK)

    data object ActionSheet : NavItem("actionsheet", "操作菜单", Icons.Default.MenuOpen, ComponentCategory.FEEDBACK)

    data object SearchBar : NavItem("searchbar", "搜索栏", Icons.Default.Search, ComponentCategory.FORM)

    data object ContextMenu : NavItem("contextmenu", "上下文菜单", Icons.Default.TouchApp, ComponentCategory.FEEDBACK)

    data object DashboardProgress : NavItem(
        "dashboardprogress",
        "仪表盘进度",
        Icons.Default.Speed,
        ComponentCategory.FEEDBACK,
    )

    data object Alert : NavItem("alert", "告警提示", Icons.Default.Warning, ComponentCategory.FEEDBACK)
    data object InputNumber : NavItem("inputnumber", "数字输入", Icons.Default.Pin, ComponentCategory.FORM)
    data object Cascader : NavItem("cascader", "级联选择", Icons.Default.AccountTree, ComponentCategory.FORM)
    data object Transfer : NavItem("transfer", "穿梭框", Icons.Default.SwapHoriz, ComponentCategory.FORM)
    data object Calendar : NavItem("calendar", "日历", Icons.Default.CalendarMonth, ComponentCategory.FORM)
    data object DateRangePicker : NavItem("daterangepicker", "日期范围", Icons.Default.DateRange, ComponentCategory.FORM)
    data object Segmented : NavItem("segmented", "分段控制器", Icons.Default.ToggleOn, ComponentCategory.FORM)
    data object Popconfirm : NavItem("popconfirm", "气泡确认", Icons.Default.HelpOutline, ComponentCategory.FEEDBACK)
    data object Result : NavItem("result", "结果页", Icons.Default.CheckCircleOutline, ComponentCategory.FEEDBACK)
    data object Affix : NavItem("affix", "固钉", Icons.Default.PushPin, ComponentCategory.NAVIGATION)
    data object InputOTP : NavItem("inputotp", "验证码输入", Icons.Default.Password, ComponentCategory.FORM)
    data object Autocomplete : NavItem("autocomplete", "自动完成", Icons.Default.Lightbulb, ComponentCategory.FORM)
    data object TreeSelect : NavItem("treeselect", "树形选择", Icons.Default.AccountTree, ComponentCategory.FORM)
    data object ColorPicker : NavItem("colorpicker", "颜色选择", Icons.Default.Palette, ComponentCategory.FORM)
    data object Grid : NavItem("grid", "栅格布局", Icons.Default.GridView, ComponentCategory.LAYOUT)
    data object Space : NavItem("space", "间距", Icons.Default.SpaceBar, ComponentCategory.LAYOUT)
    data object InfiniteScroll : NavItem("infinitescroll", "无限滚动", Icons.Default.VerticalAlignBottom, ComponentCategory.FEEDBACK)
    data object Backtop : NavItem("backtop", "回到顶部", Icons.Default.ArrowUpward, ComponentCategory.NAVIGATION)
    data object Watermark : NavItem("watermark", "水印", Icons.Default.Opacity, ComponentCategory.FEEDBACK)
    data object QRCode : NavItem("qrcode", "二维码", Icons.Default.QrCode, ComponentCategory.DATA_DISPLAY)
    data object Markdown : NavItem("markdown", "Markdown", Icons.Default.Article, ComponentCategory.DATA_DISPLAY)
    data object Mermaid : NavItem("mermaid", "Mermaid", Icons.Default.AccountTree, ComponentCategory.DATA_DISPLAY)
    data object FloatButton : NavItem("floatbutton", "浮动按钮", Icons.Default.AddCircle, ComponentCategory.FEEDBACK)
    data object PageHeader : NavItem("pageheader", "页头", Icons.Default.ArrowBack, ComponentCategory.NAVIGATION)
    data object Toggle : NavItem("toggle", "切换按钮", Icons.Default.ToggleOn, ComponentCategory.FORM)
    data object Mentions : NavItem("mentions", "提及输入", Icons.Default.AlternateEmail, ComponentCategory.FORM)
    data object CascaderPanel : NavItem("cascaderpanel", "级联面板", Icons.Default.AccountTree, ComponentCategory.FORM)

    companion object {
        val all =
            listOf(
                Button, Checkbox, Radio, Switch, Slider, TextField, Rate, Form,
                Loading, Progress, Badge, Dialog, Toast, Skeleton,
                Toolbar,
                RowLayout, BorderBox,
                Table, List, Descriptions, Statistic, Timeline, Tree, Image, Carousel, Pagination, Empty,
                Card, Avatar, Collapse,
                Tag, Popup, ActionSheet, SearchBar, ContextMenu, DashboardProgress,
                Alert, InputNumber, Cascader, Transfer, Calendar, DateRangePicker,
                Segmented, Popconfirm, Result, Affix, InputOTP,
                Autocomplete, TreeSelect, ColorPicker, Grid, Space,
                InfiniteScroll, Backtop, Watermark, QRCode, Markdown, Mermaid, FloatButton,
                PageHeader, Toggle, Mentions, CascaderPanel,
            )

        fun fromRoute(route: String?) = all.find { it.route == route }
    }
}

enum class ComponentCategory(val label: String) {
    FORM("表单组件"),
    FEEDBACK("反馈组件"),
    NAVIGATION("导航组件"),
    LAYOUT("布局组件"),
    DATA_DISPLAY("数据展示"),
}
