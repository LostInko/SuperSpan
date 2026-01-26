package com.example.superspan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superspan.R
import com.example.superspan.model.Address

class AddressAdapter(
    private val addressList: List<Address>,
    private val onAddressSelected: (Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Usiamo gli ID del TUO file XML
        val txtName: TextView = view.findViewById(R.id.txtNameAddress)
        val txtAddress: TextView = view.findViewById(R.id.txtAddress)
        val txtCap: TextView = view.findViewById(R.id.txtCap)
        val txtCity: TextView = view.findViewById(R.id.textCity)
        val imgMapPreview: ImageView = view.findViewById(R.id.imgMapPreview)
        val layoutAddress: View = view.findViewById(R.id.layout_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = addressList[position]
        val addressEncoded = "${item.Address}, ${item.City}".replace(" ", "+")
        val mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=$addressEncoded&zoom=15&size=200x200&key=AIzaSyBhUqEjaJ14r8GvdmfYVkKtpVhXpoY3dYI"

        // Usiamo Glide per caricare la mappa nell'ImageView
        Glide.with(holder.itemView.context)
            .load(mapUrl)
            .centerCrop()
            .into(holder.imgMapPreview)

        // Mappatura 1:1 con i campi della tua classe Address
        holder.txtName.text = item.Name
        holder.txtAddress.text = item.Address
        holder.txtCap.text = item.CAP
        holder.txtCity.text = item.City

        if (item.isSelected) {
            holder.layoutAddress.setBackgroundResource(R.drawable.bg_product_border)
        } else {
            holder.layoutAddress.setBackgroundResource(R.drawable.bg_product)
        }

        holder.itemView.setOnClickListener {
            onAddressSelected(item)
        }
    }

    override fun getItemCount() = addressList.size
}