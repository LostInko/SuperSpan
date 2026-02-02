package com.example.superspan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superspan.R
import com.example.superspan.model.JobOffer
import com.example.superspan.model.Product
import com.example.superspan.model.parsedPrice
import com.example.superspan.ui.fragment.ApplicationGlobal

class WorkWithUsViewModel : ViewModel() {
    val jobOffers = MutableLiveData<MutableList<JobOffer>>()

    init {
        jobOffers.value = mutableListOf(
            JobOffer(0, "Magazziniere", "Teramo", "Turno notte", 1200.50, "Lavora in magazzino", "Saper lavorare"),
            JobOffer(1, "Macellaio", "Genova", "Turno giorno", 900.00, "Lavora in macelleria", "Saper lavorare"),
            JobOffer(2, "Cassiere", "Cagliari", "Turno giorno", 950.00, "Lavora in cassa", "Saper lavorare"),
            JobOffer(3, "Manager", "Milano", "Turno notte", 1500.00, "Lavora (forse)", "Saper lavorare"),
        )
    }

    fun getAppliedOffersForUser(userId: String): List<JobOffer> {
        val listaReale = jobOffers.value ?: emptyList()

        val appliedOfferIds = ApplicationGlobal.application_list
            .filter { it.userId == userId }
            .map { it.offerId } // Assumendo che Application abbia offerId (o l'oggetto intero)

        return listaReale.filter { offer ->
            appliedOfferIds.contains(offer.id) // O confronta Int con Int
        }
    }

    fun refreshJobOffers() {
        jobOffers.value = jobOffers.value
    }

}
