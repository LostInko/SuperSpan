package com.example.superspan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superspan.R
import com.example.superspan.model.JobOffer
import com.example.superspan.model.Product
import com.example.superspan.model.parsedPrice

class WorkWithUsViewModel : ViewModel() {

    // Lista prodotti che NON si resetta cambiando fragment
    val jobOffers = MutableLiveData<MutableList<JobOffer>>()

    // Totale carrello aggiornato

    init {
        // inizializzi qui solo una volta
        jobOffers.value = mutableListOf(
            JobOffer(0, "Magazziniere", "Teramo", "Turno notte", "€€", "Lavora in magazzino", "Saper lavorare"),
            JobOffer(1, "Macellaio", "Genova", "Turno giorno", "€€", "Lavora in macelleria", "Saper lavorare"),
            JobOffer(2, "Cassiere", "Cagliari", "Turno giorno", "€€", "Lavora in cassa", "Saper lavorare"),
            JobOffer(3, "Manager", "Milano", "Turno notte", "€€", "Lavora (forse)", "Saper lavorare"),
        )
    }

    fun refreshJobOffers() {
        jobOffers.value = jobOffers.value
    }

}
