package xyz.junerver.compose.palette

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.demo.*
import xyz.junerver.compose.palette.theme.*
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.ui.theme.Primary

@Composable
fun App() {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val darkTheme = isDarkTheme(themeMode, systemDark)
    var language by rememberSaveable { mutableStateOf(Language.ZH_CN) }

    CompositionLocalProvider(
        LocalThemeMode provides themeMode,
        LocalSetThemeMode provides { ThemeManager.setThemeMode(it) },
        LocalLanguage provides language,
    ) {
        PaletteMaterialTheme(
            colors = if (darkTheme) PaletteColors.dark() else PaletteColors.light(),
            strings = language.toPaletteStrings(),
            darkTheme = darkTheme
        ) {
            AppContent(
                themeMode = themeMode,
                onThemeModeChange = { ThemeManager.setThemeMode(it) },
                language = language,
                onLanguageChange = { language = it },
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
        label = "content_transition"
    ) { targetRoute ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column {
            HeaderSection(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                language = language,
                onLanguageChange = onLanguageChange,
            )

            NavItems(
                selectedRoute = selectedRoute,
                onRouteSelected = onRouteSelected
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
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Palette",
            style = MaterialTheme.typography.headlineMedium,
            color = Primary
        )
        Text(
            text = "Compose Multiplatform 组件库",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThemeModeSelector(
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        LanguageSelector(
            language = language,
            onLanguageChange = onLanguageChange,
        )
    }
}

@Composable
private fun LanguageSelector(
    language: Language,
    onLanguageChange: (Language) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Text(
            text = "语言",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextChip(
                label = "中文",
                selected = language == Language.ZH_CN,
                onClick = { onLanguageChange(Language.ZH_CN) },
                modifier = Modifier.weight(1f)
            )
            TextChip(
                label = "English",
                selected = language == Language.EN_US,
                onClick = { onLanguageChange(Language.EN_US) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TextChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) Primary else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
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
private fun ThemeModeSelector(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Text(
            text = "主题模式",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeModeChip(
                label = "浅色",
                icon = Icons.Default.LightMode,
                selected = themeMode == ThemeMode.LIGHT,
                onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                modifier = Modifier.weight(1f)
            )
            ThemeModeChip(
                label = "深色",
                icon = Icons.Default.DarkMode,
                selected = themeMode == ThemeMode.DARK,
                onClick = { onThemeModeChange(ThemeMode.DARK) },
                modifier = Modifier.weight(1f)
            )
            ThemeModeChip(
                label = "系统",
                icon = Icons.Default.Settings,
                selected = themeMode == ThemeMode.SYSTEM,
                onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ThemeModeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) Primary else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Composable
private fun NavItems(
    selectedRoute: String,
    onRouteSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = "组件",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        NavItem.all.forEach { item ->
            NavItemRow(
                item = item,
                selected = item.route == selectedRoute,
                onClick = { onRouteSelected(item.route) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun NavItemRow(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}
