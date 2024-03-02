package com.guillaume.taffin.structuriz.ast


class StructurizrParser {

    private lateinit var lexer: StructurizrLexer

    fun parse(dsl: String): AstNode {
        lexer = StructurizrLexer(dsl)
        return parseStructurizrDslFile()
    }

    /**
     * file =
     *  | WHITESPACE?
     *  | workspace
     *  | WHITESPACE?
     */
    private fun parseStructurizrDslFile(currentLevelChildren: List<AstNode> = listOf()): StructurizrDslFile {
        return when (val next = lexer.next()) {
            null -> StructurizrDslFile(currentLevelChildren)
            else -> when (next.tokenId) {
                TokenIds.whitespace -> parseStructurizrDslFile(currentLevelChildren + Whitespace(next))
                TokenIds.workspace -> {
                    lexer.pushBack(next)
                    parseStructurizrDslFile(currentLevelChildren + parseWorkspace())
                }

                else -> StructurizrDslFile(currentLevelChildren)
            }
        }
    }

    /**
     * workspace =
     *  | workspaceDefinition
     */
    private fun parseWorkspace(children: List<AstNode> = listOf()): WorkspaceNode {
        return when (val next = lexer.next()) {
            null -> WorkspaceNode(children)
            else -> when (next.tokenId) {
                TokenIds.whitespace -> parseWorkspace(children + Whitespace(next))
                TokenIds.workspace -> {
                    lexer.pushBack(next)
                    parseWorkspace(children + parseWorkspaceDefinition())
                }

                TokenIds.openBrace -> {
                    lexer.pushBack(next)
                    parseWorkspace(children + parseWorkspaceBlock())
                }

                else -> throw Exception("Unexpected token in workspace : ${next.tokenId}")
            }
        }
    }

    /**
     * workspaceDefinition =
     *  |
     */
    private fun parseWorkspaceDefinition(
        children: List<AstNode> = listOf()
    ): WorkspaceDefinition {
        return when (val next = lexer.next()) {
            null -> throw Exception()
            else -> when (next.tokenId) {
                TokenIds.workspace -> parseWorkspaceDefinition(children + WorkspaceKeyword(next))
                TokenIds.whitespace -> parseWorkspaceDefinition(children + Whitespace(next))
                TokenIds.openBrace -> {
                    lexer.pushBack(next)
                    WorkspaceDefinition(children)
                }

                else -> throw Exception("Unexpected token in workspace definition : $next")
            }
        }
    }

    private fun parseWorkspaceBlock(children: List<AstNode> = listOf()): WorkspaceBlock {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in workspace block")
            else -> when (next.tokenId) {
                TokenIds.whitespace -> parseWorkspaceBlock(children + Whitespace(next))
                TokenIds.openBrace -> parseWorkspaceBlock(children + OpenBrace(next))
                TokenIds.closeBrace -> WorkspaceBlock(children + CloseBrace(next))
                else -> throw Exception("Unexpected token in workspace block : ${next.tokenId}")
            }
        }
    }

}
