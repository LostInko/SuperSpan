
package com.example.superspan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.Product

class FavouriteAdapter(
    private var items: List<Product>,
    private val onRemoveFavorite: (Product) -> Unit,
    private val onOpenDetail: (Product) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val imgProduct: AppCompatImageView = v.findViewById(R.id.imgProduct)
        val txtTitle: TextView = v.findViewById(R.id.txtTitle)
        val txtPrice: TextView = v.findViewById(R.id.txtPrice)
        val btnFav: AppCompatImageView = v.findViewById(R.id.btnFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.txtTitle.text = p.name
        holder.txtPrice.text = p.price
        holder.imgProduct.setImageResource(p.imageRes)

        // clic sulla card -> dettaglio
        holder.itemView.setOnClickListener { onOpenDetail(p) }

        // clic sul cuore pieno -> rimuovi dai preferiti
        holder.btnFav.setOnClickListener { onRemoveFavorite(p) }
    }

    override fun getItemCount(): Int = items.size

    fun submit(list: List<Product>) {
        items = list
        notifyDataSetChanged()
    }
}
