package com.example.superspan.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.JobOfferAdapter
import com.example.superspan.model.JobOffer
import com.example.superspan.ui.bottomsheet.JobFilterBottomSheet
import com.example.superspan.viewmodel.WorkWithUsViewModel

class WorkWithUsFragment : Fragment() {

    private lateinit var vm: WorkWithUsViewModel
    private lateinit var recyclerJobOffers: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var tvFilterSort: TextView
    private var fullJobOfferList: List<JobOffer> = emptyList() // La lista originale dal database
    private var currentSearchQuery: String = ""
    private var currentLocationFilter: String = ""
    private var currentSortMode: Int = 0 // 0=Default, 1=Crescente, 2=Decrescente

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_with_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(requireActivity())[WorkWithUsViewModel::class.java]

        // Binding
        recyclerJobOffers = view.findViewById(R.id.recyclerJobOffers)
        etSearch = view.findViewById(R.id.search_bar)
        tvFilterSort = view.findViewById(R.id.tvFilterSort)

        //Back button
        val btnBack = view.findViewById<AppCompatImageView>(R.id.btnBackTop)

        recyclerJobOffers.layoutManager = GridLayoutManager(context, 1)

        vm.jobOffers.observe(viewLifecycleOwner) { jobOfferList ->
            // Salviamo la lista completa originale
            fullJobOfferList = jobOfferList ?: emptyList()
            // Applichiamo i filtri iniziali (mostra tutto)
            applyFilters()
        }

        // RICERCA TESTUALE
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString().trim()
                applyFilters()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // FILTRA E ORDINA (Apre il Pop-up)
        tvFilterSort.setOnClickListener {
            // Creiamo il BottomSheet passandogli lo stato attuale
            val bottomSheet = JobFilterBottomSheet(
                currentSortMode = currentSortMode,
                currentLocation = currentLocationFilter,
                onApply = { newSortMode, newLocation ->

                    // 1. Aggiorniamo le variabili di stato
                    currentSortMode = newSortMode
                    currentLocationFilter = newLocation

                    // 2. Aggiorniamo il testo (Feedback visivo)
                    if (currentLocationFilter.isNotEmpty() || currentSortMode != 0) {
                        tvFilterSort.text = "Filtri attivi (Modifica)"
                    } else {
                        tvFilterSort.text = "Filtra e Ordina"
                    }

                    // 3. Riapplichiamo la logica di filtro sulla lista
                    applyFilters()
                }
            )
            bottomSheet.show(parentFragmentManager, "JobFilterBottomSheet")
        }

        // BACK BUTTON
        btnBack?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /*
     * Funzione principale che prende la lista originale (fullJobOfferList) e applica in sequenza:
     * 1. Ricerca per nome (Search Bar)
     * 2. Filtro per Luogo (BottomSheet)
     * 3. Ordinamento per Stipendio (BottomSheet)
     */
    private fun applyFilters() {
        var resultList = fullJobOfferList

        // 1. Filtro Barra di Ricerca (Nome Offerta)
        if (currentSearchQuery.isNotEmpty()) {
            resultList = resultList.filter {
                it.name.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // 2. Filtro Luogo (Dal Popup)
        if (currentLocationFilter.isNotEmpty()) {
            resultList = resultList.filter {
                it.location.contains(currentLocationFilter, ignoreCase = true)
            }
        }

        // 3. Ordinamento Stipendio (Dal Popup)
        //BISOGNA METTERE WAGE COME INT O DOUBLE
        resultList = when (currentSortMode) {
            1 -> resultList.sortedBy { it.wage }        // Crescente
            2 -> resultList.sortedByDescending { it.wage } // Decrescente
            else -> resultList // Nessun ordinamento
        }

        // 4. Aggiorniamo l'Adapter
        recyclerJobOffers.adapter = JobOfferAdapter(
            jobOfferList = resultList,
            onItemClick = { jobOffer ->
                val fragment = JobOfferFragment.newInstance(
                    id = jobOffer.id,
                    name = jobOffer.name,
                    location = jobOffer.location,
                    shift = jobOffer.shift,
                    wage = jobOffer.wage,
                    desc = jobOffer.description,
                    req = jobOffer.requirements
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        )
    }
}