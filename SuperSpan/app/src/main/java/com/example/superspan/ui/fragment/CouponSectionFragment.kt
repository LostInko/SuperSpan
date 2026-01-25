package com.example.superspan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.superspan.R


class CouponSectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_coupon, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnOnline).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponOnlineFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<View>(R.id.btnShop).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponShopFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
