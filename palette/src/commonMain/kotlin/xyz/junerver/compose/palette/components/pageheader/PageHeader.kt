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
            .padding(PageHeaderDefaults.padding())
    ) {
        if (breadcrumb != null) {
            breadcrumb()
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(PageHeaderDefaults.height()),
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
                        modifier = Modifier.size(PageHeaderDefaults.backIconSize()),
                        tint = PageHeaderDefaults.backColor(),
                    )
                    Spacer(modifier = Modifier.width(PageHeaderDefaults.backSpacing()))
                    PText(
                        text = backText,
                        color = PageHeaderDefaults.backColor(),
                        style = PageHeaderDefaults.backTextStyle(),
                    )
                }
                Spacer(modifier = Modifier.width(PageHeaderDefaults.backSectionSpacing()))
            }
            Column(modifier = Modifier.weight(1f)) {
                PText(
                    text = title,
                    color = PageHeaderDefaults.titleColor(),
                    style = PageHeaderDefaults.titleTextStyle(),
                )
                if (subtitle != null) {
                    PText(
                        text = subtitle,
                        color = PageHeaderDefaults.subtitleColor(),
                        style = PageHeaderDefaults.subtitleTextStyle(),
                    )
                }
            }
            if (extra != null) {
                extra()
            }
        }
    }
}
