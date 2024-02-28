package com.guillaume.taffin.structuriz.ast

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LexerTests {

    @Test
    fun `No next for empty string`() {
        val lexer = StructurizrLexer("")

        lexer.hasNext() shouldBe false
        lexer.next() shouldBe StructurizrToken("EofToken", "", lineStart = 0, charStart = 0, lineEnd = 0, charEnd = 0)
    }

    @ParameterizedTest
    @ValueSource(strings = [" ", "\t", "  \t\t ", "\r", "\n", "\r\n \r\n", "\n\n\n"])
    fun `A single WHITESPACE token`(text: String) {
        val lexer = StructurizrLexer(text)

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            "WhitespaceToken",
            text,
            lineStart = 0,
            charStart = 0,
            lineEnd = 0,
            charEnd = text.length - 1
        )
    }

    @Test
    fun `Workspace keyword`() {
        val text = "workspace"
        val lexer = StructurizrLexer(text)

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            "WorkspaceKeywordToken",
            text,
            lineStart = 0,
            charStart = 0,
            lineEnd = 0,
            charEnd = text.length - 1
        )
    }

    @Test
    fun `Workspace keyword surrounded by whitespace`() {
        val text = "\t workspace  "
        val lexer = StructurizrLexer(text)

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            "WhitespaceToken",
            "\t ",
            lineStart = 0,
            charStart = 0,
            lineEnd = 0,
            charEnd = 1
        )

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            "WorkspaceKeywordToken",
            "workspace",
            lineStart = 0,
            charStart = 2,
            lineEnd = 0,
            charEnd = 10
        )

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            "WhitespaceToken",
            "  ",
            lineStart = 0,
            charStart = 11,
            lineEnd = 0,
            charEnd = 12
        )

        lexer.hasNext() shouldBe false
    }
}