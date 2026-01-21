
package com.example.superspan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Activity principale che mostra una bottom bar "curva" animata.
 * La gobba (onda) si sposta sotto l'elemento selezionato e il relativo bottone
 * viene evidenziato con scaling, sollevamento e colori invertiti.
 */
class HomeActivity : AppCompatActivity() {

    // Riferimenti a UI
    private lateinit var curvedBar: CurvedBottomBarView

    private lateinit var btnHome: LinearLayout
    private lateinit var btnProd: LinearLayout
    private lateinit var btnCoup: LinearLayout
    private lateinit var btnProfile: LinearLayout

    // Colori usati per stato attivo/inattivo (lazy: calcolati al primo accesso)
    private val green by lazy { ContextCompat.getColor(this, R.color.greenIcon_test) }
    private val white by lazy { ContextCompat.getColor(this, R.color.white) }

    // Parametri per le animazioni e aspetto degli item
    private val scaleActive = 1.22f     // icona più grande quando attiva
    private val scaleInactive = 1.0f    // icona a grandezza normale quando inattiva
    private val liftDp = 16f            // sollevamento verticale dell'item attivo (in dp)
    private val animDuration = 220L     // durata animazioni in millisecondi

    // Utility: converte dp -> px, usando la densità dello schermo
    private fun dp(v: Float) = v * resources.displayMetrics.density

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abilita il layout edge-to-edge (contenuti sotto status/navigation bar, con insets gestiti a mano)
        enableEdgeToEdge()

