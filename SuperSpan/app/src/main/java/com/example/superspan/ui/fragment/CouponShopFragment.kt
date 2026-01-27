
package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.superspan.R

class CouponShopFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_coupon_online, container, false)
    // Se preferisci un layout diverso: usa R.layout.fragment_coupon_shop e duplica l’XML.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.imgCoupon1).setImageResource(R.drawable.coupon_store_bancofrutta)
        view.findViewById<ImageView>(R.id.imgCoupon2).setImageResource(R.drawable.coupon_store_3x1)
        view.findViewById<ImageView>(R.id.imgCoupon3).setImageResource(R.drawable.coupon_store_pasta3x2)


        view.findViewById<View>(R.id.btnBackTop).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }
}
