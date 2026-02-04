package com.example.superspan.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.*
import com.google.android.material.card.MaterialCardView

class FavouriteAdapter(
    private var items: List<Product>,
    private val onRemoveFavorite: (Product) -> Unit,
    private val onOpenDetail: (Product) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val cardRoot: MaterialCardView = v.findViewById(R.id.cardRoot) // Riferimento alla Card
        val imgProduct: ImageView = v.findViewById(R.id.imgProduct)
        val txtTitle: TextView = v.findViewById(R.id.txtTitle)
        val txtOldPrice: TextView = v.findViewById(R.id.txtOldPrice)
        val txtPrice: TextView = v.findViewById(R.id.txtPrice)
        val txtSavedPriceValue: TextView = v.findViewById(R.id.txtSavedPriceValue)
        val txtPriceDiff: TextView = v.findViewById(R.id.txtPriceDiff)
        val imgTrend: ImageView = v.findViewById(R.id.imgTrend)
        val btnFav: ImageView = v.findViewById(R.id.btnFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.txtTitle.text = p.name
        holder.imgProduct.setImageResource(p.imageRes)

        // 1. GESTIONE SCONTO ATTUALE (PREZZO BARRATO)
        if (p.discountPrice != null) {
            holder.txtOldPrice.visibility = View.VISIBLE
            holder.txtOldPrice.text = p.price
            holder.txtOldPrice.paintFlags = holder.txtOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.txtPrice.text = p.discountPrice
        } else {
            holder.txtOldPrice.visibility = View.GONE
            holder.txtPrice.text = p.price
        }

        // 2. GESTIONE CONFRONTO STORICO
        val current = p.parsedPrice()
        val saved = p.savedNumericPrice()
        val diff = current - saved

        holder.txtSavedPriceValue.text = p.priceWhenAddedToFav ?: (p.discountPrice ?: p.price)

        when {
            diff < 0 -> { // Prezzo diminuito
                holder.txtPriceDiff.text = String.format("%.2f €", diff).replace(".", ",")
                holder.txtPriceDiff.setTextColor(Color.parseColor("#2E7D32"))
                holder.imgTrend.setImageResource(R.drawable.ic_arrow_downward)
                holder.imgTrend.setColorFilter(Color.parseColor("#2E7D32"))
            }
            diff > 0 -> { // Prezzo aumentato
                holder.txtPriceDiff.text = String.format("+%.2f €", diff).replace(".", ",")
                holder.txtPriceDiff.setTextColor(Color.RED)
                holder.imgTrend.setImageResource(R.drawable.ic_arrow_upward)
                holder.imgTrend.setColorFilter(Color.RED)
            }
            else -> { // Invariato
                holder.txtPriceDiff.text = "Invariato"
                holder.txtPriceDiff.setTextColor(Color.GRAY)
                holder.imgTrend.setImageResource(R.drawable.ic_remove)
                holder.imgTrend.setColorFilter(Color.GRAY)
            }
        }

        // --- MODIFICA: EVIDENZIAZIONE SUPER AFFARE (Sconto > 0.50€) ---
        if (diff <= -0.50) {
            // Sfondo verde tenue e bordo verde scuro
            holder.cardRoot.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
            holder.cardRoot.strokeColor = Color.parseColor("#2E7D32")
            holder.cardRoot.strokeWidth = 4 // Bordo più spesso per l'affare
        } else {
            // Sfondo standard e bordo neutro
            holder.cardRoot.setCardBackgroundColor(Color.WHITE)
            holder.cardRoot.strokeColor = Color.parseColor("#E0E0E0")
            holder.cardRoot.strokeWidth = 2
        }

        holder.itemView.setOnClickListener { onOpenDetail(p) }
        holder.btnFav.setOnClickListener { onRemoveFavorite(p) }
    }

    override fun getItemCount(): Int = items.size

    fun submit(list: List<Product>) {
        items = list
        notifyDataSetChanged()
    }
}