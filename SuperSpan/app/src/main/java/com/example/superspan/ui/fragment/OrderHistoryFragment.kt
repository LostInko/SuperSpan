package com.example.superspan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.ui.adapter.OrderAdapter
import com.example.superspan.viewmodel.HomeViewModel

class OrderHistoryFragment : Fragment() {

    private val vm: HomeViewModel by activityViewModels()
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        // Binding dei componenti
        val backButton = view.findViewById<ImageView>(R.id.btnBackTop)

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inizializziamo la RecyclerView
        val rv = view.findViewById<RecyclerView>(R.id.recyclerOrders)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyOrders)

        adapter = OrderAdapter()
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // 2. Osserviamo i cambiamenti della lista ordini nel ViewModel
        vm.orders.observe(viewLifecycleOwner) { orderList ->
            if (orderList.isNullOrEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rv.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rv.visibility = View.VISIBLE
                // Passiamo la lista (già ordinata o invertita per vedere l'ultimo ordine in alto)
                adapter.submitList(orderList.reversed())
            }
        }

    }

}