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
        lexer.next() shouldBe null
    }

    @ParameterizedTest
    @ValueSource(strings = [" ", "\t", "  \t\t ", "\r", "\n", "\r\n \r\n", "\n\n\n"])
    fun `A single WHITESPACE token`(text: String) {
        val lexer = StructurizrLexer(text)

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.WHITESPACE,
            text,
            Coordinates(
                lineStart = 0,
                lineEnd = text.lines().size - 1,
                colStart = 0,
                colEnd = if (text.lines().size > 1) 0 else text.length - 1
            )
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["workspace", "WORKSPACE", "WorkSpace"])
    fun `Workspace keyword`(text: String) {
        val lexer = StructurizrLexer(text)

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.WORKSPACE,
            text,
            Coordinates(
                lineStart = 0,
                colStart = 0,
                lineEnd = 0,
                colEnd = text.length - 1
            )
        )
    }

    @Test
    fun `Workspace keyword surrounded by whitespace`() {
        val text = "\t \n  workspace  "
        val lexer = StructurizrLexer(text)

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.WHITESPACE,
            text = "\t \n  ",
            Coordinates(
                lineStart = 0,
                lineEnd = 1,
                colStart = 0,
                colEnd = 1
            )
        )

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.WORKSPACE,
            text = "workspace",
            Coordinates(
                lineStart = 1,
                lineEnd = 1,
                colStart = 2,
                colEnd = 10
            )
        )

        lexer.hasNext() shouldBe true
        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.WHITESPACE,
            text = "  ",
            Coordinates(
                lineStart = 1,
                lineEnd = 1,
                colStart = 11,
                colEnd = 12
            )
        )

        lexer.hasNext() shouldBe false
    }

    @Test
    fun `Curly braces tokens`() {
        val text = "{}"
        val lexer = StructurizrLexer(text)

        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.OPEN_BRACE,
            text = "{",
            coordinates = Coordinates(
                lineStart = 0,
                lineEnd = 0,
                colStart = 0,
                colEnd = 0
            )
        )
        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.CLOSE_BRACE,
            text = "}",
            coordinates = Coordinates(
                lineStart = 0,
                lineEnd = 0,
                colStart = 1,
                colEnd = 1
            )
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["model", "MODEL", "mOdEl"])
    fun `Model keyword`(text: String) {
        val lexer = StructurizrLexer(text)

        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.MODEL,
            text = text,
            coordinates = Coordinates(
                lineStart = 0,
                lineEnd = 0,
                colStart = 0,
                colEnd = 4,
            )
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["michel", "_5o5o", "my-company"])
    fun `Identifier token`(text: String) {
        val lexer = StructurizrLexer(text)

        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.IDENTIFIER,
            text = text,
            coordinates = Coordinates(
                lineStart = 0,
                lineEnd = 0,
                colStart = 0,
                colEnd = text.length - 1,
            )
        )
    }

    @Test
    fun `Assign operator`() {
        val lexer = StructurizrLexer("=")

        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.ASSIGN_OPERATOR,
            text = "=",
            coordinates = Coordinates(
                lineStart = 0,
                lineEnd = 0,
                colStart = 0,
                colEnd = 0,
            )
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["person", "PERSON", "Person"])
    fun `Person keyword`(text: String) {
        val lexer = StructurizrLexer(text)

        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.PERSON,
            text = text,
            coordinates = Coordinates(
                lineStart = 0,
                lineEnd = 0,
                colStart = 0,
                colEnd = 5,
            )
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["\"person\"", "\"My company as a workspace !\""])
    fun `String token`(text: String) {
        val lexer = StructurizrLexer(text)

        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.STRING,
            text = text,
            coordinates = Coordinates(
                lineStart = 0,
                lineEnd = 0,
                colStart = 0,
                colEnd = text.length - 1,
            )
        )
    }

    @Test
    fun `extends token`() {
        val lexer = StructurizrLexer("extends")

        lexer.next() shouldBe StructurizrToken(
            tokenId = TokenId.EXTENDS,
            text = "extends",
            coordinates = Coordinates(
                lineStart = 0,
                lineEnd = 0,
                colStart = 0,
                colEnd = 6,
            )
        )
    }
}