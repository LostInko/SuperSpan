package com.example.superspan.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.ProductAdapter
import com.example.superspan.model.Product
import com.example.superspan.viewmodel.HomeViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

class CategoryProductFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ProductAdapter

    // Questa lista contiene TUTTI i prodotti di questa specifica categoria (Base)
    private var baseProducts: List<Product> = emptyList()

    // 0 = Nessun ordinamento, 1 = Crescente, 2 = Decrescente
    private var currentSortMode = 0
    private var currentSearchQuery = ""

    private lateinit var cardAsc: MaterialCardView
    private lateinit var tvAsc: TextView
    private lateinit var cardDesc: MaterialCardView
    private lateinit var tvDesc: TextView

    companion object {
        private const val ARG_CATEGORY_LABEL = "category_label"
        private const val ARG_PARENT_SECTION = "parent_section"

        fun newInstance(label: String, parentSection: String) = CategoryProductFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY_LABEL, label)
                putString(ARG_PARENT_SECTION, parentSection)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category_product, container, false)

        // Recupero argomenti
        val categoryLabel = arguments?.getString(ARG_CATEGORY_LABEL) ?: ""
        val parentSection = arguments?.getString(ARG_PARENT_SECTION) ?: "Generale"

        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        // Binding
        val searchEditText = view.findViewById<TextInputEditText>(R.id.search_bar)
        val rv = view.findViewById<RecyclerView>(R.id.rvFilteredProducts)
        val btnBack = view.findViewById<AppCompatImageView>(R.id.btnBackTop)

        cardAsc = view.findViewById(R.id.cardSortAsc)
        tvAsc = view.findViewById(R.id.tvSortAsc)
        cardDesc = view.findViewById(R.id.cardSortDesc)
        tvDesc = view.findViewById(R.id.tvSortDesc)

        searchEditText.hint = "Cerca in $categoryLabel..."

        // Back button
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Setup RecyclerView
        rv.layoutManager = GridLayoutManager(requireContext(), 2)

        // 1. Calcolo lista base
        baseProducts = viewModel.products.value?.filter { product ->
            when (categoryLabel) {
                "Vedi tutto" -> {
                    parentSection == "Generale" || product.category.parentTab == parentSection
                }
                else -> {
                    product.category.label == categoryLabel
                }
            }
        } ?: emptyList()

        // 2. Inizializzo l'adapter
        adapter = ProductAdapter(
            productList = baseProducts,
            onItemClick = { product ->
                val detailFrag = ProductFragment.newInstance(
                    product.name, product.description, product.price, product.imageRes
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFrag)
                    .addToBackStack(null)
                    .commit()
            },
            onCartChanged = {
                viewModel.updateCartTotal()
            }
        )
        rv.adapter = adapter


        // Click su "Prezzo Crescente"
        cardAsc.setOnClickListener {
            if (currentSortMode == 1) {
                currentSortMode = 0 // Se già attivo, lo disattivo (toggle)
            } else {
                currentSortMode = 1
            }
            updateSortUI() // Aggiorna i colori
            applyFilters() // Applica l'ordinamento
        }

        // Click su "Prezzo Decrescente"
        cardDesc.setOnClickListener {
            if (currentSortMode == 2) {
                currentSortMode = 0
            } else {
                currentSortMode = 2
            }
            updateSortUI()
            applyFilters()
        }

        // Ricerca
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString().trim()
                applyFilters() // Richiama la funzione centrale che gestisce sia ricerca che ordine
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    //Aspetto visivo bottoni
    private fun updateSortUI() {
        val colorActive = ContextCompat.getColor(requireContext(), R.color.greenText)
        val colorInactive = 0xFFA0A0A0.toInt() // Grigio standard (#A0A0A0)

        // Reset: imposta entrambi come inattivi di default
        cardAsc.strokeColor = colorInactive
        cardAsc.strokeWidth = 0
        tvAsc.setTextColor(colorInactive)
        tvAsc.typeface = Typeface.DEFAULT

        cardDesc.strokeColor = colorInactive
        cardDesc.strokeWidth = 0
        tvDesc.setTextColor(colorInactive)
        tvDesc.typeface = Typeface.DEFAULT

        // Attiva quello selezionato
        when (currentSortMode) {
            1 -> { // Crescente
                cardAsc.strokeColor = colorActive
                cardAsc.strokeWidth = 4 // Spessore bordo attivo (circa 2dp)
                tvAsc.setTextColor(colorActive)
                tvAsc.typeface = Typeface.DEFAULT_BOLD
            }
            2 -> { // Decrescente
                cardDesc.strokeColor = colorActive
                cardDesc.strokeWidth = 4
                tvDesc.setTextColor(colorActive)
                tvDesc.typeface = Typeface.DEFAULT_BOLD
            }
        }
    }

    //Funzione che applica tutti i filtri
    private fun applyFilters() {

        var resultList = baseProducts

        // 1. Filtro per Testo (Ricerca)
        if (currentSearchQuery.isNotEmpty()) {
            resultList = resultList.filter {
                it.name.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // 2. Ordinamento
        resultList = when (currentSortMode) {
            1 -> resultList.sortedBy { it.price }        // Ascendente
            2 -> resultList.sortedByDescending { it.price } // Discendente
            else -> resultList // Nessun ordinamento (ordine originale)
        }

        // Aggiorniamo l'adapter con la lista finale elaborata
        adapter.updateList(resultList)
    }
}