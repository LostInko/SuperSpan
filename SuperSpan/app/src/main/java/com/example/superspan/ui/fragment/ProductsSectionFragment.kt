package com.example.superspan.ui.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.allViews
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.viewmodel.HomeViewModel
import com.example.superspan.model.ProductCategory
import com.example.superspan.adapter.CategoryAdapter
import com.google.android.material.tabs.TabLayout

class ProductsSectionFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: CategoryAdapter

    // Definiamo i colori per il tab Offerte
    private val softRed = Color.parseColor("#4DFF0000") // Rosso sbiadito
    private val activeRed = Color.parseColor("#FF0000") // Rosso acceso

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_section_products, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCategories)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = CategoryAdapter(emptyList()) { categoriaSelezionata ->
            println("Hai cliccato su: $categoriaSelezionata")
        }
        recyclerView.adapter = adapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        // Listener per i tab
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Gestione colore speciale per Offerte
                updateOfferteTabStyle(tab, true)
                filtraCategorie(tab?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Torna al rosso sbiadito se deselezionato
                updateOfferteTabStyle(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Inizializziamo il colore dei tab al caricamento
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            updateOfferteTabStyle(tab, tab?.isSelected == true)
        }

        filtraCategorie("Generale")

        return view
    }

    /**
     * Cerca il tab con testo "Offerte" e ne cambia il colore del testo
     */
    private fun updateOfferteTabStyle(tab: TabLayout.Tab?, isSelected: Boolean) {
        if (tab?.text == "Offerte") {
            tab.view.allViews.filterIsInstance<TextView>().forEach { textView ->
                textView.setTextColor(if (isSelected) activeRed else softRed)
            }
        }
    }

    private fun filtraCategorie(nomeTab: String) {
        val listaFiltrata = mutableListOf<String>()

        when (nomeTab) {
            "Generale" -> {
                // Mostra tutto l'Enum in ordine alfabetico o di inserimento
                listaFiltrata.addAll(ProductCategory.values().map { it.label })
            }
            "Alimentari" -> {
                listaFiltrata.add(ProductCategory.FRUTTA_VERDURA.label)
                listaFiltrata.add(ProductCategory.CARNE.label)
                listaFiltrata.add(ProductCategory.PESCE.label)
                listaFiltrata.add(ProductCategory.PASTA.label)
                listaFiltrata.add(ProductCategory.AFFETTATI.label)
                listaFiltrata.add(ProductCategory.LATTICINI.label)
                listaFiltrata.add(ProductCategory.BEVANDE_ALCOLICHE.label)
                listaFiltrata.add(ProductCategory.BEVANDE_ANALCOLICHE.label)
                listaFiltrata.add(ProductCategory.SNACK.label)
                listaFiltrata.add(ProductCategory.DOLCI.label)

            }
            "Casa" -> {
                listaFiltrata.add(ProductCategory.CURA_PERSONALE.label)
                listaFiltrata.add(ProductCategory.PULIZIE.label)
                listaFiltrata.add(ProductCategory.ANIMALI.label)
                listaFiltrata.add(ProductCategory.CURA_NEONATO.label)
            }
            "Offerte" -> {
                // Qui puoi mettere categorie speciali dedicate agli sconti

            }
        }

        listaFiltrata.add("Vedi tutto")
        adapter.updateData(listaFiltrata)
    }
}