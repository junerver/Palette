package xyz.junerver.compose.palette

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.core.tokens.PaletteComponentThemes
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.demo.*
import xyz.junerver.compose.palette.theme.*

@Composable
fun App() {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val darkTheme = isDarkTheme(themeMode, systemDark)
    var language by rememberSaveable { mutableStateOf(Language.ZH_CN) }
    var tokenConfig by remember { mutableStateOf(DemoThemeTokenConfig()) }
    val baseColors = remember(darkTheme) {
        if (darkTheme) PaletteColors.dark() else PaletteColors.light()
    }
    val colors = remember(baseColors, tokenConfig) { tokenConfig.resolveColors(baseColors) }
    val spacing = remember(tokenConfig) { tokenConfig.resolveSpacing() }
    val shapes = remember(tokenConfig) { tokenConfig.resolveShapes() }
    val typography = remember(tokenConfig) { tokenConfig.resolveTypography() }
    val opacity = remember(tokenConfig) { tokenConfig.resolveOpacity() }
    val motion = remember(tokenConfig) { tokenConfig.resolveMotion() }
    val elevation = remember(tokenConfig) { tokenConfig.resolveElevation() }
    val control = remember(tokenConfig) { tokenConfig.resolveControl() }
    val componentThemes =
        remember(colors, spacing, typography, opacity, motion, elevation, control, darkTheme) {
            PaletteComponentThemes.default(
                colors = colors,
                spacing = spacing,
                typography = typography,
                opacity = opacity,
                motion = motion,
                elevation = elevation,
                control = control,
                darkTheme = darkTheme,
            )
        }

    CompositionLocalProvider(
        LocalThemeMode provides themeMode,
        LocalSetThemeMode provides { ThemeManager.setThemeMode(it) },
        LocalLanguage provides language,
    ) {
        PaletteMaterialTheme(
            colors = colors,
            spacing = spacing,
            shapes = shapes,
            typography = typography,
            opacity = opacity,
            motion = motion,
            elevation = elevation,
            control = control,
            componentThemes = componentThemes,
            strings = language.toPaletteStrings(),
            darkTheme = darkTheme,
        ) {
            AppContent(
                themeMode = themeMode,
                onThemeModeChange = { ThemeManager.setThemeMode(it) },
                language = language,
                onLanguageChange = { language = it },
                tokenConfig = tokenConfig,
                onTokenConfigChange = { tokenConfig = it },
            )
        }
    }
}

@Composable
private fun AppContent(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    language: Language,
    onLanguageChange: (Language) -> Unit,
    tokenConfig: DemoThemeTokenConfig,
    onTokenConfigChange: (DemoThemeTokenConfig) -> Unit,
) {
    var selectedRoute by rememberSaveable { mutableStateOf("button") }

    Row(modifier = Modifier.fillMaxSize()) {
        SideNav(
            selectedRoute = selectedRoute,
            onRouteSelected = { selectedRoute = it },
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange,
            language = language,
            onLanguageChange = onLanguageChange,
            tokenConfig = tokenConfig,
            onTokenConfigChange = onTokenConfigChange,
        )

        Box(modifier = Modifier.fillMaxSize()) {
            MainContent(route = selectedRoute)
        }
    }
}

