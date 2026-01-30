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

    private var currentTabName: String = "Generale" // Variabile per tracciare il tab attivo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_section_products, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCategories)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = CategoryAdapter(emptyList()) { categoriaSelezionata ->
            // Passiamo sia la categoria cliccata che il tab attivo
            val nextFrag = CategoryProductFragment.newInstance(categoriaSelezionata, currentTabName)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextFrag)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabName = tab?.text.toString() // Aggiorno il tab attivo
                updateOfferteTabStyle(tab, true)
                filtraCategorie(currentTabName)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                updateOfferteTabStyle(tab, false)
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Inizializza i colori e carica la categoria iniziale
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            updateOfferteTabStyle(tab, tab?.isSelected == true)
        }

        // Forza il primo caricamento
        filtraCategorie("Generale")

        return view
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

        // Aggiungiamo sempre il "Vedi tutto" in fondo
        listaFiltrata.add("Vedi tutto")
        adapter.updateData(listaFiltrata)
    }
}