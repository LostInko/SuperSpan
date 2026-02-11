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
            JobOffer(0, "Magazziniere", "Milano", "Full-time (40h)", 1250.00, "La risorsa sarà inserita nel team logistico e si occuperà del ricevimento merci, controllo bolle, scarico camion e stoccaggio prodotti in magazzino. \nCollaborerà con il personale di vendita per il rifornimento dei reparti e parteciperà alle attività di inventario periodico. \nÈ richiesto l'utilizzo di transpallet elettrici e, preferibilmente, del muletto frontale.", "Patentino per il carrello elevatore in corso di validità; \nBuona costituzione fisica e disponibilità a lavori manuali; \nPrecisione e puntualità; \nDisponibilità a lavorare su turni, anche notturni se necessario; \nAutomunito.", R.drawable.addetto_magazzino),

            JobOffer(1, "Macellaio", "Roma", "Full-time (40h)", 1450.00, "Cerchiamo un professionista appassionato per il nostro reparto macelleria. La risorsa si occuperà del disosso, taglio e lavorazione delle carni, dell'allestimento del banco servito e take-away, garantendo la massima qualità e freschezza. \nÈ fondamentale il rispetto delle norme igienico-sanitarie HACCP e la capacità di consigliare i clienti sulla scelta dei tagli e sulle modalità di cottura.", "Esperienza pregressa di almeno 2 anni nel ruolo; \nOttima conoscenza delle tipologie di carne e tecniche di taglio; \nManualità e resistenza fisica; \nConoscenza delle normative HACCP; \nOrientamento al cliente e capacità di lavorare in team.", R.drawable.addetto_macelleria),

            JobOffer(2, "Cassiere", "Torina", "Part-time (24h)", 1050.00, "Il candidato sarà il punto di riferimento per l'esperienza d'acquisto del cliente. Le mansioni includono: \n- Gestione accurata delle operazioni di cassa e dei diversi metodi di pagamento; \n- Assistenza alla clientela per informazioni e risoluzione di piccoli reclami; \n- Mantenimento dell'ordine nella zona barriera casse. \nSi richiede disponibilità a turni spezzati e nei weekend.", "Diploma di scuola superiore; \nOttime doti relazionali e cortesia; \nPrecisione e affidabilità nella gestione del denaro; Flessibilità oraria; \nLa conoscenza base della lingua inglese è gradita.", R.drawable.addetto_cassa),

            JobOffer(3, "Store Manager", "Cagliari", "Full-time (40h)", 2300.00, "Siamo alla ricerca di un leader carismatico per la gestione completa del punto vendita. \nIl ruolo prevede la responsabilità del conto economico (P&L), la gestione, formazione e motivazione di un team di 15+ persone, e il monitoraggio dei KPI di vendita. \nIl Manager dovrà assicurare l'implementazione delle strategie commerciali aziendali e garantire standard elevati di servizio al cliente e immagine del negozio.", "Esperienza consolidata (minimo 3-5 anni) nella GDO o Retail in ruoli di responsabilità; \nSpiccate doti di leadership e gestione risorse umane; \nCapacità di analisi dati e problem solving; \nOrientamento al risultato e resistenza allo stress.", R.drawable.store_manager),

            JobOffer(4, "Addetto Gastronomia", "Verona", "Full-time (40h)", 1350.00, "Cerchiamo un appassionato di cucina per il reparto Gastronomia e Salumeria. \nLa risorsa si occuperà del taglio di salumi e formaggi, della gestione del banco caldo e della vendita assistita, consigliando i clienti sugli abbinamenti. \nÈ richiesta cura nell'esposizione e massima attenzione all'igiene e pulizia degli strumenti di taglio.", "Conoscenza dei prodotti tipici regionali e tecniche di taglio; \nEsperienza nell'uso dell'affettatrice e bilance; \nPredisposizione al contatto col pubblico; \nAttestato HACCP valido.", R.drawable.addetto_gastronomia),

            JobOffer(5, "Addetto S.Online", "Milano", "Part-time (30h)", 1100.00, "In un'ottica di potenziamento del servizio e-commerce, cerchiamo addetti alla preparazione della spesa online. \nLa risorsa riceverà gli ordini tramite terminale, preleverà i prodotti dagli scaffali (picking) prestando massima attenzione alla qualità dei freschi e alle scadenze, e gestirà il confezionamento per la consegna. \nRichiesta velocità e dimestichezza con strumenti digitali.", "Velocità e dinamismo; \nFamiliarità con l'utilizzo di tablet e scanner portatili; \nAttenzione al dettaglio (es. scelta della frutta migliore); \nDisponibilità immediata e flessibilità oraria nei weekend.", R.drawable.addetto_spesa_online),

            JobOffer(6, "Addetto Ortofrutta", "Napoli", "Part-time (24h)", 1000.00, "Il candidato gestirà il reparto più fresco del supermercato. Le mansioni prevedono: \n- Scarico e controllo qualità della merce in arrivo; \n- Allestimento scenografico dei banchi frutta e verdura; \n- Rotazione prodotti (FIFO) per garantire la freschezza e pesatura merce. \nIl ruolo richiede dinamismo e capacità di sollevare carichi.", "Passione per i prodotti freschi e occhio per la qualità; \nBuona resistenza fisica e velocità; \nDisponibilità a lavorare la mattina presto; \nCapacità di lavorare in autonomia.", R.drawable.addetto_ortofrutta),

            JobOffer(7, "Panettiere", "Firenze", "Full-time (40h)", 1400.00, "Per il nostro laboratorio interno cerchiamo un addetto al forno. La risorsa seguirà il processo di lievitazione e cottura di pane, pizze e focacce, oltre al confezionamento e all'esposizione dei prodotti da forno. \nDovrà garantire che il banco sia sempre rifornito di prodotti caldi e fragranti durante la giornata.", "Esperienza pregressa in panifici o laboratori GDO; \nConoscenza delle tecniche di lievitazione e cottura; \nDisponibilità a turni che iniziano nelle prime ore del mattino; \nSerietà e pulizia.", R.drawable.addetto_panetteria),

            JobOffer(8, "Addetto Pescheria", "Firenze", "Full-time (40h)", 1450.00, "Ricerchiamo un addetto specializzato per il banco pescheria. Il ruolo prevede l'allestimento del banco ghiaccio, la pulizia, sfilettatura e preparazione del pesce su richiesta del cliente. \nÈ fondamentale saper consigliare la clientela sulle modalità di pulizia e cottura del prodotto ittico.", "Conoscenza delle specie ittiche e stagionalità; \nOttima manualità nell'uso di coltelli e forbici per la pulizia del pesce; \nResistenza a lavorare in ambienti umidi/freddi; \nOrientamento al servizio.", R.drawable.addetto_pescheria),

            JobOffer(9, "Scaffalista Notturno", "Genova", "Notturno (30h)", 1300.00, "Cerchiamo addetti al rifornimento per la fascia oraria serale/notturna. \nA negozio chiuso, la squadra si occuperà di scaricare i pallet e riempire gli scaffali in modo massivo e ordinato, preparando il punto vendita per l'apertura del giorno successivo. \nIdeale per chi cerca un lavoro dinamico senza contatto diretto col pubblico.", "Disponibilità a lavorare esclusivamente su turni notturni o serali; \nBuona forza fisica e velocità di esecuzione; Autonomia e affidabilità; \nEssere automuniti è preferibile dato l'orario.", R.drawable.scaffalista_notturno),

            JobOffer(10, "Addetto Informazioni", "Milano", "Part-time (20h)", 950.00, "La risorsa sarà il punto di riferimento per l'accoglienza clienti. \nGestirà il banco informazioni occupandosi di: resi e cambi merce, emissione fatture, gestione tessere fedeltà, ascolto e risoluzione reclami, annunci al microfono. \nÈ richiesta un'ottima capacità di gestione dello stress e un sorriso sempre pronto.", "Eccellenti doti comunicative e pazienza; \nConoscenza base pacchetto Office; \nProblem solving immediato; \nBella presenza e cortesia; \nDiploma di scuola superiore.", R.drawable.addetto_box_informazioni),

            JobOffer(11, "Addetto Elettronica", "Siena", "Full-time (40h)", 1250.00, "Per il reparto multimediale del nostro ipermercato, cerchiamo un appassionato di tecnologia. \nLa risorsa assisterà i clienti nella scelta di smartphone, TV ed elettrodomestici, gestirà l'esposizione sicura della merce (antitaccheggio) e si occuperà delle pratiche di finanziamento e garanzia.", "Passione e competenza tecnica su elettronica di consumo; \nEsperienza di vendita assistita; \nBuona dialettica e capacità di negoziazione; \nFamiliarità con software gestionali di vendita.", R.drawable.addetto_elettronica),

            JobOffer(12, "Addetto Enoteca", "Padova", "Part-time (24h)", 1400.00, "Cerchiamo una figura esperta per curare la nostra cantina vini. \nIl candidato gestirà gli ordini e l'assortimento della cantina, organizzerà l'esposizione per regione/tipologia e guiderà i clienti nell'acquisto, suggerendo abbinamenti cibo-vino.", "Diploma da Sommelier (AIS/FISAR o equivalenti); \nProfonda conoscenza del settore enologico; \nStanding curato e ottime doti relazionali; \nCapacità di gestire inventario e ordini.", R.drawable.addetto_enoteca),

            JobOffer(13, "Allievo Capo Reparto", "Bologna", "Stage (40h)", 800.00, "Opportunità per giovani laureati che vogliono intraprendere una carriera direttiva nella GDO. \nIl percorso formativo prevede rotazione in tutti i reparti (cassa, freschi, logistica) per apprendere le dinamiche del punto vendita, affiancando lo Store Manager nella gestione dei numeri e del personale. \nScopo assunzione.", "Laurea (preferibilmente in Economia o Scienze Alimentari); \nForte ambizione e voglia di imparare; \nUmiltà e predisposizione al lavoro di squadra; \nDisponibilità full-time e mobilità territoriale.", R.drawable.allievo_capo_reparto),

            JobOffer(14, "Vice Direttore", "Cagliari", "Full-time (40h)", 1800.00, "Supporto operativo e strategico allo Store Manager. \nLa risorsa avrà la responsabilità di coordinare i capi reparto, gestire i turni del personale, supervisionare le operazioni di apertura/chiusura e intervenire nella risoluzione delle problematiche quotidiane. \nRappresenta la direzione in assenza dello Store Manager.", "Esperienza di almeno 2 anni in ruoli di coordinamento nella GDO; \nLeadership e capacità organizzative; \nFlessibilità oraria e disponibilità nei weekend; \nProblem solving orientato al cliente.", R.drawable.vice_manager)
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
