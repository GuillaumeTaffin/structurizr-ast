package com.guillaume.taffin.structuriz.ast

import com.google.gson.Gson
import com.google.gson.GsonBuilder


fun serialize(astNode: AstNode?): String {
    return gson.toJson(astNode)
}

val gson: Gson = GsonBuilder()
    .create()
