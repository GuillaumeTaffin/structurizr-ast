package com.guillaume.taffin.structuriz.ast

sealed class AstNode(val children: Array<AstNode>?) {
    val type = this::class.simpleName
}

class AstLeaf(val token: StructurizrToken) : AstNode(null)

class WorkspaceNode(definition: WorkspaceDefinition) : AstNode(arrayOf(definition))

class WorkspaceDefinition(keyword: StructurizrToken) : AstNode(children = arrayOf(AstLeaf(keyword)))