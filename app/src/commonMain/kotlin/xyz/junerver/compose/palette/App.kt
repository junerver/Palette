package xyz.junerver.compose.palette

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.demo.*
import xyz.junerver.compose.palette.ui.theme.PaletteTheme
import xyz.junerver.compose.palette.ui.theme.Primary

@Composable
fun App() {
    var darkTheme by rememberSaveable { mutableStateOf(false) }

    PaletteTheme(darkTheme = darkTheme) {
        AppContent(
            darkTheme = darkTheme,
            onThemeToggle = { darkTheme = !darkTheme }
        )
    }
}

@Composable
private fun AppContent(
    darkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    var selectedRoute by rememberSaveable { mutableStateOf("button") }

    Row(modifier = Modifier.fillMaxSize()) {
        SideNav(
            selectedRoute = selectedRoute,
            onRouteSelected = { selectedRoute = it },
            darkTheme = darkTheme,
            onThemeToggle = onThemeToggle
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
        Box(modifier = Modifier.fillMaxSize()) {
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
    darkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column {
            HeaderSection(darkTheme = darkTheme, onThemeToggle = onThemeToggle)

            NavItems(
                selectedRoute = selectedRoute,
                onRouteSelected = onRouteSelected
            )
        }
    }
}

@Composable
private fun HeaderSection(
    darkTheme: Boolean,
    onThemeToggle: () -> Unit
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

        ThemeToggle(
            darkTheme = darkTheme,
            onToggle = onThemeToggle
        )
    }
}

@Composable
private fun ThemeToggle(
    darkTheme: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (darkTheme) "深色模式" else "浅色模式",
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = if (darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
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


