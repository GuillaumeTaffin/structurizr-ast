package com.guillaume.taffin.structuriz.ast

import com.google.gson.Gson
import com.google.gson.GsonBuilder


fun serializeJson(astNode: AstNode): String {
    return gson.toJson(astNode)
}

fun serializeDsl(astNode: AstNode): String {
    return astNode.children.joinToString("") {
        when (it) {
            is AstLeaf -> it.token.text
            else -> serializeDsl(it)
        }
    }
}

val gson: Gson = GsonBuilder()
    .create()
