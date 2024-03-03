package com.guillaume.taffin.structuriz.ast

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import java.nio.file.Paths

val resources: File = Paths.get("src/test/resources").toFile()

class ParserTests {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "parser/empty_file.dsl",
            "parser/empty_anonymous_workspace.dsl",
            "parser/workspace_with_name.dsl",
        ]
    )
    fun `Valid workspace`(dslFile: String) {
        val dsl = resources.resolve(dslFile).readText()
        val referenceTree = resources.resolve(dslFile.replace("dsl", "json")).readText()

        val parser = StructurizrParser()

        val parsedTree = parser.parse(dsl)

        val actualTree = serializeJson(parsedTree.root)
        val actualDsl = serializeDsl(parsedTree.root)

        parsedTree.diagnostics shouldBe emptyList()
        actualTree shouldEqualJson referenceTree
        actualDsl shouldBe dsl
    }
}