package com.guillaume.taffin.structuriz.ast

import kotlin.math.max

class StructurizrLexer(private val text: String) {

    private var charPointer: Int = 0
    private var linePointer: Int = 0
    private var columnPointer: Int = 0

    fun hasNext(): Boolean = charPointer < text.length

    fun next(): StructurizrToken {

        for ((pattern, constructor) in patternSpec) {
            pattern.matchAt(text, charPointer)?.let {
                return if (it.value.lines().size == 1) {
                    makeSingleLineTokenAndMove(it.value, constructor)
                } else {
                    makeMultilineTokenAndMove(it.value, constructor)
                }
            }
        }

        return makeSingleLineTokenAndMove("", token("EofToken"))

    }

    private fun makeMultilineTokenAndMove(
        text: String,
        constructor: (String, Coordinates) -> StructurizrToken
    ): StructurizrToken {
        val lines = text.lines()
        val token = constructor(
            text,
            multilineCoordinates(lines)
        )

        linePointer += lines.size - 1
        columnPointer = lines.last().length
        charPointer += text.length
        return token
    }

    private fun multilineCoordinates(
        lines: List<String>
    ) = Coordinates(
        lineStart = linePointer,
        lineEnd = linePointer + lines.size - 1,
        colStart = columnPointer,
        colEnd = max(0, lines.last().length - 1)
    )

    private fun makeSingleLineTokenAndMove(
        text: String,
        constructor: (String, Coordinates) -> StructurizrToken
    ): StructurizrToken {
        val token = constructor(text, singleLineTokenCoordinates(text))
        charPointer += text.length
        columnPointer += text.length
        return token
    }

    private fun singleLineTokenCoordinates(text: String) = Coordinates(
        lineStart = linePointer,
        lineEnd = linePointer,
        colStart = columnPointer,
        colEnd = max(0, columnPointer + text.length - 1)
    )
}

val patternSpec: Array<Pair<Regex, TokenConstructor>> = arrayOf(
    keywordRegex("workspace") to token("WorkspaceKeywordToken"),
    keywordRegex("model") to token("ModelKeywordToken"),
    Regex("\\{") to token("OpenBraceToken"),
    Regex("}") to token("CloseBraceToken"),
    Regex("=") to token("AssignOperatorToken"),
    Regex("\\w[a-zA-Z0-9_-]*") to token("IdentifierToken"),
    Regex("\\s+") to token("WhitespaceToken"),
)

typealias TokenConstructor = (String, Coordinates) -> StructurizrToken

private fun keywordRegex(pattern: String) = pattern.toRegex(option = RegexOption.IGNORE_CASE)

data class Coordinates(
    val lineStart: Int,
    val lineEnd: Int,
    val colStart: Int,
    val colEnd: Int
)

data class StructurizrToken(
    val tokenId: String,
    val text: String,
    val coordinates: Coordinates,
)

fun token(id: String): TokenConstructor = { text, coordinates ->
    StructurizrToken(id, text, coordinates)
}
