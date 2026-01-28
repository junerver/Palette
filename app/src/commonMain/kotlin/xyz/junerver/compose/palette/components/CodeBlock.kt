package xyz.junerver.compose.palette.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import xyz.junerver.compose.palette.ui.theme.Success

@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    val iconTint by animateColorAsState(
        targetValue = if (copied) Success else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(end = 40.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = code.trimIndent(),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(code.trimIndent()))
                copied = true
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = if (copied) Icons.Default.Done else Icons.Default.ContentCopy,
                contentDescription = if (copied) "已复制" else "复制代码",
                tint = iconTint
            )
        }
    }
}
