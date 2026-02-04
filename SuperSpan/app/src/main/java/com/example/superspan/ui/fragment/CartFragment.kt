package com.example.superspan.ui.fragment

import android.R.attr.fragment
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.AddressAdapter // Assicurati di importarlo
import com.example.superspan.adapter.CartAdapter
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.viewmodel.HomeViewModel

class CartFragment : Fragment() {

    private var tvCartAmountInActivity: TextView? = null
    private var tvTotalPrice: TextView? = null
    private lateinit var vm : HomeViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)


        tvTotalPrice = view.findViewById(R.id.tv_total_price)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]


        val recyclerViewProduct = view.findViewById<RecyclerView>(R.id.recyclerCart)
        recyclerViewProduct.layoutManager = LinearLayoutManager(requireContext())

        cartAdapter = CartAdapter(mutableListOf(), vm)
        recyclerViewProduct.adapter = cartAdapter

        val tvEmptyCart = view.findViewById<TextView>(R.id.tvEmptyCart)
        val layoutMenu = view.findViewById<LinearLayout>(R.id.layoutMenu)
        val btnPay = view.findViewById<Button>(R.id.btnPay)

        vm.products.observe(viewLifecycleOwner) { allProducts ->
            val itemsInCart = allProducts.filter { it.qty > 0 }

            if (itemsInCart.isEmpty()) {
                tvEmptyCart.visibility = VISIBLE
                recyclerViewProduct.visibility = View.GONE
                layoutMenu.visibility = View.GONE
                btnPay.isEnabled = false
                btnPay.alpha = 0.6f;
            } else {
                tvEmptyCart.visibility = View.GONE
                cartAdapter.updateData(itemsInCart) // Usiamo il nuovo metodo
            }
        }

        val shopSpinner = view.findViewById<AppCompatSpinner>(R.id.spinnerStores)
        val shopList = listOf("Cagliari, Via Baccaredda 71", "Cagliari, Via Dante 134", "Selargius, Via Piave 62")
        val shopAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            shopList
        )

        shopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.findViewById<AppCompatSpinner>(R.id.spinnerStores).adapter = shopAdapter

        shopSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Salviamo il valore selezionato nella variabile di classe
                GlobalData.selectedShop = shopList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCartAmountInActivity = requireActivity().findViewById(R.id.tv_cart_amount)

        // Bottone back (in testa alla pagina)
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        view.findViewById<Button>(R.id.btnPay)?.setOnClickListener {
            val orderFragment = OrderFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, orderFragment)
                .addToBackStack(null) // Permette di tornare indietro al carrello premendo il tasto back
                .commit()
        }

        vm.cartTotal.observe(viewLifecycleOwner) { total ->
            val formatted = String.format("%.2f €", total)
            tvCartAmountInActivity?.text = formatted
            tvTotalPrice?.text = formatted
        }
    }
}