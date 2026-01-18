
package com.example.superspan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Oggetto globale usato per memorizzare informazioni condivise nell'app.
 * In questo caso contiene una lista di utenti (che simula un "database" in memoria).
 *
 * ATTENZIONE:
 * Questa soluzione va bene per test e piccole app.
 * In una app vera è meglio usare un database o un ViewModel persistente.
 */
object GlobalData {
    // Lista di utenti memorizzata in RAM (si resetta se l'app viene completamente chiusa)
    var user_list = mutableListOf<User>()
}

/**
 * Activity di avvio dell'app (launcher activity).
 * Mostra il layout principale con un pulsante "Start" che porta alla schermata di login.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abilita il layout "edge to edge", cioè permette al contenuto
        // di estendersi anche dietro le barre di sistema (status bar e navigation bar)
        enableEdgeToEdge()

        // Imposta il layout grafico da mostrare in questa schermata
        setContentView(R.layout.activity_main)

        // Gestisce i padding in base all'altezza della status bar / navigation bar,
        // così gli elementi in alto o in basso non vengono coperti.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Applica il padding corretto automaticamente
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Aggiunge un utente "Admin" alla lista globale degli utenti.
        // Lo metti in posizione 0 per essere facilmente accessibile.
        // Questo serve al test iniziale per avere un utente già registrato.
        GlobalData.user_list.add(
            0,
            User("Admin", "Admin", "1234", "admin", "admin")
        )

        // Recupera il pulsante "Start" dal layout
        val btnStart = findViewById<Button>(R.id.btnStart)

        // Quando premi il pulsante, vai alla schermata di Login
        btnStart.setOnClickListener {
            login()
        }
    }

    /**
     * Avvia la LoginActivity tramite un Intent esplicito.
     * Con startActivity() l'app cambia schermata.
     */
    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
