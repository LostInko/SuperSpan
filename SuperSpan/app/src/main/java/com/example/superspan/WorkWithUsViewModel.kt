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
            JobOffer(0, "Magazziniere", "Teramo", "Full-time (40h)", 1250.00, "La risorsa sarà inserita nel team logistico e si occuperà del ricevimento merci, controllo bolle, scarico camion e stoccaggio prodotti in magazzino. Collaborerà con il personale di vendita per il rifornimento dei reparti e parteciperà alle attività di inventario periodico. È richiesto l'utilizzo di transpallet elettrici e, preferibilmente, del muletto frontale.", "Patentino per il carrello elevatore in corso di validità; Buona costituzione fisica e disponibilità a lavori manuali; Precisione e puntualità; Disponibilità a lavorare su turni, anche notturni se necessario; Automunito."),
            JobOffer(1, "Macellaio Specializzato", "Genova", "Full-time (40h)", 1450.00, "Cerchiamo un professionista appassionato per il nostro reparto macelleria. La risorsa si occuperà del disosso, taglio e lavorazione delle carni, dell'allestimento del banco servito e take-away, garantendo la massima qualità e freschezza. È fondamentale il rispetto delle norme igienico-sanitarie HACCP e la capacità di consigliare i clienti sulla scelta dei tagli e sulle modalità di cottura.", "Esperienza pregressa di almeno 2 anni nel ruolo; Ottima conoscenza delle tipologie di carne e tecniche di taglio; Manualità e resistenza fisica; Conoscenza delle normative HACCP; Orientamento al cliente e capacità di lavorare in team."),
            JobOffer(2, "Cassiere", "Cagliari", "Part-time (24h)", 1050.00, "Il candidato sarà il punto di riferimento per l'esperienza d'acquisto del cliente. Le mansioni includono: gestione accurata delle operazioni di cassa e dei diversi metodi di pagamento, assistenza alla clientela per informazioni e risoluzione di piccoli reclami, mantenimento dell'ordine nella zona barriera casse. Si richiede disponibilità a turni spezzati e nei weekend.", "Diploma di scuola superiore; Ottime doti relazionali e cortesia; Precisione e affidabilità nella gestione del denaro; Flessibilità oraria; La conoscenza base della lingua inglese è gradita."),
            JobOffer(3, "Manager", "Milano", "Full-time (40h)", 2300.00, "Siamo alla ricerca di un leader carismatico per la gestione completa del punto vendita. Il ruolo prevede la responsabilità del conto economico (P&L), la gestione, formazione e motivazione di un team di 15+ persone, e il monitoraggio dei KPI di vendita. Il Manager dovrà assicurare l'implementazione delle strategie commerciali aziendali e garantire standard elevati di servizio al cliente e immagine del negozio.", "Esperienza consolidata (minimo 3-5 anni) nella GDO o Retail in ruoli di responsabilità; Spiccate doti di leadership e gestione risorse umane; Capacità di analisi dati e problem solving; Orientamento al risultato e resistenza allo stress."),
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
