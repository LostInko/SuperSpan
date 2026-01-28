package com.example.superspan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.Product

class OrderDetailProductsAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<OrderDetailProductsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgProductDetail)
        val name: TextView = view.findViewById(R.id.tvProductNameDetail)
        val qty: TextView = view.findViewById(R.id.tvProductQtyDetail)
        val price: TextView = view.findViewById(R.id.tvProductPriceDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.name.text = product.name
        holder.qty.text = "Quantità: ${product.qty}"
        holder.price.text = product.price
        holder.img.setImageResource(product.imageRes)
    }

    override fun getItemCount() = products.size
}