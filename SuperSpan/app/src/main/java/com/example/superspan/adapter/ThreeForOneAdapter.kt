package com.example.superspan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.Product

class ThreeForOneAdapter(
    private val items: List<Product>,
    private val isSelected: (Product) -> Boolean,
    private val isPaid: (Product) -> Boolean,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ThreeForOneAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgProduct)
        val title: TextView = v.findViewById(R.id.txtTitle)
        val price: TextView = v.findViewById(R.id.txtPrice)
        val desc: TextView = v.findViewById(R.id.txtDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coupon_product, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]

        // Immagine con placeholder se serve
        val res = if (p.imageRes != 0) p.imageRes else R.drawable.ic_question
        holder.img.setImageResource(res)

        holder.title.text = p.name
        holder.price.text = p.price
        holder.desc.text = p.description

        val selected = isSelected(p)
        holder.itemView.setBackgroundResource(
            if (selected) R.drawable.bg_product_selected else R.drawable.bg_product
        )

        // Toggle selezione su click del prodotto
        holder.itemView.setOnClickListener { onClick(p) }
        holder.img.setOnClickListener { onClick(p) } // opzionale: anche solo l’immagine
    }

}