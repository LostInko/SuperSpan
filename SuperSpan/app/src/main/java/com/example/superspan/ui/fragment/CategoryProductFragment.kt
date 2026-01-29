package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.ProductAdapter
import com.example.superspan.viewmodel.HomeViewModel

class CategoryProductFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ProductAdapter

    companion object {
        private const val ARG_CATEGORY_LABEL = "category_label"

        fun newInstance(label: String) = CategoryProductFragment().apply {
            arguments = Bundle().apply { putString(ARG_CATEGORY_LABEL, label) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category_product, container, false)
        val categoryLabel = arguments?.getString(ARG_CATEGORY_LABEL) ?: ""

        // barra di ricerca
        val searchEditText = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.search_bar)

        // Impostiamo l'hint dinamico
        searchEditText.hint = "Cerca in $categoryLabel"

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        // Back button
        view.findViewById<AppCompatImageView>(R.id.btnBackTop).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val rv = view.findViewById<RecyclerView>(R.id.rvFilteredProducts)
        // Usiamo GridLayoutManager per mostrare i prodotti in griglia (2 colonne)
        rv.layoutManager = GridLayoutManager(requireContext(), 2)

        // Filtriamo i prodotti in base alla label della categoria
        val filteredList = viewModel.products.value?.filter {
            it.category.label == categoryLabel || categoryLabel == "Vedi tutto"
        } ?: emptyList()

        adapter = ProductAdapter(
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
            }
        )

        rv.adapter = adapter
        return view
    }
}