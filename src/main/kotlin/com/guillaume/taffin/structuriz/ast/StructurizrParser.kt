package com.guillaume.taffin.structuriz.ast


class StructurizrParser {

    private lateinit var lexer: StructurizrLexer
    private lateinit var diagnostics: MutableList<Diagnostic>

    fun parse(dsl: String): ParsedTree {
        lexer = StructurizrLexer(dsl)
        diagnostics = mutableListOf()
        return ValidTree(root = parseStructurizrDslFile())
    }

    /**
     * file = WHITESPACE* workspace WHITESPACE*
     */
    private tailrec fun parseStructurizrDslFile(children: List<AstNode> = listOf()): StructurizrDslFile {
        return when (val next = lexer.next()) {
            null -> StructurizrDslFile(children)
            else -> when (next.tokenId) {
                TokenId.WHITESPACE -> parseStructurizrDslFile(children + Whitespace(next))
                TokenId.WORKSPACE -> {
                    lexer.pushBack(next)
                    parseStructurizrDslFile(children + parseWorkspace())
                }

                else -> StructurizrDslFile(children)
            }
        }
    }

    /**
     * workspace = workspaceDefinition workspaceBlock
     */
    private tailrec fun parseWorkspace(children: List<AstNode> = listOf()): WorkspaceNode {
        return when (val next = lexer.next()) {
            null -> WorkspaceNode(children)
            else -> when (next.tokenId) {
                TokenId.WORKSPACE -> {
                    lexer.pushBack(next)
                    parseWorkspace(children + parseWorkspaceDefinition())
                }

                TokenId.OPEN_BRACE -> {
                    lexer.pushBack(next)
                    WorkspaceNode(children + parseWorkspaceBlock())
                }

                else -> throw Exception("Unexpected token in workspace : ${next.tokenId}")
            }
        }
    }

    /**
     * workspaceDefinition = WORKSPACE name? description? WHITESPACE*
     */
    private tailrec fun parseWorkspaceDefinition(
        children: List<AstNode> = listOf(),
        nameFound: Boolean = false,
    ): WorkspaceDefinition {
        return when (val next = lexer.next()) {
            null -> WorkspaceDefinition(children)
            else -> when (next.tokenId) {
                TokenId.WORKSPACE -> parseWorkspaceDefinition(children + WorkspaceKeyword(next), nameFound = nameFound)
                TokenId.STRING, TokenId.IDENTIFIER -> {
                    lexer.pushBack(next)
                    if (!nameFound) {
                        parseWorkspaceDefinition(children + parseName(), nameFound = true)
                    } else {
                        parseWorkspaceDefinition(children + parseDescription(), nameFound)
                    }
                }

                TokenId.WHITESPACE -> parseWorkspaceDefinition(children + Whitespace(next), nameFound)
                TokenId.OPEN_BRACE -> {
                    lexer.pushBack(next)
                    WorkspaceDefinition(children)
                }

                else -> throw Exception("Unexpected token in workspace definition : $next")
            }
        }
    }

    /**
     * workspaceBlock = { WHITESPACE* model? WHITESPACE* }
     */
    private tailrec fun parseWorkspaceBlock(children: List<AstNode> = listOf()): WorkspaceBlock {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in workspace block")
            else -> when (next.tokenId) {
                TokenId.WHITESPACE -> parseWorkspaceBlock(children + Whitespace(next))
                TokenId.OPEN_BRACE -> parseWorkspaceBlock(children + OpenBrace(next))
                TokenId.CLOSE_BRACE -> WorkspaceBlock(children + CloseBrace(next))
                TokenId.MODEL -> {
                    lexer.pushBack(next)
                    parseWorkspaceBlock(children + parseModel())
                }

                else -> throw Exception("Unexpected token in workspace block : ${next.tokenId}")
            }
        }
    }

    /**
     * model = modelDefinition modelSystems
     */
    private tailrec fun parseModel(children: List<AstNode> = listOf()): ModelNode {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in the model")
            else -> when (next.tokenId) {
                TokenId.MODEL -> {
                    lexer.pushBack(next)
                    parseModel(children + parseModelDefinition())
                }

                TokenId.OPEN_BRACE -> {
                    lexer.pushBack(next)
                    ModelNode(children + parseModelSystems())
                }

                else -> throw Exception("Unexpected token in model : ${next.tokenId}")
            }
        }
    }

    /**
     * modelDefinition = MODEL WHITESPACE*
     */
    private tailrec fun parseModelDefinition(children: List<AstNode> = listOf()): ModelDefinition {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in the model definition")
            else -> when (next.tokenId) {
                TokenId.MODEL -> parseModelDefinition(children + ModelKeyword(next))
                TokenId.WHITESPACE -> parseModelDefinition(children + Whitespace(next))
                TokenId.OPEN_BRACE -> {
                    lexer.pushBack(next)
                    ModelDefinition(children)
                }

                else -> throw Exception("Unexpected token in model definition : ${next.tokenId}")
            }
        }
    }

    /**
     * modelSystems = { WHITESPACE* personDeclaration }
     */
    private tailrec fun parseModelSystems(children: List<AstNode> = listOf()): ModelSystems {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in the model block")
            else -> when (next.tokenId) {
                TokenId.OPEN_BRACE -> parseModelSystems(children + OpenBrace(next))
                TokenId.WHITESPACE -> parseModelSystems(children + Whitespace(next))
                TokenId.CLOSE_BRACE -> ModelSystems(children + CloseBrace(next))
                TokenId.PERSON -> {
                    lexer.pushBack(next)
                    parseModelSystems(children + parsePersonDeclaration())
                }

                else -> throw Exception("Unexpected token in model block : ${next.tokenId}")
            }
        }
    }

    /**
     * personDeclaration = PERSON WHITESPACE* name
     */
    private tailrec fun parsePersonDeclaration(children: List<AstNode> = listOf()): PersonDeclaration {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in the person declaration")
            else -> when (next.tokenId) {
                TokenId.PERSON -> parsePersonDeclaration(children + PersonKeyword(next))
                TokenId.WHITESPACE -> parsePersonDeclaration(children + Whitespace(next))
                TokenId.IDENTIFIER -> {
                    lexer.pushBack(next)
                    PersonDeclaration(children + parseName())
                }

                else -> throw Exception("Unexpected token in person declaration : ${next.tokenId}")
            }
        }
    }

    /**
     * name = IDENTIFIER | STRING
     */
    private fun parseName(): Name {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in the name")
            else -> when (next.tokenId) {
                TokenId.IDENTIFIER -> Name(next)
                TokenId.STRING -> Name(next)
                else -> throw Exception("Unexpected token in name : ${next.tokenId}")
            }
        }
    }

    /**
     * description = IDENTIFIER | STRING
     */
    private fun parseDescription(): Description {
        return when (val next = lexer.next()) {
            null -> throw Exception("Missing tokens in the description")
            else -> when (next.tokenId) {
                TokenId.IDENTIFIER -> Description(next)
                TokenId.STRING -> Description(next)
                else -> throw Exception("Unexpected token in description : ${next.tokenId}")
            }
        }
    }

}
