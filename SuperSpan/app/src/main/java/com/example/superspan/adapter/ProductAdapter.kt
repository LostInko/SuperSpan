package com.example.superspan.adapter

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.Product
import kotlin.math.roundToInt

class ProductAdapter(
    private var productList: List<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onCartChanged: () -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Anagrafica
        val imgProduct: AppCompatImageView = itemView.findViewById(R.id.imgProduct)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtDesc: TextView = itemView.findViewById(R.id.txtDesc)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)

        // Barra quantità
        val controlRow: ConstraintLayout = itemView.findViewById(R.id.controlRow)
        val qtyContainer: ConstraintLayout = itemView.findViewById(R.id.qtyContainer)
        val btnPlus: AppCompatImageView = itemView.findViewById(R.id.btnPlus)
        val btnMinus: AppCompatImageView = itemView.findViewById(R.id.btnMinus)
        val txtCount: TextView = itemView.findViewById(R.id.txtCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_product_grid, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.txtTitle.text = product.name
        holder.txtDesc.text = product.description
        holder.txtPrice.text = product.price
        holder.imgProduct.setImageResource(product.imageRes)

        // Gestione dei prezzi
        val txtOldPrice = holder.itemView.findViewById<TextView>(R.id.txtOldPrice)

        if (product.discountPrice != null) {
            holder.txtPrice.text = product.discountPrice
            holder.txtPrice.setTextColor(holder.itemView.context.getColor(R.color.red))

            txtOldPrice.visibility = View.VISIBLE
            txtOldPrice.text = product.price
            txtOldPrice.paintFlags = txtOldPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.txtPrice.text = product.price
            holder.txtPrice.setTextColor(holder.itemView.context.getColor(R.color.black))
            txtOldPrice.visibility = View.GONE
        }

        // dimensioni collassata/espansa
        val collapsedWidth = dpToPx(holder.itemView, 48f)
        // uso tutta la riga come target
        val expandedWidth = holder.controlRow.width.takeIf { it > 0 } ?: dpToPx(holder.itemView, 130f)

        // Stato iniziale coerente con qty
        if (product.qty <= 0) {
            setCollapsed(holder, collapsedWidth, animate = false)
        } else {
            setExpanded(holder, expandedWidth, animate = false)
            holder.txtCount.text = product.qty.toString()
        }

        // click item → dettaglio
        holder.itemView.setOnClickListener { onItemClick(product) }

        // “+” SEMPRE visibile e fermo a destra
        holder.btnPlus.setOnClickListener {
            if (product.qty <= 0) {
                product.qty = 1
                holder.txtCount.text = "1"
                // a click-time il layout è misurato: prendo width reale della riga
                val target = holder.controlRow.width.takeIf { it > 0 } ?: expandedWidth
                setExpanded(holder, target, animate = true)
            } else {
                product.qty++
                holder.txtCount.text = product.qty.toString()
            }
            onCartChanged()
        }

        // “−”
        holder.btnMinus.setOnClickListener {
            if (product.qty > 1) {
                product.qty--
                holder.txtCount.text = product.qty.toString()
                onCartChanged()
                return@setOnClickListener
            }

            // SE È ARRIVATO QUI → qty sta diventando 0
            product.qty = 0
            holder.txtCount.text = "0"

            // SOLO ORA deve chiudersi
            setCollapsed(holder, collapsedWidth, animate = true)

            onCartChanged()
        }

    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }

    // --- Helpers ---

    private fun setCollapsed(holder: ProductViewHolder, targetWidth: Int, animate: Boolean) {
        // nascondi - e contatore
        if (animate) {
            fadeOut(holder.btnMinus)
            fadeOut(holder.txtCount)
            animateWidth(holder.qtyContainer, targetWidth)
        } else {
            holder.btnMinus.visibility = View.GONE
            holder.btnMinus.alpha = 0f
            holder.txtCount.visibility = View.GONE
            holder.txtCount.alpha = 0f
            setWidth(holder.qtyContainer, targetWidth)
        }
    }

    private fun setExpanded(holder: ProductViewHolder, targetWidth: Int, animate: Boolean) {
        // mostra - e contatore
        if (animate) {
            animateWidth(holder.qtyContainer, targetWidth) {
                fadeIn(holder.btnMinus)
                fadeIn(holder.txtCount)
            }
        } else {
            setWidth(holder.qtyContainer, targetWidth)
            holder.btnMinus.visibility = View.VISIBLE
            holder.btnMinus.alpha = 1f
            holder.txtCount.visibility = View.VISIBLE
            holder.txtCount.alpha = 1f
        }
    }

    private fun animateWidth(view: View, targetWidth: Int, endAction: (() -> Unit)? = null) {
        val start = view.width
        if (start == targetWidth) {
            endAction?.invoke(); return
        }
        ValueAnimator.ofInt(start, targetWidth).apply {
            duration = 180
            addUpdateListener { anim ->
                val w = anim.animatedValue as Int
                setWidth(view, w)
            }
            doOnEnd { endAction?.invoke() }
        }.start()
    }

    private fun setWidth(view: View, widthPx: Int) {
        val lp = view.layoutParams as ViewGroup.LayoutParams
        lp.width = widthPx
        view.layoutParams = lp
    }

    private fun fadeIn(view: View) {
        view.visibility = View.VISIBLE
        view.animate().alpha(1f).setDuration(120).start()
    }

    private fun fadeOut(view: View) {
        view.animate().alpha(0f).setDuration(120).withEndAction {
            view.visibility = View.GONE
        }.start()
    }

    private fun dpToPx(v: View, dp: Float): Int =
        (dp * v.resources.displayMetrics.density).roundToInt()

    private inline fun ValueAnimator.doOnEnd(crossinline action: () -> Unit) {
        addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationEnd(animation: android.animation.Animator) { action() }
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })
    }
}
