package com.example.superspan.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.JobOfferAdapter
import com.example.superspan.model.JobOffer
import com.example.superspan.ui.bottomsheet.JobFilterBottomSheet
import com.example.superspan.viewmodel.WorkWithUsViewModel
import com.google.android.material.card.MaterialCardView

class WorkWithUsFragment : Fragment() {

    private lateinit var vm: WorkWithUsViewModel
    private lateinit var recyclerJobOffers: RecyclerView
    private lateinit var etSearch: EditText

    // Riferimenti per la Card Filtro
    private lateinit var cardFilterSort: MaterialCardView
    private lateinit var tvFilterSort: TextView

    private var fullJobOfferList: List<JobOffer> = emptyList()
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

        // Binding Componenti
        recyclerJobOffers = view.findViewById(R.id.recyclerJobOffers)
        etSearch = view.findViewById(R.id.search_bar)
        cardFilterSort = view.findViewById(R.id.cardFilterSort)
        tvFilterSort = view.findViewById(R.id.tvFilterSort)
        val btnBack = view.findViewById<AppCompatImageView>(R.id.btnBackTop)

        recyclerJobOffers.layoutManager = GridLayoutManager(context, 1)

        // Osservazione Dati
        vm.jobOffers.observe(viewLifecycleOwner) { jobOfferList ->
            fullJobOfferList = jobOfferList ?: emptyList()
            applyFilters()
        }

        // Ricerca Testuale
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString().trim()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        //  Gestione Click Card Filtro
        cardFilterSort.setOnClickListener {
            val bottomSheet = JobFilterBottomSheet(
                currentSortMode = currentSortMode,
                currentLocation = currentLocationFilter,
                onApply = { newSortMode, newLocation ->
                    // Aggiorna stato
                    currentSortMode = newSortMode
                    currentLocationFilter = newLocation

                    // Feedback visivo e logica
                    updateFilterUI()
                    applyFilters()
                }
            )
            bottomSheet.show(parentFragmentManager, "JobFilterBottomSheet")
        }

        // Back Button
        btnBack?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    //Aspetto estetico Card
    private fun updateFilterUI() {
        val hasFilters = currentLocationFilter.isNotEmpty() || currentSortMode != 0

        if (hasFilters) {
            // Stato Attivo
            val activeColor = ContextCompat.getColor(requireContext(), R.color.brand_primary)
            tvFilterSort.text = "Filtri attivi (Modifica)"
            tvFilterSort.setTextColor(activeColor)

            // Cambia colore all'icona a sinistra
            tvFilterSort.compoundDrawableTintList = ColorStateList.valueOf(activeColor)
            // Cambia colore al bordo della Card
            cardFilterSort.setStrokeColor(ColorStateList.valueOf(activeColor))
            cardFilterSort.strokeWidth = 4 // ispessisce  il bordo

        } else {
            // Stato Default: Grigio standard
            val defaultGray = Color.parseColor("#A0A0A0")
            val defaultBlack = Color.parseColor("#000000")


            tvFilterSort.text = "Filtra e Ordina le offerte"
            tvFilterSort.setTextColor(defaultBlack)
            tvFilterSort.compoundDrawableTintList = ColorStateList.valueOf(defaultBlack)

            cardFilterSort.setStrokeColor(ColorStateList.valueOf(defaultGray))
            // Convertiamo 1dp in pixel per essere precisi
            val density = resources.displayMetrics.density
            cardFilterSort.strokeWidth = (1 * density).toInt()
        }
    }


    // Logica filtro e ordinamento
    private fun applyFilters() {
        var resultList = fullJobOfferList

        // 1. Filtro Ricerca
        if (currentSearchQuery.isNotEmpty()) {
            resultList = resultList.filter {
                it.name.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // 2. Filtro Luogo
        if (currentLocationFilter.isNotEmpty()) {
            resultList = resultList.filter {
                it.location.contains(currentLocationFilter, ignoreCase = true)
            }
        }

        // 3. Ordinamento
        resultList = when (currentSortMode) {
            1 -> resultList.sortedBy { it.wage }
            2 -> resultList.sortedByDescending { it.wage }
            else -> resultList
        }

        // 4. Update Adapter
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