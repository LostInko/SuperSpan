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
import com.example.superspan.model.Product
import com.example.superspan.viewmodel.HomeViewModel

class CartAdapter(
    private var cartList: MutableList<Product>,
    private val vm: HomeViewModel
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtOldPrice: TextView = view.findViewById(R.id.txtOldPrice) // AGGIUNTO
        val txtDesc: TextView = view.findViewById(R.id.txtDesc)
        val txtCount: TextView = view.findViewById(R.id.txtCount)
        val btnPlus: View = view.findViewById(R.id.btnPlus)
        val btnMinus: View = view.findViewById(R.id.btnMinus)
        val viewForeground: View = view.findViewById(R.id.viewForeground)
        val btnDelete: View = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    fun updateData(newList: List<Product>) {
        cartList.clear()
        cartList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartList[position]

        holder.txtTitle.text = product.name
        holder.txtDesc.text = product.description
        holder.txtCount.text = product.qty.toString()
        holder.viewForeground.translationX = 0f

        if (product.discountPrice != null) {
            //prodotto in offerta
            holder.txtPrice.text = product.discountPrice
            holder.txtPrice.setTextColor(Color.parseColor("#D60000")) // Rosso

            holder.txtOldPrice.visibility = View.VISIBLE
            holder.txtOldPrice.text = product.price

            holder.txtOldPrice.paintFlags = holder.txtOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            //prodotto normale
            holder.txtPrice.text = product.price
            holder.txtPrice.setTextColor(Color.BLACK)
            holder.txtOldPrice.visibility = View.GONE
        }

        if (product.imageRes != 0) {
            holder.imgProduct.setImageResource(product.imageRes)
        }


        holder.btnDelete.setOnClickListener {
            vm.updateProductQuantity(product, 0)
        }

        holder.btnMinus.visibility = View.VISIBLE
        holder.btnMinus.alpha = 1f
        holder.txtCount.visibility = View.VISIBLE
        holder.txtCount.alpha = 1f

        holder.btnPlus.setOnClickListener {
            product.qty++
            holder.txtCount.text = product.qty.toString()
            vm.notifyChange()
        }

        holder.btnMinus.setOnClickListener {
            if (product.qty > 0) {
                product.qty--
                holder.txtCount.text = product.qty.toString()
                vm.notifyChange()
            }
        }
    }

    override fun getItemCount(): Int = cartList.size
}