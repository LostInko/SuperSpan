
package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.viewmodel.HomeViewModel
import androidx.fragment.app.Fragment
import com.example.superspan.R

class CouponShopFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_coupon_shop, container, false) // <-- fix qui

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.imgCoupon1).setImageResource(R.drawable.coupon_store_bancofrutta)
        view.findViewById<ImageView>(R.id.imgCoupon2).setImageResource(R.drawable.coupon_store_3x1)
        view.findViewById<ImageView>(R.id.imgCoupon3).setImageResource(R.drawable.coupon_store_pasta3x2)

        view.findViewById<View>(R.id.btnBackTop).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        view.findViewById<ImageView>(R.id.imgCoupon2).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponThreeForOneFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // Barra espandibile
        setupExpandableCouponBar(view)

        val vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        vm.isAnyCouponActivated.observe(viewLifecycleOwner) { activated ->
            view.findViewById<View>(R.id.couponExpandableBar).visibility =
                if (activated) View.VISIBLE else View.GONE
        }


        val slotTwo = view.findViewById<ImageView>(R.id.imgCoupon2)
        slotTwo.setOnClickListener {
            val activeName = vm.activatedCouponName.value.orEmpty()
            val threeForOneName = "3×1 • Cura personale" // usa esattamente lo stesso testo che attivi nel ViewModel

            if (activeName.equals(threeForOneName, ignoreCase = false)) {
                showAlreadyActiveThreeForOneToast()
                return@setOnClickListener
            }

            // Se non è attivo quel coupon, procedi con la navigazione al fragment 3x1
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponThreeForOneFragment.newInstance())
                .addToBackStack(null)
                .commit()
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
    private fun showAlreadyActiveThreeForOneToast() {
        view?.performHapticFeedback(android.view.HapticFeedbackConstants.REJECT)
        Toast.makeText(requireContext(), "Coupon 3×1 già attivo", Toast.LENGTH_SHORT).show()
    }

}
