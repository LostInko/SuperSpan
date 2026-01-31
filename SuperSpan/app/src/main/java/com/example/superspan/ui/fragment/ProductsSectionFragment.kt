package com.example.superspan.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.CategoryAdapter
import com.example.superspan.adapter.ProductAdapter
import com.example.superspan.model.ProductCategory
import com.example.superspan.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText

class ProductsSectionFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var rvCategories: RecyclerView // Viene usata sia per Categorie che Prodotti
    private lateinit var tabLayout: TabLayout
    private lateinit var searchBar: TextInputEditText
    private lateinit var divider: View

    // Adapter per le categorie
    private lateinit var categoryAdapter: CategoryAdapter

    // Variabili stato Tab
    private val softRed = Color.parseColor("#4DFF0000") // Rosso sbiadito
    private val activeRed = Color.parseColor("#FF0000") // Rosso acceso
    private var currentTabName: String = "Generale"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_section_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        // Binding
        rvCategories = view.findViewById(R.id.rvCategories)
        tabLayout = view.findViewById(R.id.tabLayout)
        searchBar = view.findViewById(R.id.search_bar)
        divider = view.findViewById(R.id.divider)

        // Inizializzazione adapter delle categorie
        categoryAdapter = CategoryAdapter(emptyList()) { categoriaSelezionata ->
            val nextFrag = CategoryProductFragment.newInstance(categoriaSelezionata, currentTabName)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextFrag)
                .addToBackStack(null)
                .commit()
        }

        // Setup Tab Layout
        setupTabLayout()

        // Setup TextWatcher per la ricerca
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()

                if (query.isEmpty()) {
                    showCategoriesMode()
                } else {
                    showSearchMode(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Stato Iniziale: Mostra Categorie
        showCategoriesMode()
    }

    // --- MODALITÀ 1: Navigazione a Categorie (Default) ---
    private fun showCategoriesMode() {
        tabLayout.visibility = View.VISIBLE
        divider.visibility = View.VISIBLE

        // Le categorie stanno bene in lista verticale
        if (rvCategories.layoutManager !is LinearLayoutManager || rvCategories.adapter !is CategoryAdapter) {
            rvCategories.layoutManager = LinearLayoutManager(requireContext())
            rvCategories.adapter = categoryAdapter
        }

        // Ripristina le categorie del tab corrente
        filtraCategorie(currentTabName)
    }

    // --- MODALITÀ 2: Ricerca Globale Prodotti ---
    private fun showSearchMode(query: String) {
        tabLayout.visibility = View.GONE
        divider.visibility = View.GONE

        // I prodotti stanno meglio in una griglia (2 colonne)
        // Controllo se dobbiamo cambiare LayoutManager per evitare sfarfallii
        if (rvCategories.layoutManager !is GridLayoutManager) {
            rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)
        }

        val allProducts = viewModel.products.value ?: emptyList()

        // Filtro: cerchiamo nel nome o nella label della categoria
        val filteredList = allProducts.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
                    product.category.label.contains(query, ignoreCase = true)
        }

        // Creiamo l'adapter prodotti
        val productAdapter = ProductAdapter(
            productList = filteredList,
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
                // aggiorna il totale nel ViewModel quando clicchi + o -
                viewModel.updateCartTotal()
                viewModel.refreshProducts() // Forza l'aggiornamento UI
            }
        )

        rvCategories.adapter = productAdapter
    }

    // --- Gestione Tab (Grafica e Logica) ---
    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabName = tab?.text.toString()

                //Gestione colore rosso 'offerte'
                updateOfferteTabStyle(tab, true)

                // Se sto cercando e cambio tab, pulisco la ricerca per tornare alle categorie
                if (searchBar.text?.isNotEmpty() == true) {
                    searchBar.text?.clear()
                    searchBar.clearFocus() // Chiude tastiera opzionale
                } else {
                    filtraCategorie(currentTabName)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                updateOfferteTabStyle(tab, false)
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Inizializza stile tab iniziale
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            updateOfferteTabStyle(tab, tab?.isSelected == true)
        }
    }

    private fun updateOfferteTabStyle(tab: TabLayout.Tab?, isSelected: Boolean) {
        if (tab?.text == "Offerte") {
            tab.view.allViews.filterIsInstance<TextView>().forEach { textView ->
                textView.setTextColor(if (isSelected) activeRed else softRed)
            }
        }
    }

    private fun filtraCategorie(nomeTab: String) {
        val listaFiltrata = ProductCategory.getLabelsByTab(nomeTab).toMutableList()
        listaFiltrata.add("Vedi tutto")
        categoryAdapter.updateData(listaFiltrata)
    }
}