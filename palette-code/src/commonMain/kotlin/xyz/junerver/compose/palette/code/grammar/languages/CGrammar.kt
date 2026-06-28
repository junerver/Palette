package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar

/**
 * C and C++ grammars, built on the shared [cFamilyGrammar] factory.
 *
 * Both share `//` line comments and non-nested `/* */` block comments (the factory's default regex
 * path), so no matcher is needed. Keyword lists follow the language standards (C89–C11, C++11–C++20).
 */
// region C ──────────────────────────────────────────────────────────────────────
private val cKeywords = listOf(
    "auto", "break", "case", "char", "const", "continue", "default", "do",
    "double", "else", "enum", "extern", "float", "for", "goto", "if",
    "inline", "int", "long", "register", "restrict", "return", "short", "signed",
    "sizeof", "static", "struct", "switch", "typedef", "union", "unsigned", "void",
    "volatile", "while", "_Bool", "_Complex", "_Imaginary", "_Alignas", "_Alignof",
    "_Atomic", "_Generic", "_Noreturn", "_Static_assert", "_Thread_local",
)

private val cBooleans = listOf("true", "false", "NULL", "nullptr")

private val cPrimitiveTypes = listOf(
    "size_t", "ssize_t", "ptrdiff_t", "wchar_t", "int8_t", "int16_t", "int32_t", "int64_t",
    "uint8_t", "uint16_t", "uint32_t", "uint64_t", "FILE", "va_list", "jmp_buf",
)

internal val CGrammar: Grammar = cFamilyGrammar(
    keywords = cKeywords,
    booleans = cBooleans,
    primitiveTypes = cPrimitiveTypes,
)
// endregion ─────────────────────────────────────────────────────────────────────

// region C++ ────────────────────────────────────────────────────────────────────
private val cppKeywords = listOf(
    "alignas", "alignof", "and", "asm", "auto", "bitand", "bitor", "bool",
    "break", "case", "catch", "char", "char8_t", "char16_t", "char32_t", "class",
    "compl", "concept", "const", "consteval", "constexpr", "const_cast", "continue",
    "co_await", "co_return", "co_yield", "decltype", "default", "delete", "do",
    "double", "dynamic_cast", "else", "enum", "explicit", "export", "extern", "false",
    "float", "for", "friend", "goto", "if", "inline", "int", "long", "mutable",
    "namespace", "new", "noexcept", "not", "not_eq", "nullptr", "operator", "or",
    "or_eq", "private", "protected", "public", "register", "reinterpret_cast",
    "requires", "return", "short", "signed", "sizeof", "static", "static_assert",
    "static_cast", "struct", "switch", "template", "this", "thread_local", "throw",
    "true", "try", "typedef", "typeid", "typename", "union", "unsigned", "using",
    "virtual", "void", "volatile", "wchar_t", "while", "xor", "xor_eq",
)

private val cppBooleans = listOf("true", "false", "nullptr", "NULL")

private val cppPrimitiveTypes = listOf(
    "std", "string", "vector", "map", "set", "pair", "size_t", "ptrdiff_t",
    "int8_t", "int16_t", "int32_t", "int64_t", "uint8_t", "uint16_t", "uint32_t", "uint64_t",
)

internal val CppGrammar: Grammar = cFamilyGrammar(
    keywords = cppKeywords,
    booleans = cppBooleans,
    primitiveTypes = cppPrimitiveTypes,
)
// endregion ─────────────────────────────────────────────────────────────────────
