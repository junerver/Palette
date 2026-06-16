package xyz.junerver.compose.palette.components.pageheader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple

@Composable
fun PPageHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    backText: String = "返回",
    extra: (@Composable () -> Unit)? = null,
    breadcrumb: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PageHeaderDefaults.backgroundColor())
            .padding(PageHeaderDefaults.Padding)
    ) {
        if (breadcrumb != null) {
            breadcrumb()
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(PageHeaderDefaults.Height),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                Row(
                    modifier = Modifier.clickableWithoutRipple { onBack() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(PageHeaderDefaults.BackIconSize),
                        tint = PageHeaderDefaults.backColor(),
                    )
                    Spacer(modifier = Modifier.width(PageHeaderDefaults.BackSpacing))
                    PText(
                        text = backText,
                        color = PageHeaderDefaults.backColor(),
                        fontSize = PageHeaderDefaults.TitleFontSize,
                    )
                }
                Spacer(modifier = Modifier.width(PageHeaderDefaults.Padding))
            }
            Column(modifier = Modifier.weight(1f)) {
                PText(
                    text = title,
                    color = PageHeaderDefaults.titleColor(),
                    fontSize = PageHeaderDefaults.TitleFontSize,
                    fontWeight = FontWeight.Bold,
                )
                if (subtitle != null) {
                    PText(
                        text = subtitle,
                        color = PageHeaderDefaults.subtitleColor(),
                        fontSize = PageHeaderDefaults.SubtitleFontSize,
                    )
                }
            }
            if (extra != null) {
                extra()
            }
        }
    }
}
