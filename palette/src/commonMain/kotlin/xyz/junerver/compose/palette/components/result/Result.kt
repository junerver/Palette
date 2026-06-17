package xyz.junerver.compose.palette.components.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PResult(
    status: ResultStatus,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    icon: (@Composable (() -> Unit))? = null,
    extra: (@Composable (() -> Unit))? = null,
) {
    val displayTitle = title ?: ResultDefaults.defaultTitle(status)
    val displaySubtitle = subtitle ?: ResultDefaults.defaultSubtitle(status)
    val iconColor = ResultDefaults.iconColor(status)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (icon != null) {
            icon()
        } else {
            Icon(
                imageVector = iconForStatus(status),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(ResultDefaults.iconSize())
            )
        }

        Spacer(modifier = Modifier.height(ResultDefaults.iconToTitleSpacing()))

        PText(
            text = displayTitle,
            color = ResultDefaults.titleColor(),
            style = ResultDefaults.titleTextStyle(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ResultDefaults.titleToSubtitleSpacing()))

        PText(
            text = displaySubtitle,
            color = ResultDefaults.subtitleColor(),
            style = ResultDefaults.subtitleTextStyle(),
            textAlign = TextAlign.Center
        )

        if (extra != null) {
            Spacer(modifier = Modifier.height(ResultDefaults.subtitleToExtraSpacing()))
            extra()
        }
    }
}

private fun iconForStatus(status: ResultStatus): ImageVector = when (status) {
    ResultStatus.Success -> Icons.Outlined.CheckCircle
    ResultStatus.Error -> Icons.Default.Cancel
    ResultStatus.Info -> Icons.Default.Info
    ResultStatus.Warning -> Icons.Default.Warning
    ResultStatus.NotFound -> Icons.Outlined.SearchOff
    ResultStatus.NetworkError -> Icons.Outlined.WifiOff
}