@Composable
private fun MainContent(route: String) {
    AnimatedContent(
        targetState = route,
        transitionSpec = {
            slideInHorizontally { it } + fadeIn() togetherWith
                slideOutHorizontally { -it } + fadeOut()
        },
        label = "content_transition",
    ) { targetRoute ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            when (targetRoute) {
                NavItem.Button.route -> ButtonDemo()
                NavItem.Checkbox.route -> CheckboxDemo()
                NavItem.Radio.route -> RadioDemo()
                NavItem.Switch.route -> SwitchDemo()
                NavItem.Slider.route -> SliderDemo()
                NavItem.TextField.route -> TextFieldDemo()
                NavItem.Rate.route -> RateDemo()
                NavItem.Form.route -> FormDemo()
                NavItem.Loading.route -> LoadingDemo()
                NavItem.Progress.route -> ProgressDemo()
                NavItem.Badge.route -> BadgeDemo()
                NavItem.Dialog.route -> DialogDemo()
                NavItem.Toast.route -> ToastDemo()
                NavItem.Skeleton.route -> SkeletonDemo()
                NavItem.Toolbar.route -> ToolbarDemo()
                NavItem.RowLayout.route -> RowLayoutDemo()
                NavItem.BorderBox.route -> BorderBoxDemo()
                NavItem.Table.route -> TableDemo()
                NavItem.List.route -> ListDemo()
                NavItem.Descriptions.route -> DescriptionsDemo()
                NavItem.Statistic.route -> StatisticDemo()
                NavItem.Chart.route -> ChartDemo()
                NavItem.Timeline.route -> TimelineDemo()
                NavItem.Tree.route -> TreeDemo()
                NavItem.Image.route -> ImageDemo()
                NavItem.Carousel.route -> CarouselDemo()
                NavItem.Pagination.route -> PaginationDemo()
                NavItem.Empty.route -> EmptyDemo()
                NavItem.Card.route -> CardDemo()
                NavItem.Avatar.route -> AvatarDemo()
                NavItem.Collapse.route -> CollapseDemo()
                NavItem.Tag.route -> TagDemo()
                NavItem.Popup.route -> PopupDemo()
                NavItem.ActionSheet.route -> ActionSheetDemo()
                NavItem.SearchBar.route -> SearchBarDemo()
                NavItem.ContextMenu.route -> ContextMenuDemo()
                NavItem.DashboardProgress.route -> DashboardProgressDemo()
                NavItem.Alert.route -> AlertDemo()
                NavItem.InputNumber.route -> InputNumberDemo()
                NavItem.Cascader.route -> CascaderDemo()
                NavItem.Transfer.route -> TransferDemo()
                NavItem.Calendar.route -> CalendarDemo()
                NavItem.DateRangePicker.route -> DateRangePickerDemo()
                NavItem.Segmented.route -> SegmentedDemo()
                NavItem.Popconfirm.route -> PopconfirmDemo()
                NavItem.Result.route -> ResultDemo()
                NavItem.Affix.route -> AffixDemo()
                NavItem.InputOTP.route -> InputOTPDemo()
                NavItem.Autocomplete.route -> AutocompleteDemo()
                NavItem.TreeSelect.route -> TreeSelectDemo()
                NavItem.ColorPicker.route -> ColorPickerDemo()
                NavItem.Grid.route -> GridDemo()
                NavItem.Space.route -> SpaceDemo()
                NavItem.InfiniteScroll.route -> InfiniteScrollDemo()
                NavItem.Backtop.route -> BacktopDemo()
                NavItem.Watermark.route -> WatermarkDemo()
                NavItem.QRCode.route -> QRCodeDemo()
                NavItem.Markdown.route -> MarkdownDemo()
                NavItem.Mermaid.route -> MermaidDemo()
                NavItem.FloatButton.route -> FloatButtonDemo()
                NavItem.PageHeader.route -> PageHeaderDemo()
                NavItem.Toggle.route -> ToggleDemo()
                NavItem.Mentions.route -> MentionsDemo()
                NavItem.CascaderPanel.route -> CascaderPanelDemo()
            }
        }
    }
}

@Composable
private fun SideNav(
    selectedRoute: String,
    onRouteSelected: (String) -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    language: Language,
    onLanguageChange: (Language) -> Unit,
    tokenConfig: DemoThemeTokenConfig,
    onTokenConfigChange: (DemoThemeTokenConfig) -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .width(280.dp)
                .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Column {
            HeaderSection(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                language = language,
                onLanguageChange = onLanguageChange,
                tokenConfig = tokenConfig,
                onTokenConfigChange = onTokenConfigChange,
            )

            NavItems(
                selectedRoute = selectedRoute,
                onRouteSelected = onRouteSelected,
            )
        }
    }
}

@Composable
private fun HeaderSection(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    language: Language,
    onLanguageChange: (Language) -> Unit,
    tokenConfig: DemoThemeTokenConfig,
    onTokenConfigChange: (DemoThemeTokenConfig) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
        Text(
            text = "Palette",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Compose Multiplatform 组件库",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(14.dp))

        CompactPreferencePanel(
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange,
            language = language,
            onLanguageChange = onLanguageChange,
            tokenConfig = tokenConfig,
            onTokenConfigChange = onTokenConfigChange,
        )
    }
}

