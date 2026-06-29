package xyz.junerver.compose.palette

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import xyz.junerver.compose.palette.theme.DemoThemeTokenConfig
import xyz.junerver.compose.palette.theme.colorTokenSpecs
import xyz.junerver.compose.palette.theme.componentThemeGroupSpecs
import xyz.junerver.compose.palette.theme.controlTokenSpecs
import xyz.junerver.compose.palette.theme.elevationTokenSpecs
import xyz.junerver.compose.palette.theme.motionTokenSpecs
import xyz.junerver.compose.palette.theme.opacityTokenSpecs
import xyz.junerver.compose.palette.theme.shapeTokenSpecs
import xyz.junerver.compose.palette.theme.spacingTokenSpecs
import xyz.junerver.compose.palette.theme.typographyTokenSpecs

@Composable
internal fun GlobalTokenDialog(
    config: DemoThemeTokenConfig,
    onConfigChange: (DemoThemeTokenConfig) -> Unit,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val coreTokenCount =
        colorTokenSpecs.size +
            spacingTokenSpecs.size +
            shapeTokenSpecs.size +
            typographyTokenSpecs.size +
            opacityTokenSpecs.size +
            motionTokenSpecs.size +
            elevationTokenSpecs.size +
            controlTokenSpecs.size

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth(0.88f)
                    .fillMaxHeight(0.86f)
                    .widthIn(max = 760.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 16.dp,
        ) {
            Column {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 16.dp, end = 12.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "全局样式预设",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "核心 token $coreTokenCount 项，组件分组 ${componentThemeGroupSpecs.size} 项，已自定义 ${config.customCount} 项",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    TextButton(
                        onClick = { onConfigChange(DemoThemeTokenConfig()) },
                        enabled = config.hasAnyCustomValue(),
                    ) {
                        Text("全部重置")
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                        )
                    }
                }

                HorizontalDivider()

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    PresetSection(title = "品牌色") {
                        PresetGrid(
                            items = colorPresets,
                            selected = { it.isSelected(config) },
                            onClick = { onConfigChange(it.apply(config)) },
                        )
                    }

                    PresetSection(title = "密度") {
                        PresetGrid(
                            items = densityPresets,
                            selected = { it.isSelected(config) },
                            onClick = { onConfigChange(it.apply(config)) },
                        )
                    }

                    PresetSection(title = "圆角") {
                        PresetGrid(
                            items = radiusPresets,
                            selected = { it.isSelected(config) },
                            onClick = { onConfigChange(it.apply(config)) },
                        )
                    }

                    PresetSection(title = "状态效果") {
                        PresetGrid(
                            items = effectPresets,
                            selected = { it.isSelected(config) },
                            onClick = { onConfigChange(it.apply(config)) },
                        )
                    }

                    TokenCoverageSection()
                }

                HorizontalDivider()

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("完成")
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}

