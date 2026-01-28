package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.OrderDetailProductsAdapter
import com.example.superspan.viewmodel.HomeViewModel

class OrderDetailFragment : Fragment(R.layout.fragment_order_details) {

    private val vm: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvProducts = view.findViewById<RecyclerView>(R.id.rvOrderDetailProducts)
        rvProducts.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        vm.selectedOrder.observe(viewLifecycleOwner) { order ->
            if (order != null) {
                view.findViewById<TextView>(R.id.tvDetailTitle).text = "Ordine #${order.orderNumber}"
                view.findViewById<TextView>(R.id.tvDetailShop).text = "Negozio: ${order.shop}"
                view.findViewById<TextView>(R.id.tvDetailAddress).text = "Indirizzo: ${order.address.Address}, ${order.address.City}"

                rvProducts.adapter = OrderDetailProductsAdapter(order.products)
            }
        }

        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}