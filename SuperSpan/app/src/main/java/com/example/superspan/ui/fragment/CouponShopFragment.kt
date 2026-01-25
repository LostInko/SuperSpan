
package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.CouponListAdapter

class CouponShopFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: CouponListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_coupon_shop, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvCoupons)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = CouponListAdapter(
            items = listOf(
                // 🔁 Sostituisci con i TUOI file in res/drawable
                "coupon_shop_1",
                "coupon_shop_2",
                "coupon_shop_3"
            )
        ) { name ->
            Toast.makeText(requireContext(), "Selezionato: $name", Toast.LENGTH_SHORT).show()

            // Navigazione a dettaglio se serve:
            // parentFragmentManager.beginTransaction()
            //     .replace(R.id.fragment_container, CouponDetailFragment.newInstance(name))
            //     .addToBackStack(null)
            //     .commit()
        }

        rv.adapter = adapter

        // Info
        view.findViewById<View>(R.id.btnInfo)?.setOnClickListener {
            Toast.makeText(requireContext(), "Coupon in negozio: mostra il codice alla cassa.", Toast.LENGTH_LONG).show()
        }
    }
}
