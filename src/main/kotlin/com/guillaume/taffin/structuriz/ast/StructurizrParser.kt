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
     * file = WHITESPACE* workspace? WHITESPACE*
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

                TokenId.WHITESPACE -> parseWorkspace(children + Whitespace(next))

                else -> throw Exception("Unexpected token in workspace : $next")
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
                TokenId.WORKSPACE -> parseWorkspaceDefinition(
                    children + WorkspaceKeyword(next),
                    nameFound = nameFound,
                )

                TokenId.EXTENDS -> {
                    if (nameFound) throw Exception("Extends keyword not allowed after defining the name of the workspace")
                    WorkspaceDefinition(children + ExtendsKeyword(next) + parseWhitespace() + parseFileUrl())
                }

                TokenId.STRING, TokenId.IDENTIFIER -> {
                    if (!nameFound) {
                        parseWorkspaceDefinition(children + Name(next), nameFound = true)
                    } else {
                        WorkspaceDefinition(children + Description(next))
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

    private fun parseFileUrl(): FileUrl {
        return when (val next = lexer.next()) {
            null -> throw Exception("Expecting <file|url> but found nothing")
            else -> when (next.tokenId) {
                TokenId.STRING, TokenId.IDENTIFIER -> FileUrl(next)
                else -> throw Exception("Expecting <file|url> but found $next")
            }
        }
    }

    private fun parseWhitespace(): Whitespace {
        return when (val next = lexer.next()) {
            null -> throw Exception("Expecting WHITESPACE but found nothing")
            else -> when (next.tokenId) {
                TokenId.WHITESPACE -> Whitespace(next)
                else -> throw Exception("Expecting WHITESPACE but found $next")
            }
        }
    }

    private fun parseNewLineWhitespace(): Whitespace {
        return parseWhitespace().let {
            if (it.containsNewLine()) it else throw Exception("Expecting new line character but got ${it.token}")
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
                TokenId.MODEL -> parseWorkspaceBlock(children + parseModel(ModelKeyword(next)))
                TokenId.NAME -> parseWorkspaceBlock(children + parseWorkspaceNameAssignation(Identifier(next)))
                TokenId.DESCRIPTION -> parseWorkspaceBlock(
                    children + parseWorkspaceDescriptionAssignation(
                        Identifier(
                            next
                        )
                    )
                )


                else -> throw Exception("Unexpected token in workspace block : ${next.tokenId}")
            }
        }
    }

    private fun parseWorkspaceNameAssignation(nameIdentifier: Identifier): WorkspaceNameAssignation {
        return WorkspaceNameAssignation(
            listOf(
                nameIdentifier,
                parseWhitespace(),
                parseAssignOperator(),
                parseWhitespace(),
                parseName(),
                parseNewLineWhitespace()
            )
        )
    }

    private fun parseWorkspaceDescriptionAssignation(descriptionId: Identifier): WorkspaceDescriptionAssignation {
        return WorkspaceDescriptionAssignation(
            listOf(
                descriptionId,
                parseWhitespace(),
                parseAssignOperator(),
                parseWhitespace(),
                parseDescription(),
                parseNewLineWhitespace()
            )
        )
    }

    private fun parseAssignOperator(): AssignOperator {
        return when (val next = lexer.next()) {
            null -> throw Exception("Expecting '=' but found nothing")
            else -> when (next.tokenId) {
                TokenId.ASSIGN_OPERATOR -> AssignOperator(next)
                else -> throw Exception("Expecting '=' but found $next")
            }
        }
    }

    /**
     * model = modelDefinition modelSystems
     */
    private fun parseModel(keyword: ModelKeyword): ModelNode {
        return ModelNode(listOf(keyword, parseWhitespace(), parseModelSystems()))
    }

    /**
     * modelSystems = { WHITESPACE* personDeclaration }
     */
    private fun parseModelSystems(): ModelSystems {
        return ModelSystems(listOf(parseOpenBrace(), parseModelStatements(), parseClosingBrace()))
    }

    private fun parseOpenBrace(): OpenBrace {
        return when (val next = lexer.next()) {
            null -> throw Exception("Expecting '{' but found nothing")
            else -> when (next.tokenId) {
                TokenId.OPEN_BRACE -> OpenBrace(next)
                else -> throw Exception("Expecting '{' but found $next")
            }
        }
    }

    private fun parseClosingBrace(): CloseBrace {
        return when (val next = lexer.next()) {
            null -> throw Exception("Expecting '}' but found nothing")
            else -> when (next.tokenId) {
                TokenId.CLOSE_BRACE -> CloseBrace(next)
                else -> throw Exception("Expecting '}' but found $next")
            }
        }
    }

    private fun parseModelStatements(children: List<AstNode> = listOf()): ModelStatements {
        return when (val next = lexer.next()) {
            null -> throw Exception("Expecting model statements or '}' but found nothing")
            else -> when (next.tokenId) {
                TokenId.WHITESPACE -> parseModelStatements(children + Whitespace(next))
                TokenId.PERSON -> parseModelStatements(children + parsePersonStatement(PersonKeyword(next)))
                TokenId.CLOSE_BRACE -> {
                    lexer.pushBack(next)
                    ModelStatements(children)
                }

                else -> throw Exception("Expecting '}' but found $next")
            }
        }
    }

    private fun parsePersonStatement(keyword: PersonKeyword): PersonStatement {
        return PersonStatement(listOf(keyword, parseWhitespace(), parseName()))
    }

    private fun parseName(): Name {
        return when (val next = lexer.next()) {
            null -> throw Exception("Expecting name but found nothing")
            else -> when (next.tokenId) {
                TokenId.IDENTIFIER, TokenId.STRING -> Name(next)
                else -> throw Exception("Expecting name but found $next")
            }
        }
    }

    private fun parseDescription(): Description {
        return when (val next = lexer.next()) {
            null -> throw Exception("Expecting a description but found nothing")
            else -> when (next.tokenId) {
                TokenId.IDENTIFIER, TokenId.STRING -> Description(next)
                else -> throw Exception("Expecting a description but found $next")
            }
        }
    }

}
