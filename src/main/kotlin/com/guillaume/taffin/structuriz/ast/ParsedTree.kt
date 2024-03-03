package com.guillaume.taffin.structuriz.ast

sealed class ParsedTree(
    val root: AstNode,
    val diagnostics: List<Diagnostic>
)

class ValidTree(root: AstNode, diagnostics: List<Diagnostic> = listOf()) : ParsedTree(root, diagnostics)
class InvalidTree(root: AstNode, diagnostics: List<Diagnostic> = listOf()) : ParsedTree(root, diagnostics)

sealed class Diagnostic(val message: String)
class Error(message: String) : Diagnostic(message)
class Warning(message: String) : Diagnostic(message)
class Info(message: String) : Diagnostic(message)

