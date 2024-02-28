package com.guillaume.taffin.structuriz.ast

class StructurizrLexer(private val text: String) {

    private var charPointer: Int = 0

    fun hasNext(): Boolean = charPointer < text.length

    fun next(): StructurizrToken {

        var matchResult = Regex("workspace").matchAt(text, charPointer)
        if (null != matchResult) {
            val token = StructurizrToken(
                tokenId = "WorkspaceKeywordToken",
                text = matchResult.value,
                lineStart = 0,
                charStart = charPointer,
            )
            charPointer += matchResult.value.length
            return token
        }

        matchResult = Regex("\\s+").matchAt(text, charPointer)
        if (null != matchResult) {
            val token = StructurizrToken(
                tokenId = "WhitespaceToken",
                text = matchResult.value,
                lineStart = 0,
                charStart = charPointer,
            )
            charPointer += matchResult.value.length
            return token
        }

        return StructurizrToken(
            tokenId = "EofToken",
            text = "",
            lineStart = 0,
            charStart = charPointer,
            charEnd = charPointer,
        )

    }
}

data class StructurizrToken(
    val tokenId: String,
    val text: String,
    val lineStart: Int,
    val lineEnd: Int = lineStart,
    val charStart: Int,
    val charEnd: Int = charStart + text.length - 1,
)
