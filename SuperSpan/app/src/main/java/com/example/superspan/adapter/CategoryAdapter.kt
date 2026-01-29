package com.example.superspan.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R

class CategoryAdapter(
    private var categories: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryName = categories[position]
        holder.tvName.text = categoryName

        if (categoryName.contains("Offerte", true)) {
            holder.tvName.setTextColor(android.graphics.Color.RED)
        } else {
            holder.tvName.setTextColor(android.graphics.Color.BLACK)
        }

        holder.itemView.setOnClickListener { onItemClick(categoryName) }
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newList: List<String>) {
        this.categories = newList
        notifyDataSetChanged()
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
    }
}