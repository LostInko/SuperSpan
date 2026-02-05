package com.example.superspan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.viewmodel.ActiveCoupon

/**
 * Adapter per la gestione della lista dei coupon attivati.
 * Visualizza i dati contenuti nel modello [ActiveCoupon] all'interno di un RecyclerView.
 */
class ActiveCouponsAdapter(
    private var items: List<ActiveCoupon>,
    private val onRemove: (ActiveCoupon) -> Unit
) : RecyclerView.Adapter<ActiveCouponsAdapter.VH>() {

    /**
     * ViewHolder che mantiene i riferimenti alle view per ogni singolo elemento della lista.
     */
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvCouponName)
        val qr: ImageView = v.findViewById(R.id.imgCouponQr)
        val btnRemove: View = v.findViewById(R.id.btnRemoveCoupon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // Inflate del layout specifico per l'elemento del coupon attivo
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_coupon, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        // Costruzione del testo: mostra Titolo e, se presente, il Dettaglio (es. prodotto scelto)
        holder.name.text = if (item.detail.isNullOrBlank()) {
            item.title
        } else {
            "${item.title} — ${item.detail}"
        }

        // Assegnazione della risorsa statica del QR Code
        holder.qr.setImageResource(R.drawable.qr_code)

        // Listener per il pulsante di rimozione: delega l'azione al chiamante tramite la lambda onRemove
        holder.btnRemove.setOnClickListener {
            onRemove(item)
        }
    }

    /**
     * Metodo per aggiornare il dataset dell'adapter e notificare il cambiamento alla UI.
     * @param newItems La nuova lista di coupon da visualizzare.
     */
    fun submit(newItems: List<ActiveCoupon>) {
        items = newItems
        notifyDataSetChanged()
    }
}