        // Layout dell'activity
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Applica padding top/left/right in base alle system bars (status/navigation)
        // Per evitare che i contenuti finiscano "sotto" le barre di sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, 0) // niente padding bottom, la bottom bar è parte del layout
            insets
        }

        // Bind dei componenti principali
        curvedBar = findViewById(R.id.curved_bar)

        btnHome = findViewById(R.id.btn_home)
        btnProd = findViewById(R.id.btn_prod)
        btnCoup = findViewById(R.id.btn_coup)
        btnProfile = findViewById(R.id.btn_profile)

        val btnFav = findViewById<ImageView>(R.id.btn_fav)
        val btnCart = findViewById<ImageView>(R.id.btn_cart)

        // Posiziona la gobba sotto il bottone HOME


        // Stato iniziale: posiziona la gobba sotto il bottone HOME
        // .post() rimanda l'operazione a dopo il layout, così le dimensioni/posizioni sono già calcolate.
        curvedBar.post {
            curvedBar.snapTo(centerXRelativeToBar(btnHome))
        }

        // Seleziona HOME come attiva (senza animare per evitare "salti" al primo frame)
        select(btnHome, animate = false)

        // Listener pulsanti bottom bar: sposta la gobba, marca selezione e aggiorna contenuto/testo
        btnHome.setOnClickListener {
            moveWaveTo(btnHome)                // anima la gobba verso HOME
            select(btnHome)
            home()
        }
        btnFav.setOnClickListener {
            favourite()
        }
        btnCart.setOnClickListener {
        }
        btnProfile.setOnClickListener {
            moveWaveTo(btnProfile)
            select(btnProfile)
        }
        btnProd.setOnClickListener {
            moveWaveTo(btnProd)
            select(btnProd)
        }
        btnCoup.setOnClickListener {
            moveWaveTo(btnCoup)
            select(btnCoup)
        }

    }

    private fun favourite() {
        // Creiamo un'istanza del fragment
        val fragment = FavouriteFragment()

        // Lo inseriamo nel contenitore dell'Activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // 'fragment_container' è l'ID nel tuo XML
            .addToBackStack(null) // Opzionale: permette di tornare indietro con il tasto back
            .commit()
    }

    private fun home() {
        // Creiamo un'istanza del fragment
        val fragment = HomeFragment()

        // Lo inseriamo nel contenitore dell'Activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // 'fragment_container' è l'ID nel tuo XML
            .addToBackStack(null) // Opzionale: permette di tornare indietro con il tasto back
            .commit()
    }


    /**
     * Anima la curvatura (onda/gobba) della bottom bar verso il centro X del target.
     */
    private fun moveWaveTo(target: View) {
        curvedBar.animateTo(centerXRelativeToBar(target), duration = animDuration)
    }

    /**
     * Calcola la coordinata X del centro del target, relativa alla curvatura della barra.
     * (Serve per dire alla barra "mettiti sotto questo bottone".)
     */
    private fun centerXRelativeToBar(v: View): Float {
        return (v.x + v.width / 2f) - curvedBar.x
    }

    /**
     * Gestisce la selezione di uno dei 4 gruppi (HOME/FAV/CART/PROFILE):
     * - Prima rimette tutti nello stato inattivo
     * - Poi mette attivo solo il target
     */
    private fun select(target: LinearLayout, animate: Boolean = true) {
        setGroupInactive(btnHome, animate)
        setGroupInactive(btnProd, animate)
        setGroupInactive(btnCoup, animate)
        setGroupInactive(btnProfile, animate)

        setGroupActive(target, animate)
    }

    /**
     * Porta un gruppo (bottone bottom bar) allo stato inattivo:
     * - Colori verdi su icona/testo
     * - Scala icona a 1.0
     * - Riporta il gruppo alla quota base (translationY = 0)
     * - Rimette lo Z a 0 (niente priorità di disegno)
     */
    private fun setGroupInactive(group: LinearLayout, animate: Boolean) {
        tintGroup(group, active = false)

        val icon = group.findViewById<ImageView?>(findIconId(group))
        if (icon != null) {
            if (animate) {
                icon.animate()
                    .scaleX(scaleInactive)
                    .scaleY(scaleInactive)
                    .setDuration(animDuration)
                    .start()
            } else {
                icon.scaleX = scaleInactive
                icon.scaleY = scaleInactive
            }
        }

        if (animate) {
            group.animate()
                .translationY(0f)               // torna giù alla posizione di base
                .setDuration(animDuration)
                .start()
        } else {
            group.translationY = 0f
        }

        group.translationZ = 0f                // torna dietro (sotto all'item attivo e alla curva)
    }

    /**
     * Porta un gruppo allo stato attivo:
     * - Colori bianchi su icona/testo
     * - Scala icona a 1.22
     * - Solleva il gruppo (translationY negativo) per farlo "uscire" dalla curva
     * - Aumenta lo Z così sta sopra agli altri e sopra la curva
     */
    private fun setGroupActive(group: LinearLayout, animate: Boolean) {
        tintGroup(group, active = true)

        val icon = group.findViewById<ImageView?>(findIconId(group))
        if (icon != null) {
            if (animate) {
                icon.animate()
                    .scaleX(scaleActive)
                    .scaleY(scaleActive)
                    .setDuration(animDuration)
                    .start()
            } else {
                icon.scaleX = scaleActive
                icon.scaleY = scaleActive
            }
        }

        val lift = -dp(liftDp)                 // valore negativo = verso l'alto
        if (animate) {
            group.animate()
                .translationY(lift)
                .setDuration(animDuration)
                .start()
        } else {
            group.translationY = lift
        }

        group.translationZ = 12f               // porta visivamente sopra la curva e gli altri item
    }

    /**
     * Applica colori e background al gruppo in base allo stato.
     * - Attivo: icona/testo bianchi + background pillola selezionata
     * - Inattivo: icona/testo verdi + nessun background
     */
    private fun tintGroup(group: LinearLayout, active: Boolean) {
        val colorIcon = if (active) white else green
        val colorText = if (active) white else green

        // Itera tutti i figli del LinearLayout: colora icone e testi
        for (i in 0 until group.childCount) {
            when (val child = group.getChildAt(i)) {
                is ImageView -> child.setColorFilter(colorIcon)
                is TextView -> child.setTextColor(colorText)
            }
        }

        // Applica la "pillola" di selezione solo quando attivo
        group.background = if (active)
            ContextCompat.getDrawable(this, R.drawable.bg_selected_pill_test)
        else null
    }

    /**
     * Dato il LinearLayout del bottone, ritorna l'id della sua icona interna.
     * Serve per trovare rapidamente l'ImageView su cui fare scaling/tint.
     */
    private fun findIconId(group: LinearLayout): Int = when (group.id) {
        R.id.btn_home    -> R.id.ic_home
        R.id.btn_prod     -> R.id.ic_prod
        R.id.btn_coup    -> R.id.ic_coup
        R.id.btn_profile -> R.id.ic_profile
        else -> View.NO_ID
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}
