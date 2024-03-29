package com.guillaume.taffin.structuriz.ast

sealed class AstNode(val children: List<AstNode>) {
    val type = this::class.simpleName
}

sealed class AstLeaf(val token: StructurizrToken) : AstNode(listOf())

class StructurizrDslFile(children: List<AstNode> = listOf()) : AstNode(children)

class WorkspaceNode(children: List<AstNode>) : AstNode(children)

class ModelNode(children: List<AstNode>) : AstNode(children)

class WorkspaceDefinition(children: List<AstNode>) : AstNode(children)

class WorkspaceBlock(children: List<AstNode>) : AstNode(children)

class Whitespace(token: StructurizrToken) : AstLeaf(token) {
    fun containsNewLine(): Boolean {
        return token.text.contains(System.lineSeparator())
    }
}

class OpenBrace(token: StructurizrToken) : AstLeaf(token)

class CloseBrace(token: StructurizrToken) : AstLeaf(token)

class WorkspaceKeyword(token: StructurizrToken) : AstLeaf(token)

class ExtendsKeyword(token: StructurizrToken) : AstLeaf(token)

class ModelKeyword(token: StructurizrToken) : AstLeaf(token)

class ModelSystems(children: List<AstNode>) : AstNode(children)

class PersonStatement(children: List<AstNode>) : AstNode(children)

class PersonKeyword(token: StructurizrToken) : AstLeaf(token)

class Text(token: StructurizrToken) : AstLeaf(token)

class Name(token: StructurizrToken) : AstLeaf(token)

class Description(token: StructurizrToken) : AstLeaf(token)

class FileUrl(token: StructurizrToken) : AstLeaf(token)

class ModelStatements(children: List<AstNode>) : AstNode(children)

class WorkspaceNameAssignment(children: List<AstNode>) : AstNode(children)

class WorkspaceDescriptionAssignment(children: List<AstNode>) : AstNode(children)

class Identifier(token: StructurizrToken) : AstLeaf(token)

class AssignOperator(token: StructurizrToken) : AstLeaf(token)

class PropertiesKeyword(token: StructurizrToken) : AstLeaf(token)

class Properties(children: List<AstNode>) : AstNode(children)

class PropertiesAssignment(children: List<AstNode>) : AstNode(children)

class PropertyAssignment(children: List<AstNode>) : AstNode(children)