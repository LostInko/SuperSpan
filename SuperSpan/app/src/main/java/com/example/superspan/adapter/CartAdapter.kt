package com.example.superspan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.Product
import com.example.superspan.viewmodel.HomeViewModel

class CartAdapter(
    private val cartList: List<Product>,
    private val vm: HomeViewModel // Passiamo il ViewModel per gestire i click
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtDesc: TextView = view.findViewById(R.id.txtDesc)
        val txtCount: TextView = view.findViewById(R.id.txtCount)
        val btnPlus: View = view.findViewById(R.id.btnPlus)
        val btnMinus: View = view.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false) // Assicurati che il file si chiami item_cart.xml
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartList[position]

        // 1. Binding dei dati
        holder.txtTitle.text = product.name
        holder.txtPrice.text = product.price
        holder.txtDesc.text = product.description
        holder.txtCount.text = product.qty.toString()

        if (product.imageRes != 0) {
            holder.imgProduct.setImageResource(product.imageRes)
        }

        // 2. Gestione Visibilità (visto che nel tuo XML sono 'gone' di default)
        // Nel carrello, se il prodotto c'è, la quantità è almeno 1
        holder.btnMinus.visibility = View.VISIBLE
        holder.btnMinus.alpha = 1f
        holder.txtCount.visibility = View.VISIBLE
        holder.txtCount.alpha = 1f

        // 3. Logica dei pulsanti
        holder.btnPlus.setOnClickListener {
            product.qty++
            holder.txtCount.text = product.qty.toString()
            vm.notifyChange() // Aggiorna totale e liste ovunque
        }

        holder.btnMinus.setOnClickListener {
            if (product.qty > 0) {
                product.qty--
                holder.txtCount.text = product.qty.toString()
                vm.notifyChange()
                // Nota: se la qty diventa 0, il CartFragment (che osserva il VM)
                // filtrerà automaticamente il prodotto fuori dalla lista al prossimo update.
            }
        }
    }

    override fun getItemCount(): Int = cartList.size
}