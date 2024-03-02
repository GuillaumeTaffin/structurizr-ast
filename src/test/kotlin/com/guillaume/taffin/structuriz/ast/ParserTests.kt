package com.guillaume.taffin.structuriz.ast

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Paths

class ParserTests {

    val resources = Paths.get("src/test/resources").toFile()

    @ParameterizedTest
    @ValueSource(
        strings = [
            "parser/empty_file.dsl",
            "parser/empty_anonymous_workspace.dsl",
        ]
    )
    fun `Simple workspace`(dslFile: String) {
        val dsl = resources.resolve(dslFile).readText()
        val referenceTree = resources.resolve(dslFile.replace("dsl", "json")).readText()

        val parser = StructurizrParser()

        val astNode = parser.parse(dsl)

        val actualTree = serializeJson(astNode)
        val actualDsl = serializeDsl(astNode)

        actualTree shouldEqualJson referenceTree
        actualDsl shouldBe dsl
    }
}