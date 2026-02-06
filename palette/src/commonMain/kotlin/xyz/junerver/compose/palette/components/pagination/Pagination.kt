package xyz.junerver.compose.palette.components.pagination

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PPagination(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    simple: Boolean = false,
    colors: PaginationColors = PaginationDefaults.colors()
) {
    require(currentPage in 1..totalPages) { "currentPage must be in 1..totalPages" }
    require(totalPages > 0) { "totalPages must be positive" }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(PaginationDefaults.Spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PaginationButton(
            text = "‹",
            enabled = currentPage > 1,
            onClick = { onPageChange(currentPage - 1) },
            colors = colors
        )
        
        if (!simple) {
            val pages = calculatePageNumbers(currentPage, totalPages)
            pages.forEach { page ->
                when (page) {
                    -1 -> Text(
                        text = PaletteTheme.strings.paginationEllipsis,
                        color = colors.textColor,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    else -> PaginationButton(
                        text = page.toString(),
                        isActive = page == currentPage,
                        onClick = { onPageChange(page) },
                        colors = colors
                    )
                }
            }
        } else {
            Text(
                text = PaletteTheme.strings.tablePaginationSummary(currentPage, totalPages),
                color = colors.textColor,
                style = PaletteTheme.typography.body
            )
        }
        
        PaginationButton(
            text = "›",
            enabled = currentPage < totalPages,
            onClick = { onPageChange(currentPage + 1) },
            colors = colors
        )
    }
}

@Composable
private fun PaginationButton(
    text: String,
    onClick: () -> Unit,
    colors: PaginationColors,
    isActive: Boolean = false,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.defaultMinSize(
            minWidth = PaginationDefaults.MinTouchSize,
            minHeight = PaginationDefaults.MinTouchSize
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = when {
                !enabled -> colors.disabledColor
                isActive -> colors.activeColor
                else -> colors.textColor
            },
            style = PaletteTheme.typography.body
        )
    }
}

internal fun calculatePageNumbers(current: Int, total: Int): List<Int> {
    if (total <= 7) {
        return (1..total).toList()
    }
    
    val result = mutableListOf<Int>()
    result.add(1)
    
    when {
        current <= 4 -> {
            (2..5).forEach { result.add(it) }
            result.add(-1)
            result.add(total)
        }
        current >= total - 3 -> {
            result.add(-1)
            (total - 4 until total).forEach { result.add(it) }
            result.add(total)
        }
        else -> {
            result.add(-1)
            (current - 1..current + 1).forEach { result.add(it) }
            result.add(-1)
            result.add(total)
        }
    }
    
    return result
}
