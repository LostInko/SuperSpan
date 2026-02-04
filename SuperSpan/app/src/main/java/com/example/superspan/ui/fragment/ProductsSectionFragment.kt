package com.example.superspan.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    private lateinit var rvCategories: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var searchBar: TextInputEditText
    private lateinit var divider: View

    private lateinit var categoryAdapter: CategoryAdapter

    private val softRed = Color.parseColor("#4DFF0000")
    private val activeRed = Color.parseColor("#FF0000")
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

        rvCategories = view.findViewById(R.id.rvCategories)
        tabLayout = view.findViewById(R.id.tabLayout)
        searchBar = view.findViewById(R.id.search_bar)
        divider = view.findViewById(R.id.divider)

        categoryAdapter = CategoryAdapter(emptyList()) { categoriaSelezionata ->
            val nextFrag = CategoryProductFragment.newInstance(categoriaSelezionata, currentTabName)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextFrag)
                .addToBackStack(null)
                .commit()
        }

        setupTabLayout()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    // Se svuoto la ricerca, torno alla modalità corretta in base al tab
                    if (currentTabName == "Offerte") showOfferteMode() else showCategoriesMode()
                } else {
                    showSearchMode(query)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        showCategoriesMode()
    }

    private fun showCategoriesMode() {
        tabLayout.visibility = View.VISIBLE
        divider.visibility = View.VISIBLE

        // Reset a lista verticale per categorie
        rvCategories.layoutManager = LinearLayoutManager(requireContext())
        rvCategories.adapter = categoryAdapter

        filtraCategorie(currentTabName)
    }

    // --- NUOVA MODALITÀ: Solo Prodotti in Sconto ---
    private fun showOfferteMode() {
        tabLayout.visibility = View.VISIBLE
        divider.visibility = View.VISIBLE

        // Griglia a 2 colonne per i prodotti
        rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)

        val discountedProducts = viewModel.products.value?.filter { it.discountPrice != null } ?: emptyList()

        rvCategories.adapter = ProductAdapter(
            productList = discountedProducts,
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
                viewModel.refreshProducts()
            }
        )
    }

    private fun showSearchMode(query: String) {
        tabLayout.visibility = View.GONE
        divider.visibility = View.GONE

        if (rvCategories.layoutManager !is GridLayoutManager) {
            rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)
        }

        val allProducts = viewModel.products.value ?: emptyList()
        val filteredList = allProducts.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
                    product.category.label.contains(query, ignoreCase = true)
        }

        rvCategories.adapter = ProductAdapter(
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
                viewModel.updateCartTotal()
                viewModel.refreshProducts()
            }
        )
    }

    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabName = tab?.text.toString()
                updateOfferteTabStyle(tab, true)

                if (searchBar.text?.isNotEmpty() == true) {
                    searchBar.text?.clear()
                    searchBar.clearFocus()
                }

                // LOGICA TAB OFFERTE
                if (currentTabName == "Offerte") {
                    showOfferteMode()
                } else {
                    showCategoriesMode()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                updateOfferteTabStyle(tab, false)
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

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
        val categorieOriginali = ProductCategory.getLabelsByTab(nomeTab)
        val listaOrdinata = mutableListOf<String>()
        listaOrdinata.add("Vedi tutto")
        listaOrdinata.addAll(categorieOriginali)
        categoryAdapter.updateData(listaOrdinata)
    }
}