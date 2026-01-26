
package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.superspan.R

class CouponOnlineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_coupon_online, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set PNG
        view.findViewById<ImageView>(R.id.imgCoupon1).setImageResource(R.drawable.coupon_online_dolci)
        view.findViewById<ImageView>(R.id.imgCoupon2).setImageResource(R.drawable.coupon_online_gastronomia)
        view.findViewById<ImageView>(R.id.imgCoupon3).setImageResource(R.drawable.coupon_online_frutta)


        view.findViewById<View>(R.id.btnBackTop).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }
}
