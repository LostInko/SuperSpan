package com.example.superspan.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Canvas

// Cambia il costruttore: passiamo sia LEFT che RIGHT come direzioni base
open class SwipeToDeleteCallback(private val adapter: CartAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    // 1. GESTIONE DINAMICA DELLE DIREZIONI
    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val position = viewHolder.adapterPosition
        return if (adapter.isItemOpen(position)) {
            // Se è già aperto, permettiamo solo lo swipe a DESTRA per richiuderlo
            ItemTouchHelper.RIGHT
        } else {
            // Se è chiuso, permettiamo solo lo swipe a SINISTRA per aprirlo
            ItemTouchHelper.LEFT
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        if (direction == ItemTouchHelper.LEFT) {
            // Se abbiamo swippato a sinistra, apriamo il bottone
            adapter.setItemSwiped(position)
        } else if (direction == ItemTouchHelper.RIGHT) {
            // Se abbiamo swippato a destra, richiudiamo l'item
            adapter.closeSwipedItem()
        }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val holder = viewHolder as CartAdapter.CartViewHolder
        val buttonWidth = holder.btnDelete.width.toFloat()
        val position = viewHolder.adapterPosition

        // 2. LOGICA DI TRASCINAMENTO DIFFERENZIATA
        val translationX = if (adapter.isItemOpen(position)) {
            // Se stiamo richiudendo da destra, partiamo da -buttonWidth e torniamo verso 0
            // dX in questo caso sarà positivo
            Math.min(0f, -buttonWidth + dX)
        } else {
            // Se stiamo aprendo, limitiamo come prima
            Math.max(-buttonWidth, dX)
        }

        getDefaultUIUtil().onDraw(
            c, recyclerView, holder.viewForeground,
            translationX, dY, actionState, isCurrentlyActive
        )
    }
}