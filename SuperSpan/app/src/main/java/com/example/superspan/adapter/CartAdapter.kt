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
    private var cartList: MutableList<Product>,
    private val vm: HomeViewModel // Passiamo il ViewModel per gestire i click
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var swipedPosition = -1

    fun setItemSwiped(position: Int) {
        val previousPosition = swipedPosition
        swipedPosition = position

        // Notifichiamo i cambiamenti per far scattare le animazioni
        if (previousPosition != -1) notifyItemChanged(previousPosition)
        notifyItemChanged(swipedPosition)
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtDesc: TextView = view.findViewById(R.id.txtDesc)
        val txtCount: TextView = view.findViewById(R.id.txtCount)
        val btnPlus: View = view.findViewById(R.id.btnPlus)
        val btnMinus: View = view.findViewById(R.id.btnMinus)
        val viewForeground: View = view.findViewById(R.id.viewForeground)
        val btnDelete: View = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false) // Assicurati che il file si chiami item_cart.xml
        return CartViewHolder(view)
    }

    //Aggiorna la lista senza ricreare l'adapter
    fun updateData(newList: List<Product>) {
        cartList.clear()
        cartList.addAll(newList)
        notifyDataSetChanged()
    }

    fun isItemOpen(position: Int): Boolean {
        return swipedPosition == position
    }

    fun closeSwipedItem() {
        val prev = swipedPosition
        swipedPosition = -1
        if (prev != -1) notifyItemChanged(prev)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartList[position]

        holder.txtTitle.text = product.name
        holder.txtPrice.text = product.price
        holder.txtDesc.text = product.description
        holder.txtCount.text = product.qty.toString()
        holder.viewForeground.translationX = 0f

        if (product.imageRes != 0) {
            holder.imgProduct.setImageResource(product.imageRes)
        }

        val buttonWidth = holder.btnDelete.width.toFloat()
        // CONTROLLO ANIMAZIONE COINCIDENTE
        if (position == swipedPosition) {
            // Se lo stato è "aperto", forziamo la traslazione sulla larghezza del bottone
            holder.viewForeground.animate()
                .translationX(-buttonWidth)
                .setDuration(200)
                .start()
        } else {
            // Se è chiuso, torna a zero
            holder.viewForeground.animate()
                .translationX(0f)
                .setDuration(200)
                .start()
        }

        holder.viewForeground.setOnClickListener {
            if (swipedPosition == position) {
                // Se l'utente clicca sul prodotto aperto (invece di swippare), lo richiudiamo
                closeSwipedItem()
            } else {
                // Qui potresti aprire la pagina di dettaglio del prodotto, se prevista
            }
        }

        holder.btnDelete.setOnClickListener {
            swipedPosition = -1
            vm.updateProductQuantity(product, 0)
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