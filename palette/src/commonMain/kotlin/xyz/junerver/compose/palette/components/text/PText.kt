package xyz.junerver.compose.palette.components.text

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * Theme-aware text component that automatically adapts to dark/light mode.
 *
 * This component uses [LocalContentColor] and [LocalTextStyle] from the theme,
 * ensuring text colors are appropriate for the current theme mode.
 *
 * @param text The text to be displayed
 * @param modifier The modifier to be applied to the text
 * @param color The color of the text. Defaults to [LocalContentColor]
 * @param fontSize The size of glyphs to use when painting the text
 * @param fontStyle The typeface variant to use when drawing the letters (e.g., italic)
 * @param fontWeight The typeface thickness to use when painting the text (e.g., [FontWeight.Bold])
 * @param fontFamily The font family to be used when rendering the text
 * @param letterSpacing The amount of space to add between each letter
 * @param textDecoration The decorations to paint on the text (e.g., an underline)
 * @param textAlign The alignment of the text within the lines of the paragraph
 * @param lineHeight Line height for the [Paragraph] in [TextUnit] unit
 * @param overflow How visual overflow should be handled
 * @param softWrap Whether the text should break at soft line breaks
 * @param maxLines An optional maximum number of lines for the text to span
 * @param minLines The minimum height in terms of minimum number of visible lines
 * @param onTextLayout Callback that is executed when a new text layout is calculated
 * @param style Style configuration for the text such as color, font, line height etc
 */
@Composable
fun PText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    // Use LocalContentColor when color is not explicitly specified
    val textColor = if (color == Color.Unspecified) {
        LocalContentColor.current
    } else {
        color
    }
    
    // Merge color into style to ensure it takes precedence
    val mergedStyle = style.copy(color = textColor)
    
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = mergedStyle
    )
}
