package xyz.junerver.compose.palette.components.code

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.palette.code.CodeTokenType
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Immutable
data class CodeBlockColors(
    val backgroundColor: Color,
    val borderColor: Color,
    val contentColor: Color,
    val keywordColor: Color,
    val stringColor: Color,
    val numberColor: Color,
    val commentColor: Color,
    val functionColor: Color,
    val typeColor: Color,
    val annotationColor: Color,
    val operatorColor: Color,
    val punctuationColor: Color,
    val lineNumberColor: Color = contentColor,
    val highlightedLineColor: Color = Color.Unspecified,
    val headerBackgroundColor: Color = Color.Unspecified,
) {
    fun colorFor(type: CodeTokenType): Color =
        when (type) {
            CodeTokenType.Plain -> contentColor
            CodeTokenType.Keyword -> keywordColor
            CodeTokenType.StringLiteral -> stringColor
            CodeTokenType.NumberLiteral -> numberColor
            CodeTokenType.Comment -> commentColor
            CodeTokenType.Function -> functionColor
            CodeTokenType.Type -> typeColor
            CodeTokenType.Annotation -> annotationColor
            CodeTokenType.Operator -> operatorColor
            CodeTokenType.Punctuation -> punctuationColor
            CodeTokenType.Inserted -> stringColor
            CodeTokenType.Deleted -> commentColor
            CodeTokenType.Property -> functionColor
            CodeTokenType.Variable -> contentColor
            CodeTokenType.Constant -> keywordColor
            CodeTokenType.Builtin -> functionColor
            CodeTokenType.ClassName -> typeColor
            CodeTokenType.Namespace -> typeColor
            // Prism-aligned additions map onto existing semantic colors by default; theme
            // tokens can be added later if a distinct color is warranted.
            CodeTokenType.Boolean -> keywordColor
            CodeTokenType.Char -> stringColor
            CodeTokenType.Regex -> stringColor
            CodeTokenType.Symbol -> annotationColor
            CodeTokenType.Url -> annotationColor
            CodeTokenType.Selector -> typeColor
            CodeTokenType.Tag -> typeColor
            CodeTokenType.AttrName -> annotationColor
            CodeTokenType.AttrValue -> stringColor
            CodeTokenType.Doctype -> commentColor
            CodeTokenType.Entity -> annotationColor
            CodeTokenType.Prolog -> commentColor
            CodeTokenType.Cdata -> commentColor
            CodeTokenType.Atrule -> annotationColor
            CodeTokenType.Bold -> contentColor
            CodeTokenType.Italic -> contentColor
            CodeTokenType.Important -> keywordColor
        }
}

object CodeBlockDefaults {
    @Composable
    fun colors(): CodeBlockColors {
        val tokens = PaletteTheme.componentThemes.utility
        return CodeBlockColors(
            backgroundColor = tokens.codeBlockBackgroundColor,
            borderColor = tokens.codeBlockBorderColor,
            contentColor = tokens.codeBlockContentColor,
            keywordColor = tokens.codeBlockKeywordColor,
            stringColor = tokens.codeBlockStringColor,
            numberColor = tokens.codeBlockNumberColor,
            commentColor = tokens.codeBlockCommentColor,
            functionColor = tokens.codeBlockFunctionColor,
            typeColor = tokens.codeBlockTypeColor,
            annotationColor = tokens.codeBlockAnnotationColor,
            operatorColor = tokens.codeBlockOperatorColor,
            punctuationColor = tokens.codeBlockPunctuationColor,
            lineNumberColor = tokens.codeBlockLineNumberColor,
            highlightedLineColor = tokens.codeBlockHighlightedLineColor,
            headerBackgroundColor = tokens.codeBlockHeaderBackgroundColor,
        )
    }

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.utility.codeBlockTextStyle

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.utility.codeBlockCornerRadius

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.utility.codeBlockBorderWidth

    @Composable
    fun padding(): Dp = PaletteTheme.componentThemes.utility.codeBlockPadding
}
