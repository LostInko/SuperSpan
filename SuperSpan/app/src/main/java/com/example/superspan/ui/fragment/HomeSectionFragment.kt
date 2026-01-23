
package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.ProductAdapter
import com.example.superspan.model.Product
import com.example.superspan.ui.activity.GlobalData

class HomeSectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerProducts = view.findViewById<RecyclerView>(R.id.recyclerProducts)
        recyclerProducts.layoutManager = GridLayoutManager(context, 2)

        // Recupera l'utente corrente
        val user = GlobalData.currentUser


        val tvStoreTitle = view.findViewById<TextView>(R.id.tvStoreTitle)
        tvStoreTitle.text = user?.citta ?: "Nessuna città"

        // Lista prodotti (placeholder)
        val products = listOf(
            Product("Succo ACE", "Brik 0.2L", "1,75€", R.drawable.succo_ace)
        )

        recyclerProducts.adapter = ProductAdapter(products) { product ->
            val fragment = ProductFragment.newInstance(
                name = product.name,
                desc = product.description,
                price = product.price,
                imageRes = product.imageRes
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }


        // Messaggio di benvenuto
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        tvUserName.text = "Benvenuto " + (user?.name ?: "Utente") + "!"

        val workBanner = view.findViewById<ImageView>(R.id.bannerLavora)
        workBanner.setOnClickListener {
            val fragment = WorkWithUsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
