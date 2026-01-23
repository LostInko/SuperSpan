package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        view.findViewById<TextView>(R.id.tvStoreTitle).text =
            user?.citta ?: "Nessuna città"

        view.findViewById<TextView>(R.id.tvUserName).text =
            "Benvenuto " + (user?.name ?: "Utente") + "!"

        // Osserva la lista prodotti dal ViewModel
        vm.products.observe(viewLifecycleOwner) { productList ->

            val homeProducts = productList.take(4).toMutableList()

            recyclerProducts.adapter = ProductAdapter(
                productList = homeProducts,
                onItemClick = { product ->
                    val fragment = ProductFragment.newInstance(
                        name = product.name,
                        desc = product.description,
                        price = product.price,
                        imageRes = product.imageRes
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
            private fun dp(view: View, v: Int) = (v * view.resources.displayMetrics.density).toInt()
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val space = dp(view, 8)
                outRect.set(space, space, space, space)
            }
        })

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
