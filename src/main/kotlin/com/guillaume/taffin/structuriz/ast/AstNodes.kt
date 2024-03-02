package com.guillaume.taffin.structuriz.ast

sealed class AstNode(val children: List<AstNode>) {
    val type = this::class.simpleName
}

sealed class AstLeaf(val token: StructurizrToken) : AstNode(listOf())

class StructurizrDslFile(children: List<AstNode> = listOf()) : AstNode(children)

class WorkspaceNode(children: List<AstNode>) : AstNode(children)

class WorkspaceDefinition(children: List<AstNode>) : AstNode(children)

class WorkspaceBlock(children: List<AstNode>) : AstNode(children)

class Whitespace(token: StructurizrToken) : AstLeaf(token)

class OpenBrace(token: StructurizrToken) : AstLeaf(token)

class CloseBrace(token: StructurizrToken) : AstLeaf(token)

class WorkspaceKeyword(token: StructurizrToken) : AstLeaf(token)