package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * Go grammar.
 *
 * Built on [cFamilyGrammar] with Go's keyword set and `templateLiterals` enabled so backtick raw
 * strings (`` `…` ``) classify as strings. Go has no block-comment nesting (the `/* */` regex path
 * suffices) and no string interpolation, so the factory defaults cover the rest.
 */
private val goKeywords = listOf(
    "break", "default", "func", "interface", "select", "case", "defer", "go",
    "map", "struct", "chan", "else", "goto", "package", "switch", "const",
    "fallthrough", "if", "range", "type", "continue", "for", "import", "return", "var",
)

private val goBooleans = listOf("true", "false", "nil", "iota")

private val goBuiltinTypes = listOf(
    "any", "bool", "byte", "comparable", "complex64", "complex128", "error",
    "float32", "float64", "int", "int8", "int16", "int32", "int64", "rune",
    "string", "uint", "uint8", "uint16", "uint32", "uint64", "uintptr",
    "true", "false", "nil",
    "append", "cap", "close", "complex", "copy", "delete", "imag", "len",
    "make", "new", "panic", "print", "println", "real", "recover", "min", "max", "clear",
)

internal val GoGrammar: Grammar = cFamilyGrammar(
    keywords = goKeywords,
    booleans = goBooleans,
    primitiveTypes = goBuiltinTypes,
    templateLiterals = true,
)
