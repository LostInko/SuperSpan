package com.example.superspan.model

class JobOffer (
    val id : Int,               // Id univoco
    val name: String,           // Nome da mostrare

    // Altri dati non importanti per il funzionamento della logica
    val location: String,
    val shift: String,
    val wage: Double,
    val description: String,
    val requirements: String,
    val image: Int
)