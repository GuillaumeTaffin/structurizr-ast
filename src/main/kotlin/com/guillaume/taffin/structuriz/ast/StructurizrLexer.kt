package com.guillaume.taffin.structuriz.ast

import com.guillaume.taffin.structuriz.ast.Patterns.CLOSE_BRACE
import com.guillaume.taffin.structuriz.ast.Patterns.MODEL
import com.guillaume.taffin.structuriz.ast.Patterns.OPEN_BRACE
import com.guillaume.taffin.structuriz.ast.Patterns.WHITESPACE
import com.guillaume.taffin.structuriz.ast.Patterns.WORKSPACE
import kotlin.math.max

class StructurizrLexer(private val text: String) {

    private var charPointer: Int = 0
    private var linePointer: Int = 0
    private var columnPointer: Int = 0

    fun hasNext(): Boolean = charPointer < text.length

    fun next(): StructurizrToken {

        WORKSPACE.matchAt(text, charPointer)?.let {
            return makeSingleLineTokenAndMove(it.value, ::WorkspaceKeywordToken)
        }

        MODEL.matchAt(text, charPointer)?.let {
            return makeSingleLineTokenAndMove(it.value, ::ModelKeywordToken)
        }

        OPEN_BRACE.matchAt(text, charPointer)?.let {
            return makeSingleLineTokenAndMove(it.value, ::OpenBraceToken)
        }

        CLOSE_BRACE.matchAt(text, charPointer)?.let {
            return makeSingleLineTokenAndMove(it.value, ::CloseBraceToken)
        }

        WHITESPACE.matchAt(text, charPointer)?.let {
            return makeMultilineTokenAndMove(it.value, ::WhitespaceToken)
        }

        return makeSingleLineTokenAndMove("", ::EofToken)

    }

    private fun makeMultilineTokenAndMove(
        text: String,
        constructor: (String, Coordinates) -> StructurizrToken
    ): StructurizrToken {
        val lines = text.lines()
        val token = constructor(
            text,
            multilineCoordinates(lines, text)
        )

        linePointer += lines.size - 1
        charPointer += text.length
        return token
    }

    private fun multilineCoordinates(
        lines: List<String>,
        text: String
    ) = Coordinates(
        lineStart = linePointer,
        lineEnd = linePointer + lines.size - 1,
        colStart = columnPointer,
        colEnd = if (lines.size == 1) {
            columnPointer += text.length
            columnPointer - 1
        } else {
            columnPointer = lines.last().length
            max(0, columnPointer - 1)
        }
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

private

object Patterns {
    val WORKSPACE = keywordRegex("workspace")
    val MODEL = keywordRegex("model")
    val OPEN_BRACE = Regex("\\{")
    val CLOSE_BRACE = Regex("}")
    val WHITESPACE = Regex("\\s+")
}

private fun keywordRegex(pattern: String) = pattern.toRegex(option = RegexOption.IGNORE_CASE)

data class Coordinates(
    val lineStart: Int,
    val lineEnd: Int,
    val colStart: Int,
    val colEnd: Int
)

sealed class StructurizrToken(
    val tokenId: String,
    val text: String,
    val coordinates: Coordinates,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StructurizrToken

        if (tokenId != other.tokenId) return false
        if (text != other.text) return false
        if (coordinates != other.coordinates) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tokenId.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + coordinates.hashCode()
        return result
    }

    override fun toString(): String {
        return "StructurizrToken(tokenId='$tokenId', text='$text', coordinates=$coordinates)"
    }
}

class WorkspaceKeywordToken(text: String, coordinates: Coordinates) :
    StructurizrToken("WorkspaceKeywordToken", text, coordinates)

class ModelKeywordToken(text: String, coordinates: Coordinates) :
    StructurizrToken("ModelKeywordToken", text, coordinates)

class WhitespaceToken(text: String, coordinates: Coordinates) :
    StructurizrToken("WhitespaceToken", text, coordinates)

class OpenBraceToken(text: String, coordinates: Coordinates) :
    StructurizrToken("OpenBraceToken", text, coordinates)

class CloseBraceToken(text: String, coordinates: Coordinates) :
    StructurizrToken("CloseBraceToken", text, coordinates)

class EofToken(text: String, coordinates: Coordinates) :
    StructurizrToken("EofToken", text, coordinates)
