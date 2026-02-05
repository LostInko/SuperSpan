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
import com.google.android.material.card.MaterialCardView
import com.example.superspan.model.*

/**
 * Adapter per la gestione dei prodotti preferiti.
 * Include la logica di confronto prezzi tra il momento del salvataggio e il prezzo attuale.
 */
class FavouriteAdapter(
    private var items: List<Product>,
    private val onRemoveFavorite: (Product) -> Unit,
    private val onOpenDetail: (Product) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cardRoot: MaterialCardView = v.findViewById(R.id.cardRoot)
        val imgProduct: ImageView = v.findViewById(R.id.imgProduct)
        val txtTitle: TextView = v.findViewById(R.id.txtTitle)
        val txtOldPrice: TextView = v.findViewById(R.id.txtOldPrice)
        val txtPrice: TextView = v.findViewById(R.id.txtPrice)
        val txtSavedPriceValue: TextView = v.findViewById(R.id.txtSavedPriceValue)
        val txtPriceDiff: TextView = v.findViewById(R.id.txtPriceDiff)
        val imgTrend: ImageView = v.findViewById(R.id.imgTrend)
        val btnFav: ImageView = v.findViewById(R.id.btnFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite, parent, false)
        return ProductViewHolder(v)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = items[position]

        // Setup dati base
        holder.txtTitle.text = product.name
        holder.imgProduct.setImageResource(product.imageRes)

        // 1. GESTIONE PREZZO ATTUALE
        // Se c'è un prezzo scontato, barra il vecchio e mostra il nuovo.
        // Risponde al bisogno di Michele di "vedere chiaramente il prezzo vecchio e il nuovo".
        if (product.discountPrice != null) {
            holder.txtOldPrice.visibility = View.VISIBLE
            holder.txtOldPrice.text = product.price
            // Applica l'effetto barrato tramite flag grafici del Paint
            holder.txtOldPrice.paintFlags = holder.txtOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.txtPrice.text = product.discountPrice
        } else {
            holder.txtOldPrice.visibility = View.GONE
            holder.txtPrice.text = product.price
        }

        // 2. LOGICA DI CONFRONTO STORICO
        val current = product.parsedPrice()
        val saved = product.savedNumericPrice()
        val diff = current - saved

        // Mostra il prezzo che il prodotto aveva quando è stato aggiunto ai preferiti
        holder.txtSavedPriceValue.text = product.priceWhenAddedToFav ?: (product.discountPrice ?: product.price)

        applyTrendLogic(holder, diff)
        applyHighlightLogic(holder, diff)

        // Listener per azioni utente
        holder.itemView.setOnClickListener { onOpenDetail(product) }
        holder.btnFav.setOnClickListener { onRemoveFavorite(product) }
    }

    /**
     * Gestisce l'estetica dell'indicatore di tendenza (frecce e colori).
     */
    private fun applyTrendLogic(holder: ProductViewHolder, diff: Double) {
        when {
            diff < 0 -> { // Prezzo diminuito (Risparmio!)
                holder.txtPriceDiff.text = String.format("%.2f €", diff).replace(".", ",")
                holder.txtPriceDiff.setTextColor(Color.parseColor("#2E7D32")) // Verde scuro
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
    }

    /**
     * Applica l'evidenziazione visiva se il prodotto è un "Super Affare" (sconto >= 50 cent).
     */
    private fun applyHighlightLogic(holder: ProductViewHolder, diff: Double) {
        if (diff <= -0.50) {
            holder.cardRoot.setCardBackgroundColor(Color.parseColor("#E8F5E9")) // Sfondo verde tenue
            holder.cardRoot.strokeColor = Color.parseColor("#2E7D32")
            holder.cardRoot.strokeWidth = 4
        } else {
            holder.cardRoot.setCardBackgroundColor(Color.WHITE)
            holder.cardRoot.strokeColor = Color.parseColor("#E0E0E0")
            holder.cardRoot.strokeWidth = 2
        }
    }

    override fun getItemCount(): Int = items.size

    /**
     * Aggiorna la lista dei preferiti.
     */
    fun updateItems(newList: List<Product>) {
        items = newList
        notifyDataSetChanged()
    }
}