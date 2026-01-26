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
import com.example.superspan.adapter.AddressAdapter // Assicurati di importarlo
import com.example.superspan.adapter.CartAdapter
import com.example.superspan.viewmodel.HomeViewModel

class CartFragment : Fragment() {

    private var tvCartAmountInActivity: TextView? = null
    private var tvTotalPrice: TextView? = null
    private lateinit var vm : HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)


        tvTotalPrice = view.findViewById<TextView>(R.id.tv_total_price)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]


        val recyclerViewProduct = view.findViewById<RecyclerView>(R.id.recyclerCart)
        recyclerViewProduct.layoutManager = LinearLayoutManager(requireContext())

        vm.products.observe(viewLifecycleOwner) { allProducts ->
            val itemsInCart = allProducts.filter { it.qty > 0 }
            recyclerViewProduct.adapter = CartAdapter(itemsInCart, vm)
        }


        val recyclerViewAddress = view.findViewById<RecyclerView>(R.id.recyclerAddress)
        recyclerViewAddress.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        vm.addresses.observe(viewLifecycleOwner){ allAddresses ->
            recyclerViewAddress.adapter = AddressAdapter(allAddresses) { selectedAddress ->
                vm.selectAddress(selectedAddress)
            }
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCartAmountInActivity = requireActivity().findViewById(R.id.tv_cart_amount)

        // Bottone back (in testa alla pagina)
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            // Oppure, se usi Navigation Component:
            // findNavController().navigateUp()
        }

        vm.cartTotal.observe(viewLifecycleOwner) { total ->
            val formatted = String.format("%.2f €", total)
            tvCartAmountInActivity?.text = formatted
            tvTotalPrice?.text = formatted
        }
    }

}