package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.superspan.R
import com.example.superspan.viewmodel.HomeViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.adapter.CartAdapter

class OrderFragment : Fragment() {

    private var tvCartAmountInActivity: TextView? = null
    private var tvTotalPrice: TextView? = null
    private lateinit var vm : HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_order, container, false)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val tvAddressTitle = view.findViewById<TextView>(R.id.tvAddressTitle)
        val tvAddressDetails = view.findViewById<TextView>(R.id.tvAddressDetails)
        val tvChangeAddress = view.findViewById<TextView>(R.id.tvChangeAddress)
        val recyclerViewProduct = view.findViewById<RecyclerView>(R.id.recyclerCart)

        tvTotalPrice = view.findViewById<TextView>(R.id.tv_total_price)

        vm.addresses.observe(viewLifecycleOwner) { allAddresses ->
            val defaultAddress = allAddresses.find { it.isSelected }

            if (defaultAddress != null) {
                tvAddressTitle.text = defaultAddress.Name
                tvAddressDetails.text = "${defaultAddress.Address}, ${defaultAddress.City}"
            }
        }

        tvChangeAddress.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddressListFragment())
                .addToBackStack(null)
                .commit()
        }

        recyclerViewProduct.layoutManager = LinearLayoutManager(requireContext())

        vm.products.observe(viewLifecycleOwner) { allProducts ->
            val itemsInCart = allProducts.filter { it.qty > 0 }.toMutableList()
            recyclerViewProduct.adapter = CartAdapter(itemsInCart, vm)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCartAmountInActivity = requireActivity().findViewById(R.id.tv_cart_amount)
        val tvNoAddress = view.findViewById<TextView>(R.id.tvNoAddress)
        val llAddress = view.findViewById<LinearLayout>(R.id.lladdresses)
        val btnPay = view.findViewById<Button>(R.id.btnPay)
        val rcProducts = view.findViewById<RecyclerView>(R.id.recyclerCart)
        val tvEmptyCart = view.findViewById<TextView>(R.id.tvEmptyCart)


        // Bottone Back
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        view.findViewById<Button>(R.id.btnPay)?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrderConfirmationFragment())
                .addToBackStack(null)
                .commit()
        }

        vm.cartTotal.observe(viewLifecycleOwner) { total ->
            val formatted = String.format("%.2f €", total)
            tvCartAmountInActivity?.text = formatted
            tvTotalPrice?.text = formatted
            if(total == 0.0) {
                btnPay.isEnabled = false
            }
        }

        vm.addresses.observe(viewLifecycleOwner) { allAddresses ->
            if(allAddresses.isNullOrEmpty()) {
                llAddress.visibility = View.GONE
                tvNoAddress.visibility = View.VISIBLE
                btnPay.isEnabled = false
                btnPay.alpha = 0.6f;
            } else {
                llAddress.visibility = View.VISIBLE
                tvNoAddress.visibility = View.GONE
            }
        }

        vm.products.observe(viewLifecycleOwner){ allProducts ->
            val itemsInCart = allProducts.filter { it.qty > 0 }
            if(itemsInCart.isEmpty()) {
                tvEmptyCart.visibility = View.VISIBLE
                rcProducts.visibility = View.GONE
            } else {
                tvEmptyCart.visibility = View.GONE
                rcProducts.visibility = View.VISIBLE
            }
        }
    }
}