package com.guillaume.taffin.structuriz.ast


class StructurizrParser {

    private lateinit var lexer: StructurizrLexer

    fun parse(dsl: String): AstNode {
        lexer = StructurizrLexer(dsl)
        return parseStructurizrDslFile()
    }

    /**
     * file =
     *  | WHITESPACE*
     *  | workspace
     *  | WHITESPACE*
     */
    private tailrec fun parseStructurizrDslFile(currentLevelChildren: List<AstNode> = listOf()): StructurizrDslFile {
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
     *  | workspaceBlock
     */
    private tailrec fun parseWorkspace(children: List<AstNode> = listOf()): WorkspaceNode {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in workspace node")
            else -> when (next.tokenId) {
                TokenIds.whitespace -> parseWorkspace(children + Whitespace(next))
                TokenIds.workspace -> {
                    lexer.pushBack(next)
                    parseWorkspace(children + parseWorkspaceDefinition())
                }

                TokenIds.openBrace -> {
                    lexer.pushBack(next)
                    WorkspaceNode(children + parseWorkspaceBlock())
                }

                else -> throw Exception("Unexpected token in workspace : ${next.tokenId}")
            }
        }
    }

    /**
     * workspaceDefinition =
     *  | WORKSPACE
     *  | WHITESPACE*
     */
    private tailrec fun parseWorkspaceDefinition(
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

    /**
     * workspaceBlock =
     *  | {
     *  | WHITESPACE*
     *  | }
     */
    private tailrec fun parseWorkspaceBlock(children: List<AstNode> = listOf()): WorkspaceBlock {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in workspace block")
            else -> when (next.tokenId) {
                TokenIds.whitespace -> parseWorkspaceBlock(children + Whitespace(next))
                TokenIds.openBrace -> parseWorkspaceBlock(children + OpenBrace(next))
                TokenIds.closeBrace -> WorkspaceBlock(children + CloseBrace(next))
                TokenIds.model -> {
                    lexer.pushBack(next)
                    parseWorkspaceBlock(children + parseModel())
                }

                else -> throw Exception("Unexpected token in workspace block : ${next.tokenId}")
            }
        }
    }

    /**
     * model =
     *  | modelDefinition
     *  | modelBlock
     */
    private tailrec fun parseModel(children: List<AstNode> = listOf()): ModelNode {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in the model")
            else -> when (next.tokenId) {
                TokenIds.model -> {
                    lexer.pushBack(next)
                    parseModel(children + parseModelDefinition())
                }

                TokenIds.openBrace -> {
                    lexer.pushBack(next)
                    ModelNode(children + parseModelBlock())
                }

                TokenIds.whitespace -> parseModel(children + Whitespace(next))
                else -> throw Exception("Unexpected token in model : ${next.tokenId}")
            }
        }
    }

    /**
     * modelDefinition =
     *  | MODEL
     *  | WHITESPACE*
     */
    private tailrec fun parseModelDefinition(children: List<AstNode> = listOf()): ModelDefinition {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in the model definition")
            else -> when (next.tokenId) {
                TokenIds.model -> parseModelDefinition(children + ModelKeyword(next))
                TokenIds.whitespace -> parseModelDefinition(children + Whitespace(next))
                TokenIds.openBrace -> {
                    lexer.pushBack(next)
                    ModelDefinition(children)
                }

                else -> throw Exception("Unexpected token in model definition : ${next.tokenId}")
            }
        }
    }

    /**
     * modelBlock =
     *  | {
     *  | WHITESPACE*
     *  | }
     */
    private tailrec fun parseModelBlock(children: List<AstNode> = listOf()): ModelBlock {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in the model block")
            else -> when (next.tokenId) {
                TokenIds.openBrace -> parseModelBlock(children + OpenBrace(next))
                TokenIds.whitespace -> parseModelBlock(children + Whitespace(next))
                TokenIds.closeBrace -> ModelBlock(children + CloseBrace(next))
                else -> throw Exception("Unexpected token in model block : ${next.tokenId}")
            }
        }
    }

}
