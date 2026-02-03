package com.example.superspan.model

data class Application (
    val id: Int = generateId(),
    val name: String,
    val userId : String,    // Id dell'utente che ha effettuato la candidatura, per mostrare solo quelle a lui correlate
    val offerId: Int,       // Id dell'offerta di lavoro selezionata, per mostrare poi la descrizione
    val risposte: String,   // Stringa con le risposte date (separate da ###), poi scomposta per mostrarla nel riepilogo
    val files: String       // Stessa cosa delle risposte
) {
    companion object {
        private var nextOrderId = 1

        fun generateId(): Int {
            return nextOrderId++
        }
    }
}