@Composable
private fun PresetGrid(
    items: List<StylePreset>,
    selected: (StylePreset) -> Boolean,
    onClick: (StylePreset) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { item ->
                    PresetOption(
                        preset = item,
                        selected = selected(item),
                        onClick = { onClick(item) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PresetOption(
    preset: StylePreset,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(9.dp)
    val borderColor =
        if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        }
    val backgroundColor =
        if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        }

    Row(
        modifier =
            modifier
                .height(62.dp)
                .clip(shape)
                .background(backgroundColor)
                .border(1.dp, borderColor, shape)
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        preset.swatch?.let { swatch ->
            Box(
                modifier =
                    Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(swatch)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(7.dp)),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = preset.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = preset.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TokenCoverageSection() {
    PresetSection(title = "可配置 Token 清单") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TokenCoverageRow(
                title = "PaletteColors",
                count = colorTokenSpecs.size,
                content = colorTokenSpecs.joinToString(" / ") { it.label },
            )
            TokenCoverageRow(
                title = "PaletteSpacing",
                count = spacingTokenSpecs.size,
                content = spacingTokenSpecs.joinToString(" / ") { it.label },
            )
            TokenCoverageRow(
                title = "PaletteShapes",
                count = shapeTokenSpecs.size,
                content = shapeTokenSpecs.joinToString(" / ") { it.label },
            )
            TokenCoverageRow(
                title = "PaletteTypography",
                count = typographyTokenSpecs.size,
                content = typographyTokenSpecs.joinToString(" / ") { it.label },
            )
            TokenCoverageRow(
                title = "PaletteOpacity",
                count = opacityTokenSpecs.size,
                content = opacityTokenSpecs.joinToString(" / ") { it.label },
            )
            TokenCoverageRow(
                title = "PaletteMotion",
                count = motionTokenSpecs.size,
                content = motionTokenSpecs.joinToString(" / ") { it.label },
            )
            TokenCoverageRow(
                title = "PaletteElevation",
                count = elevationTokenSpecs.size,
                content = elevationTokenSpecs.joinToString(" / ") { it.label },
            )
            TokenCoverageRow(
                title = "PaletteControlTokens",
                count = controlTokenSpecs.size,
                content = controlTokenSpecs.joinToString(" / ") { it.label },
            )
            TokenCoverageRow(
                title = "PaletteComponentThemes",
                count = componentThemeGroupSpecs.size,
                content = componentThemeGroupSpecs.joinToString(" / ") { it.key.removePrefix("componentThemes.") },
            )
        }
    }
}

@Composable
private fun TokenCoverageRow(
    title: String,
    count: Int,
    content: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "$count 项",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = content,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private data class StylePreset(
    val label: String,
    val description: String,
    val swatch: Color? = null,
    val apply: (DemoThemeTokenConfig) -> DemoThemeTokenConfig,
    val isSelected: (DemoThemeTokenConfig) -> Boolean,
)

private val colorPresets =
    listOf(
        colorPreset(
            label = "默认蓝",
            description = "Palette 默认",
            primary = null,
        ),
        colorPreset(
            label = "靛紫",
            description = "品牌主色",
            primary = Color(0xFF6366F1),
        ),
        colorPreset(
            label = "翠绿",
            description = "偏业务工具",
            primary = Color(0xFF059669),
            success = Color(0xFF16A34A),
        ),
        colorPreset(
            label = "日落橙",
            description = "强调操作",
            primary = Color(0xFFEA580C),
            warning = Color(0xFFF59E0B),
        ),
        colorPreset(
            label = "玫红",
            description = "高识别度",
            primary = Color(0xFFE11D48),
            error = Color(0xFFDC2626),
        ),
    )

private val densityPresets =
    listOf(
        densityPreset(
            label = "默认",
            description = "标准间距",
            spacing = emptyMap(),
            dp = emptyMap(),
            sp = emptyMap(),
        ),
        densityPreset(
            label = "紧凑",
            description = "更高信息密度",
            spacing =
                mapOf(
                    "spacing.extraSmall" to 3f,
                    "spacing.small" to 6f,
                    "spacing.medium" to 12f,
                    "spacing.large" to 18f,
                    "spacing.extraLarge" to 24f,
                ),
            dp =
                mapOf(
                    "control.small.height" to 22f,
                    "control.small.iconSize" to 14f,
                    "control.small.horizontalPadding" to 9f,
                    "control.small.verticalPadding" to 3f,
                    "control.medium.height" to 34f,
                    "control.medium.iconSize" to 18f,
                    "control.medium.horizontalPadding" to 12f,
                    "control.medium.verticalPadding" to 6f,
                    "control.large.height" to 38f,
                    "control.large.iconSize" to 20f,
                    "control.large.horizontalPadding" to 16f,
                    "control.large.verticalPadding" to 8f,
                ),
            sp =
                mapOf(
                    "typography.title.fontSize" to 16f,
                    "typography.title.lineHeight" to 22f,
                    "typography.body.fontSize" to 13f,
                    "typography.body.lineHeight" to 18f,
                    "typography.label.fontSize" to 11f,
                    "typography.label.lineHeight" to 15f,
                    "control.small.fontSize" to 12f,
                    "control.medium.fontSize" to 14f,
                    "control.large.fontSize" to 16f,
                ),
        ),
        densityPreset(
            label = "宽松",
            description = "触控友好",
            spacing =
                mapOf(
                    "spacing.extraSmall" to 6f,
                    "spacing.small" to 12f,
                    "spacing.medium" to 20f,
                    "spacing.large" to 32f,
                    "spacing.extraLarge" to 40f,
                ),
            dp =
                mapOf(
                    "control.small.height" to 30f,
                    "control.small.iconSize" to 18f,
                    "control.small.horizontalPadding" to 14f,
                    "control.small.verticalPadding" to 6f,
                    "control.medium.height" to 46f,
                    "control.medium.iconSize" to 22f,
                    "control.medium.horizontalPadding" to 20f,
                    "control.medium.verticalPadding" to 10f,
                    "control.large.height" to 54f,
                    "control.large.iconSize" to 26f,
                    "control.large.horizontalPadding" to 26f,
                    "control.large.verticalPadding" to 14f,
                ),
            sp =
                mapOf(
                    "typography.title.fontSize" to 20f,
                    "typography.title.lineHeight" to 28f,
                    "typography.body.fontSize" to 15f,
                    "typography.body.lineHeight" to 22f,
                    "typography.label.fontSize" to 13f,
                    "typography.label.lineHeight" to 18f,
                    "control.small.fontSize" to 14f,
                    "control.medium.fontSize" to 17f,
                    "control.large.fontSize" to 19f,
                ),
        ),
    )

private val radiusPresets =
    listOf(
        radiusPreset(
            label = "默认",
            description = "标准圆角",
            values = emptyMap(),
        ),
        radiusPreset(
            label = "直角",
            description = "硬朗边缘",
            values =
                mapOf(
                    "shapes.small.radius" to 0f,
                    "shapes.medium.radius" to 0f,
                    "shapes.large.radius" to 0f,
                    "control.small.cornerRadius" to 0f,
                    "control.medium.cornerRadius" to 0f,
                    "control.large.cornerRadius" to 0f,
                ),
        ),
        radiusPreset(
            label = "圆润",
            description = "柔和卡片",
            values =
                mapOf(
                    "shapes.small.radius" to 8f,
                    "shapes.medium.radius" to 14f,
                    "shapes.large.radius" to 22f,
                    "control.small.cornerRadius" to 10f,
                    "control.medium.cornerRadius" to 12f,
                    "control.large.cornerRadius" to 14f,
                ),
        ),
        radiusPreset(
            label = "胶囊",
            description = "按钮更圆",
            values =
                mapOf(
                    "shapes.small.radius" to 12f,
                    "shapes.medium.radius" to 20f,
                    "shapes.large.radius" to 28f,
                    "control.small.cornerRadius" to 999f,
                    "control.medium.cornerRadius" to 999f,
                    "control.large.cornerRadius" to 999f,
                ),
        ),
    )

private val effectPresets =
    listOf(
        effectPreset(
            label = "默认",
            description = "标准反馈",
            floats = emptyMap(),
            dp = emptyMap(),
            ints = emptyMap(),
        ),
        effectPreset(
            label = "轻量",
            description = "弱化阴影与状态",
            floats =
                mapOf(
                    "opacity.hover" to 0.04f,
                    "opacity.pressed" to 0.08f,
                    "opacity.selected" to 0.08f,
                    "opacity.focusRing" to 0.12f,
                    "opacity.overlay" to 0.32f,
                ),
            dp =
                mapOf(
                    "elevation.raised" to 0f,
                    "elevation.overlay" to 2f,
                    "elevation.modal" to 4f,
                    "elevation.floating" to 6f,
                ),
            ints =
                mapOf(
                    "motion.durationFast" to 100,
                    "motion.durationNormal" to 180,
                    "motion.durationSlow" to 260,
                    "motion.overlayEnter" to 140,
                    "motion.overlayExit" to 120,
                ),
        ),
        effectPreset(
            label = "明显",
            description = "更强状态反馈",
            floats =
                mapOf(
                    "opacity.hover" to 0.12f,
                    "opacity.pressed" to 0.18f,
                    "opacity.selected" to 0.18f,
                    "opacity.focusRing" to 0.32f,
                    "opacity.overlay" to 0.56f,
                ),
            dp =
                mapOf(
                    "elevation.raised" to 2f,
                    "elevation.overlay" to 8f,
                    "elevation.modal" to 14f,
                    "elevation.floating" to 18f,
                ),
            ints =
                mapOf(
                    "motion.durationFast" to 180,
                    "motion.durationNormal" to 300,
                    "motion.durationSlow" to 460,
                    "motion.overlayEnter" to 240,
                    "motion.overlayExit" to 220,
                ),
        ),
    )

private val allColorKeys = colorTokenSpecs.map { it.key }.toSet()
private val densityDpKeys =
    (
        spacingTokenSpecs.map { it.key } +
            listOf(
                "control.small.height",
                "control.small.iconSize",
                "control.small.horizontalPadding",
                "control.small.verticalPadding",
                "control.medium.height",
                "control.medium.iconSize",
                "control.medium.horizontalPadding",
                "control.medium.verticalPadding",
                "control.large.height",
                "control.large.iconSize",
                "control.large.horizontalPadding",
                "control.large.verticalPadding",
            )
    ).toSet()
private val densitySpKeys =
    (
        typographyTokenSpecs.map { it.key } +
            listOf(
                "control.small.fontSize",
                "control.medium.fontSize",
                "control.large.fontSize",
            )
    ).toSet()
private val radiusDpKeys =
    (
        shapeTokenSpecs.map { it.key } +
            listOf(
                "control.small.cornerRadius",
                "control.medium.cornerRadius",
                "control.large.cornerRadius",
            )
    ).toSet()
private val effectFloatKeys =
    setOf(
        "opacity.hover",
        "opacity.pressed",
        "opacity.selected",
        "opacity.focusRing",
        "opacity.overlay",
    )
private val effectDpKeys =
    setOf(
        "elevation.raised",
        "elevation.overlay",
        "elevation.modal",
        "elevation.floating",
    )
private val effectIntKeys = motionTokenSpecs.map { it.key }.toSet()

private fun colorPreset(
    label: String,
    description: String,
    primary: Color?,
    success: Color? = null,
    warning: Color? = null,
    error: Color? = null,
): StylePreset {
    val colors =
        buildMap {
            if (primary != null) {
                put("colors.primary", primary)
                put("colors.info", primary)
            }
            if (success != null) put("colors.success", success)
            if (warning != null) put("colors.warning", warning)
            if (error != null) {
                put("colors.error", error)
                put("colors.danger", error)
            }
        }

    return StylePreset(
        label = label,
        description = description,
        swatch = primary,
        apply = { config ->
            config.copy(colors = colors)
        },
        isSelected =
            if (colors.isEmpty()) {
                { config -> allColorKeys.none { it in config.colors } }
            } else {
                { config -> config.colors == colors }
            },
    )
}

private fun densityPreset(
    label: String,
    description: String,
    spacing: Map<String, Float>,
    dp: Map<String, Float>,
    sp: Map<String, Float>,
): StylePreset {
    val nextDp = spacing + dp
    return StylePreset(
        label = label,
        description = description,
        apply = { config ->
            config.copy(
                dp = config.dp.withoutKeys(densityDpKeys) + nextDp,
                sp = config.sp.withoutKeys(densitySpKeys) + sp,
            )
        },
        isSelected =
            if (nextDp.isEmpty() && sp.isEmpty()) {
                { config -> densityDpKeys.none { it in config.dp } && densitySpKeys.none { it in config.sp } }
            } else {
                { config ->
                    nextDp.all { (key, value) -> config.dp[key] == value } &&
                        sp.all { (key, value) -> config.sp[key] == value }
                }
            },
    )
}

private fun radiusPreset(
    label: String,
    description: String,
    values: Map<String, Float>,
): StylePreset =
    StylePreset(
        label = label,
        description = description,
        apply = { config ->
            config.copy(dp = config.dp.withoutKeys(radiusDpKeys) + values)
        },
        isSelected =
            if (values.isEmpty()) {
                { config -> radiusDpKeys.none { it in config.dp } }
            } else {
                { config -> values.all { (key, value) -> config.dp[key] == value } }
            },
    )

private fun effectPreset(
    label: String,
    description: String,
    floats: Map<String, Float>,
    dp: Map<String, Float>,
    ints: Map<String, Int>,
): StylePreset =
    StylePreset(
        label = label,
        description = description,
        apply = { config ->
            config.copy(
                floats = config.floats.withoutKeys(effectFloatKeys) + floats,
                dp = config.dp.withoutKeys(effectDpKeys) + dp,
                ints = config.ints.withoutKeys(effectIntKeys) + ints,
            )
        },
        isSelected =
            if (floats.isEmpty() && dp.isEmpty() && ints.isEmpty()) {
                { config ->
                    effectFloatKeys.none { it in config.floats } &&
                        effectDpKeys.none { it in config.dp } &&
                        effectIntKeys.none { it in config.ints }
                }
            } else {
                { config ->
                    floats.all { (key, value) -> config.floats[key] == value } &&
                        dp.all { (key, value) -> config.dp[key] == value } &&
                        ints.all { (key, value) -> config.ints[key] == value }
                }
            },
    )

private fun <T> Map<String, T>.withoutKeys(keys: Set<String>): Map<String, T> = filterKeys { it !in keys }
