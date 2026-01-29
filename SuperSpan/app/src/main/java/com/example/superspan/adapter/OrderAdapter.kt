package com.example.superspan.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.Order

class OrderAdapter(private val onItemClick: (Order) -> Unit) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback(),) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
        private val tvOrderShop: TextView = itemView.findViewById(R.id.tvOrderShop)
        private val tvOrderAddress: TextView = itemView.findViewById(R.id.tvOrderAddress)
        private val tvOrderProductsSummary: TextView = itemView.findViewById(R.id.tvOrderProductsSummary)

        fun bind(order: Order) {
            tvOrderNumber.text = "Ordine #${order.orderNumber}"
            tvOrderShop.text = "Negozio: ${order.shop}"
            tvOrderAddress.text = "Consegnato a: ${order.address.Address}, ${order.address.City}"

            val totalItems = order.products.sumOf { it.qty }
            tvOrderProductsSummary.text = "$totalItems prodotti acquistati"
        }
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int, payloads: List<Any?>) {
        val order = getItem(position)
        holder.bind(order)

        holder.itemView.setOnClickListener { onItemClick(order) }
    }

    // DiffUtil serve per aggiornare solo gli elementi cambiati, migliorando le performance
    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderNumber == newItem.orderNumber
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}