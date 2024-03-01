package com.guillaume.taffin.structuriz.ast

import io.kotest.assertions.json.shouldEqualJson
import org.junit.jupiter.api.Test

class ParserTests {

    @Test
    fun `Simple workspace`() {
        val dsl = """
            workspace
            """.trim()

        val parser = StructurizrParser()

        val root = parser.parse(dsl)

        serialize(root) shouldEqualJson """
            {
              "children": [
                {
                  "children": [],
                  "type": "WorkspaceDefinition"
                }
              ],
              "type": "WorkspaceNode"
            }
            """
    }
}