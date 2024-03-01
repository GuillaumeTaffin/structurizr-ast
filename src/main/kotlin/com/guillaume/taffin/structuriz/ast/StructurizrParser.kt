package com.guillaume.taffin.structuriz.ast


class StructurizrParser {

    private lateinit var lexer: StructurizrLexer

    fun parse(dsl: String): AstNode? {
        lexer = StructurizrLexer(dsl)

        lexer.next()?.let { keyword ->
            if (keyword.tokenId == TokenIds.workspace) {
                lexer.next()?.let { whitespace ->

                }
                return WorkspaceNode(definition = WorkspaceDefinition(keyword = keyword))
            }
        }

        return null
    }

    /**
     * workspace =
     *  | workspace_keyword
     */
    fun parseWorkspace() {
        val keyword = parseWorkspaceKeyword()
    }

    /**
     * workspace_keyword = WORKSPACE
     */
    private fun parseWorkspaceKeyword(): AstLeaf {
        return when (val next = lexer.next()) {
            null -> throw Exception()
            else -> when (next.tokenId) {
                TokenIds.workspace -> AstLeaf(next)
                else -> throw Exception("Expecting workspace token but got ${next.text}")
            }
        }
    }
}
