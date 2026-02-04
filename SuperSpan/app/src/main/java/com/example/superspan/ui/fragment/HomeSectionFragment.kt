package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.ProductAdapter
import com.example.superspan.viewmodel.HomeViewModel
import com.example.superspan.model.Product
import com.example.superspan.ui.activity.GlobalData

class HomeSectionFragment : Fragment() {

    private lateinit var vm: HomeViewModel
    private var tvCartAmountInActivity: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val recyclerProducts = view.findViewById<RecyclerView>(R.id.recyclerProducts)
        recyclerProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerProducts.setHasFixedSize(false)
        recyclerProducts.isNestedScrollingEnabled = false

        // Recupera l'utente corrente
        val user = GlobalData.currentUser

        view.findViewById<TextView>(R.id.tvStoreTitle).text ="Cagliari, Via Baccaredda 71"

        view.findViewById<TextView>(R.id.tvUserName).text =
            "Benvenuto " + (user?.name ?: "Utente") + "!"

        // Osserva la lista prodotti dal ViewModel

        // Osserva la lista prodotti dal ViewModel
        vm.products.observe(viewLifecycleOwner) { productList ->

            // FILTRO: Prendi solo i prodotti che hanno un prezzo scontato
            val discountedProducts = productList.filter { it.discountPrice != null }

            // Mostra i primi 4 prodotti scontati trovati
            val homeProducts = discountedProducts.take(4).toMutableList()

            recyclerProducts.adapter = ProductAdapter(
                productList = homeProducts,
                onItemClick = { product ->
                    val fullList = vm.products.value.orEmpty()
                    val index = fullList.indexOfFirst { it.name == product.name && it.imageRes == product.imageRes }

                    val fragment = ProductFragment.newInstance(
                        name = product.name,
                        desc = product.description,
                        price = product.price,
                        imageRes = product.imageRes,
                        index = index
                    )
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                },
                onCartChanged = { vm.updateCartTotal() }
            )
        }

        recyclerProducts.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val spanCount = 2
                val spacing = dp(12) // Spazio tra le card (uguale alla pagina prodotti)

                // Spazio orizzontale
                if (position % spanCount == 0) {
                    // Card di sinistra
                    outRect.left = dp(16) // Margine esterno sinistro
                    outRect.right = spacing / 2
                } else {
                    // Card di destra
                    outRect.left = spacing / 2
                    outRect.right = dp(16) // Margine esterno destro
                }

                // Spazio verticale
                outRect.bottom = spacing
                if (position < spanCount) {
                    outRect.top = 0
                }
            }
        })

        val workBanner = view.findViewById<ImageView>(R.id.bannerLavora)
        workBanner.setOnClickListener {
            val fragment = WorkWithUsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCartAmountInActivity = requireActivity().findViewById(R.id.tv_cart_amount)

        // aggiorna il totale quando cambia
        vm.cartTotal.observe(viewLifecycleOwner) { total ->
            val formatted = String.format("%.2f €", total)
            tvCartAmountInActivity?.text = formatted
        }
    }
}
