package com.example.superspan.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.ProductAdapter
import com.example.superspan.model.Product
import com.example.superspan.viewmodel.HomeViewModel
import com.google.android.material.textfield.TextInputEditText

class CategoryProductFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ProductAdapter

    // Questa lista contiene TUTTI i prodotti di questa specifica categoria.
    private var baseProducts: List<Product> = emptyList()

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

        //Binding
        val searchEditText = view.findViewById<TextInputEditText>(R.id.search_bar)
        val rv = view.findViewById<RecyclerView>(R.id.rvFilteredProducts)

        searchEditText.hint = "Cerca in $categoryLabel..."

        // Back button
        view.findViewById<AppCompatImageView>(R.id.btnBackTop).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Setup RecyclerView
        rv.layoutManager = GridLayoutManager(requireContext(), 2)

        // 1. CALCOLO DELLA LISTA BASE (Solo prodotti di questa categoria)
        // Si calcola una volta sola e si salva in baseProducts
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

        // 2. Inizializzo l'adapter con la lista completa
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

        // 3. Ricerca locale
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                performLocalSearch(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun performLocalSearch(query: String) {
        // Se la query è vuota, ripristina la lista base completa
        if (query.isEmpty()) {
            adapter.updateList(baseProducts)
        } else {
            // Altrimenti filtra 'baseProducts'
            // Cerca solo tra i prodotti che sono GIÀ in questa categoria
            val filtered = baseProducts.filter {
                it.name.contains(query, ignoreCase = true)
            }
            adapter.updateList(filtered)
        }
    }
}