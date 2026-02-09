package com.example.superspan.model

enum class TipoDomanda {Aperta, Chiusa, Numero}

class Question (
    val title : String,             // Domanda
    var answer : String,            // Risposta (che verrà salvata)
    val options : List<String>?,    // Opzioni (in caso di domanda a risposta multipla) - l'opzione selezionata verrà salvata in answer
    val tipo: TipoDomanda,          // Tipo domanda, aperta o chiusa
    var hasError: Boolean = false   // Indica se la domanda contiene errori (in pratica se non è stata ancora compilata)
)