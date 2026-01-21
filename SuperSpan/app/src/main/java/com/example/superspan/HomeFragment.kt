package com.example.superspan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerProducts = view.findViewById<RecyclerView>(R.id.recyclerProducts)
        recyclerProducts.layoutManager = GridLayoutManager(context, 2)

        val products = listOf(
            Product("Succo ACE", "Brik 0.2L", "1,75€", R.drawable.succo_ace)
        )

        recyclerProducts.adapter = ProductAdapter(products)

        // Imposta il nome utente in un TextView prelevandolo da una lista globale
        // ATTENZIONE: se la lista è vuota, questo genererà un crash (IndexOutOfBounds).
        // Valuta un controllo di sicurezza se necessario.
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        tvUserName.text = ("Ciao, "+ GlobalData.currentUser?.name)

        return view
    }
}