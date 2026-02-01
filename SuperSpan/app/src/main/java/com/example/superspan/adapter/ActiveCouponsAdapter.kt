package com.example.superspan.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.viewmodel.ActiveCoupon

class ActiveCouponsAdapter(
    private var items: List<ActiveCoupon>,
    private val onRemove: (ActiveCoupon) -> Unit
) : RecyclerView.Adapter<ActiveCouponsAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvCouponName)
        val qr: ImageView = v.findViewById(R.id.imgCouponQr)
        val btnRemove: View = v.findViewById(R.id.btnRemoveCoupon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_coupon, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = if (item.detail.isNullOrBlank()) item.title else "${item.title} — ${item.detail}"
        holder.qr.setImageResource(R.drawable.qr_code)
        holder.btnRemove.setOnClickListener { onRemove(item) }
    }

    fun submit(newItems: List<ActiveCoupon>) {
        items = newItems
        notifyDataSetChanged()
    }
}