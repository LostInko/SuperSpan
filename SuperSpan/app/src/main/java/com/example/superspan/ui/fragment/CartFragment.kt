package com.example.superspan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.CartAdapter
import com.example.superspan.viewmodel.HomeViewModel

class CartFragment : Fragment() {

    private var tvCartAmountInActivity: TextView? = null
    private var tvTotalPrice: TextView? = null
    private lateinit var vm : HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_cart, container, false)

        tvTotalPrice = view.findViewById<TextView>(R.id.tv_total_price)

        vm  = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerCart)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // OSSERVA: ogni volta che premi + o - altrove, questo blocco di codice corre!
        vm.products.observe(viewLifecycleOwner) { allProducts ->
            // Prendi solo i prodotti che hanno almeno 1 quantità
            val itemsInCart = allProducts.filter { it.qty > 0 }

            // Passa la lista filtrata all'adapter (che dovrai creare)
            recyclerView.adapter = CartAdapter(itemsInCart, vm)
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
            tvTotalPrice?.text = formatted
        }
    }

}