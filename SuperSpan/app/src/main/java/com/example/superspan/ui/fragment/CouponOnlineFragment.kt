
package com.example.superspan.ui.fragment

import android.widget.TextView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.superspan.viewmodel.HomeViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.R

class CouponOnlineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_coupon_online, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.imgCoupon1).setImageResource(R.drawable.coupon_online_dolci)
        view.findViewById<ImageView>(R.id.imgCoupon2).setImageResource(R.drawable.coupon_online_gastronomia)
        view.findViewById<ImageView>(R.id.imgCoupon3).setImageResource(R.drawable.coupon_online_frutta)

        view.findViewById<View>(R.id.btnBackTop).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Barra espandibile
        setupExpandableCouponBar(view)

        val vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        vm.isAnyCouponActivated.observe(viewLifecycleOwner) { activated ->
            view.findViewById<View>(R.id.couponExpandableBar).visibility =
                if (activated) View.VISIBLE else View.GONE
        }

        vm.activatedCouponName.observe(viewLifecycleOwner) { name ->
            view.findViewById<TextView>(R.id.couponNameLabel)?.text = name ?: ""
        }

    }
    private fun setupExpandableCouponBar(root: View) {
        val bar = root.findViewById<View>(R.id.couponExpandableBar)
        val header = root.findViewById<View>(R.id.couponExpandableHeader)
        val content = root.findViewById<View>(R.id.couponExpandedContent)
        val chevron = root.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.couponExpandableChevron)

        header.setOnClickListener {
            val expanding = content.visibility != View.VISIBLE
            // Toggle contenuto (fade)
            if (expanding) {
                content.alpha = 0f
                content.visibility = View.VISIBLE
                content.animate().alpha(1f).setDuration(150).start()
                chevron.setImageResource(R.drawable.ic_chevron_up)
            } else {
                content.animate().alpha(0f).setDuration(120).withEndAction {
                    content.visibility = View.GONE
                    chevron.setImageResource(R.drawable.ic_chevron_down)
                }.start()
            }
        }
    }
}
