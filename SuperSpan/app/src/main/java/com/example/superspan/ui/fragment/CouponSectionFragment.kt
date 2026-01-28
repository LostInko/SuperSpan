package com.example.superspan.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.R
import com.example.superspan.viewmodel.HomeViewModel

class CouponSectionFragment : Fragment() {

    private lateinit var vm: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_coupon, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnOnline)?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponOnlineFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<View>(R.id.btnShop)?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponShopFragment())
                .addToBackStack(null)
                .commit()
        }

        // Setup barra (toggle espansione + X)
        setupExpandableCouponBar(view)

        // OBSERVE: LiveData
        vm.isAnyCouponActivated.observe(viewLifecycleOwner) { activated ->
            val bar = view.findViewById<View>(R.id.couponExpandableBar)
            bar?.visibility = if (activated) View.VISIBLE else View.GONE
            // opzionale: piccola animazione quando compare/scompare
            bar?.animate()?.cancel()
            if (activated) {
                bar?.alpha = 0f
                bar?.animate()?.alpha(1f)?.setDuration(150)?.start()
            } else {
                // niente: è già GONE
            }
        }
        vm.activatedCouponName.observe(viewLifecycleOwner) { name ->
            view.findViewById<TextView>(R.id.couponNameLabel)?.text = name ?: ""
        }

        // Sync immediato
        syncCouponBar(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { syncCouponBar(it) }
    }

    private fun syncCouponBar(root: View) {
        val bar = root.findViewById<View>(R.id.couponExpandableBar)
        val nameTv = root.findViewById<TextView>(R.id.couponNameLabel)

        val activated = vm.isAnyCouponActivated.value == true
        val name = vm.activatedCouponName.value.orEmpty()

        bar?.visibility = if (activated) View.VISIBLE else View.GONE
        nameTv?.text = name
    }

    private fun setupExpandableCouponBar(root: View) {
        val header = root.findViewById<View>(R.id.couponExpandableHeader)
        val content = root.findViewById<View>(R.id.couponExpandedContent)
        val chevron =
            root.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.couponExpandableChevron)
        val close =
            root.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.couponCloseButton)

        header?.setOnClickListener {
            val expanding = content?.visibility != View.VISIBLE
            if (expanding) {
                content?.alpha = 0f
                content?.visibility = View.VISIBLE
                content?.animate()?.alpha(1f)?.setDuration(150)?.start()
                chevron?.setImageResource(R.drawable.ic_chevron_up)
            } else {
                content?.animate()?.alpha(0f)?.setDuration(120)?.withEndAction {
                    content?.visibility = View.GONE
                    chevron?.setImageResource(R.drawable.ic_chevron_down)
                }?.start()
            }
        }

        close?.setOnClickListener {
            vm.clearActivatedCoupon()
            content?.visibility = View.GONE
            chevron?.setImageResource(R.drawable.ic_chevron_down)
            // niente scroll: la barra è sempre in fondo
        }
    }
}
