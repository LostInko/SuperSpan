
package com.example.superspan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R

class CouponListAdapter(
    private var items: List<String>, // nomi drawable, es. "coupon_online_1"
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CouponListAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgCoupon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coupon_ticket, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val name = items[position]
        val ctx = holder.itemView.context
        val resId = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)

        if (resId != 0) {
            holder.img.setImageResource(resId)
        } else {
            holder.img.setImageResource(R.drawable.coupon_placeholder)
        }

        holder.itemView.setOnClickListener { onClick(name) }
    }

    override fun getItemCount(): Int = items.size

    fun submit(list: List<String>) {
        items = list
        notifyDataSetChanged()
    }
}
