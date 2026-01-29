package com.example.superspan.model

enum class TipoDomanda {Aperta, Chiusa}

class Question (
    val title : String,
    var answer : String,
    val options : List<String>?,
    val tipo: TipoDomanda,
    var hasError: Boolean = true
)