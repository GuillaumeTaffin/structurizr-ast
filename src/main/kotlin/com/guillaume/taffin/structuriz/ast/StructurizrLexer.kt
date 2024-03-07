package com.guillaume.taffin.structuriz.ast

import kotlin.math.max

class StructurizrLexer(private val text: String) {

    private var charPointer: Int = 0
    private var linePointer: Int = 0
    private var columnPointer: Int = 0

    private val tokenFifo = ArrayDeque<StructurizrToken>()

    fun hasNext(): Boolean = tokenFifo.isNotEmpty() || charPointer < text.length

    fun next(): StructurizrToken? {

        if (tokenFifo.isNotEmpty()) return tokenFifo.removeFirst()

        for (tokenId in TokenId.entries) {
            tokenId.regex.matchAt(text, charPointer)?.let {
                return if (it.value.lines().size == 1) {
                    makeSingleLineTokenAndMove(it.value, tokenId.token())
                } else {
                    makeMultilineTokenAndMove(it.value, tokenId.token())
                }
            }
        }

        return null

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

    fun pushBack(token: StructurizrToken) {
        tokenFifo.addFirst(token)
    }
}

enum class TokenId(val regex: Regex) {
    WORKSPACE(keywordRegex("workspace")),
    MODEL(keywordRegex("model")),
    PERSON(keywordRegex("person")),
    EXTENDS(Regex("extends")),
    NAME(Regex("name")),
    DESCRIPTION(Regex("description")),
    OPEN_BRACE(Regex("\\{")),
    CLOSE_BRACE(Regex("}")),
    ASSIGN_OPERATOR(Regex("=")),
    IDENTIFIER(Regex("\\w[a-zA-Z0-9_-]*")),
    STRING(Regex("\"[^\n\"]*\"")),
    WHITESPACE(Regex("\\s+")),
    ;

    fun token(): TokenConstructor = { text, coordinates ->
        StructurizrToken(this, text, coordinates)
    }
}

typealias TokenConstructor = (String, Coordinates) -> StructurizrToken

private fun keywordRegex(pattern: String) = pattern.toRegex(option = RegexOption.IGNORE_CASE)

data class Coordinates(
    val lineStart: Int,
    val lineEnd: Int,
    val colStart: Int,
    val colEnd: Int
)

data class StructurizrToken(
    val tokenId: TokenId,
    val text: String,
    val coordinates: Coordinates,
)