@Composable
private fun CompactPreferencePanel(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    language: Language,
    onLanguageChange: (Language) -> Unit,
    tokenConfig: DemoThemeTokenConfig,
    onTokenConfigChange: (DemoThemeTokenConfig) -> Unit,
) {
    var showTokenDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CompactPreferenceRow(label = "主题") {
            ThemeModeChip(
                label = "浅色",
                icon = Icons.Default.LightMode,
                selected = themeMode == ThemeMode.LIGHT,
                onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                modifier = Modifier.weight(1f),
            )
            ThemeModeChip(
                label = "深色",
                icon = Icons.Default.DarkMode,
                selected = themeMode == ThemeMode.DARK,
                onClick = { onThemeModeChange(ThemeMode.DARK) },
                modifier = Modifier.weight(1f),
            )
            ThemeModeChip(
                label = "系统",
                icon = Icons.Default.Settings,
                selected = themeMode == ThemeMode.SYSTEM,
                onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f),
            )
        }

        CompactPreferenceRow(label = "语言") {
            TextChip(
                label = "中文",
                selected = language == Language.ZH_CN,
                onClick = { onLanguageChange(Language.ZH_CN) },
                modifier = Modifier.weight(1f),
            )
            TextChip(
                label = "English",
                selected = language == Language.EN_US,
                onClick = { onLanguageChange(Language.EN_US) },
                modifier = Modifier.weight(1f),
            )
        }

        CompactPreferenceRow(label = "样式") {
            TextChip(
                label = "全局 Token",
                selected = tokenConfig.hasAnyCustomValue(),
                onClick = { showTokenDialog = true },
                modifier = Modifier.weight(1f),
            )
            TextChip(
                label = if (tokenConfig.customCount == 0) "未自定义" else "${tokenConfig.customCount} 项",
                selected = tokenConfig.hasAnyCustomValue(),
                onClick = { showTokenDialog = true },
                modifier = Modifier.weight(1f),
            )
        }
    }

    if (showTokenDialog) {
        GlobalTokenDialog(
            config = tokenConfig,
            onConfigChange = onTokenConfigChange,
            onDismiss = { showTokenDialog = false },
        )
    }
}

@Composable
private fun CompactPreferenceRow(
    label: String,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(34.dp),
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
private fun TextChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface

    Row(
        modifier =
            modifier
                .height(28.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(backgroundColor)
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

@Composable
private fun ThemeModeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface

    Row(
        modifier =
            modifier
                .height(28.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(backgroundColor)
                .clickable(onClick = onClick)
                .padding(horizontal = 5.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

@Composable
private fun NavItems(
    selectedRoute: String,
    onRouteSelected: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredItems = remember(searchQuery) {
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            NavItem.all
        } else {
            NavItem.all.filter { it.matchesSearch(query) }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
    ) {
        ComponentSearchField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 8.dp),
        )

        Text(
            text = if (searchQuery.isBlank()) "组件" else "组件 ${filteredItems.size}/${NavItem.all.size}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
        ) {
            if (filteredItems.isEmpty()) {
                Text(
                    text = "未找到组件",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                )
            } else {
                filteredItems.forEach { item ->
                    NavItemRow(
                        item = item,
                        selected = item.route == selectedRoute,
                        onClick = { onRouteSelected(item.route) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ComponentSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    val contentColor = MaterialTheme.colorScheme.onSurface
    val hintColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier =
            modifier
                .height(36.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                .padding(start = 10.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = hintColor,
            modifier = Modifier.size(18.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "搜索组件",
                    style = MaterialTheme.typography.bodySmall,
                    color = hintColor,
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = contentColor),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (value.isNotEmpty()) {
            IconButton(
                onClick = { onValueChange("") },
                modifier = Modifier.size(26.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "清空搜索",
                    tint = hintColor,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

private fun NavItem.matchesSearch(query: String): Boolean {
    val normalizedQuery = query.lowercase()
    return label.lowercase().contains(normalizedQuery) ||
        route.lowercase().contains(normalizedQuery) ||
        category.label.lowercase().contains(normalizedQuery) ||
        category.name.lowercase().contains(normalizedQuery)
}

@Composable
private fun NavItemRow(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable(onClick = onClick)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
        )
    }
}
