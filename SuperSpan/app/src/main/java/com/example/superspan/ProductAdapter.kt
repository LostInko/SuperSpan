package com.example.superspan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val productList: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imgProduct: ImageView =
            itemView.findViewById(R.id.imgProduct)
        val txtTitle: TextView =
            itemView.findViewById(R.id.txtTitle)
        val txtDesc: TextView =
            itemView.findViewById(R.id.txtDesc)
        val txtPrice: TextView =
            itemView.findViewById(R.id.txtPrice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val product = productList[position]

        holder.txtTitle.text = product.name
        holder.txtDesc.text = product.description
        holder.txtPrice.text = product.price
        holder.imgProduct.setImageResource(product.imageRes)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}