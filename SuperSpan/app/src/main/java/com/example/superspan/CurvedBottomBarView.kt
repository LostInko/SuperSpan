package com.example.superspan

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.max
import kotlin.math.min

/**
 * View custom che disegna una barra con una "gobba" (notch) fluida al centro X specificato.
 * Usata come background animato per una bottom bar con icone "sollevate".
 *
 * Concetti chiave:
 * - notchCenterX: centro orizzontale della gobba (dove si trova il picco).
 * - notchDepth: profondità/altezza della gobba.
 * - notchHalfWidth: mezza larghezza della gobba (larghezza totale = 2x).
 * - ctrlOffsetX: influenza la "morbidezza" delle curve di Bezier.
 * - baselineOffset: quota di base della barra rispetto al bordo della View.
 * - bumpUp: se true la gobba "sale" verso l'alto, altrimenti "scende" verso il basso.
 */
class CurvedBottomBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Pennello per riempire la forma (colore e antialiasing)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.greenNav_test)
    }

    // Path che rappresenta la forma della barra con gobba
    private val path = Path()

    // Coordinata X del centro della gobba (in px dentro questa View)
    private var notchCenterX = 0f

    // Utility: dp -> px
    private fun dp(v: Float) = v * resources.displayMetrics.density

    // ------------------------------
    // Parametri della forma/curva
    // ------------------------------

    // Altezza della gobba (in px, calcolata da dp)
    private var notchDepth = dp(90f)

    // Metà larghezza della gobba (più è grande, più la gobba è "larga")
    private var notchHalfWidth = dp(100f)

    // Offset orizzontale dei punti di controllo Bezier (morbidezza della curva)
    private var ctrlOffsetX = dp(30f)

    // Se true la gobba è rivolta verso l'alto (tipico per una bottom bar)
    var bumpUp: Boolean = true
        set(value) {
            field = value
            invalidate()  // ridisegna quando cambia
        }

    // Altezza "base" della barra (quanto è distante dal bordo top/bottom)
    // Serve a lasciare spazio per l'icona "sollevata".
    private var baselineOffset = dp(22f)

    // ------------------------------
    // API pubbliche di configurazione
    // ------------------------------

    /** Cambia il colore della barra e forza il ridisegno. */
    fun setBarColor(@ColorInt color: Int) {
        paint.color = color
        invalidate()
    }

    /**
     * Consente di aggiornare dimensioni/parametri della gobba in modo dinamico.
     * Ogni parametro è opzionale; se presente, viene convertito da dp a px.
     */
    fun setSizes(
        depthDp: Float? = null,
        halfWidthDp: Float? = null,
        ctrlDp: Float? = null,
        baselineDp: Float? = null
    ) {
        depthDp?.let { notchDepth = dp(it) }
        halfWidthDp?.let { notchHalfWidth = dp(it) }
        ctrlDp?.let { ctrlOffsetX = dp(it) }
        baselineDp?.let { baselineOffset = dp(it) }
        invalidate()
    }

    /**
     * Posiziona istantaneamente la gobba alla X indicata (clamp tra 0 e width).
     * Usa quando vuoi "saltare" direttamente a un tab senza animazione.
     */
    fun snapTo(x: Float) {
        notchCenterX = x.coerceIn(0f, width.toFloat())
        invalidate()
    }

    /**
     * Anima la gobba dalla posizione attuale a targetX.
     * - Interpolatore FastOutSlowIn: parte veloce, rallenta in arrivo (material-like).
     * - Durante l'animazione si invalida la View per ridisegnare frame-by-frame.
     */
    fun animateTo(targetX: Float, duration: Long = 220) {
        val start = notchCenterX
        val end = targetX.coerceIn(0f, width.toFloat())
        ValueAnimator.ofFloat(start, end).apply {
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                notchCenterX = it.animatedValue as Float
                invalidate() // richiama onDraw
            }
        }.start()
    }

    // ------------------------------
    // Disegno della forma
    // ------------------------------

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        // La baseline è la "linea" su cui poggia la barra prima della gobba.
        // Se bumpUp=true, la baseline è spostata verso il basso (offset dall'alto)
        // e il picco sale verso l'alto (peakY < baseY).
        val baseY = if (bumpUp) min(baselineOffset, h) else 0f

        // Calcolo del picco della gobba (verso su o giù) partendo dalla baseline
        val peakY = if (bumpUp) {
            // verso l'alto: peakY è più in alto (valore Y più piccolo)
            max(0f, baseY - notchDepth)
        } else {
            // verso il basso: peakY è più in basso (valore Y più grande)
            min(h, baseY + notchDepth)
        }

        // Bordo sinistro e destro della gobba, centrati su notchCenterX
        val leftEdge = max(0f, notchCenterX - notchHalfWidth)
        val rightEdge = min(w, notchCenterX + notchHalfWidth)

        // Punti di controllo per la cubic Bezier (a metà delle semilarghezze)
        val leftCtrl = max(0f, notchCenterX - notchHalfWidth / 2f)
        val rightCtrl = min(w, notchCenterX + notchHalfWidth / 2f)

        path.reset()
        // Partiamo da sinistra sulla baseline
        path.moveTo(0f, baseY)

        if (notchCenterX <= 0f) {
            // Nessuna gobba visibile (centro fuori o all'inizio): linea piatta
            path.lineTo(w, baseY)
        } else {
            // Linea fino all'inizio della gobba
            path.lineTo(leftEdge, baseY)

            // Prima metà della gobba (sinistra -> picco)
            path.cubicTo(
                leftCtrl, baseY,               // primo control point
                notchCenterX - ctrlOffsetX,    // secondo control point X
                peakY,                         // secondo control point Y (verso il picco)
                notchCenterX, peakY            // punto del picco
            )

            // Seconda metà della gobba (picco -> destra)
            path.cubicTo(
                notchCenterX + ctrlOffsetX,    // primo control point X
                peakY,                         // primo control point Y
                rightCtrl, baseY,              // secondo control + ritorno a baseline
                rightEdge, baseY               // fine gobba
            )

            // Chiude la linea superiore fino al bordo destro
            path.lineTo(w, baseY)
        }

        // Chiude il path verso il fondo della View per poter riempire (fill)
        path.lineTo(w, h)
        path.lineTo(0f, h)
        path.close()

        // Disegna il path riempito
        canvas.drawPath(path, paint)
    }
}
