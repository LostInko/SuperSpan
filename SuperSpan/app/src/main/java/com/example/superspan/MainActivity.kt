
package com.example.superspan

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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


        // --- Bind delle View ---
        val tvRegister = findViewById<TextView>(R.id.tvRegister)  // link "Registrati"
        val etUsername = findViewById<EditText>(R.id.etUsername)  // input username
        val etPassword = findViewById<EditText>(R.id.etPassword)  // input password
        val btnLogin   = findViewById<Button>(R.id.btnLogin)      // bottone login
        val tvError    = findViewById<TextView>(R.id.tvError)     // messaggio di errore (di solito inizialmente GONE)

        // Tap su "Registrati" -> vai alla schermata di registrazione
        tvRegister.setOnClickListener {
            register()
        }

        // Tap su "Login" -> valida credenziali
        btnLogin.setOnClickListener {
            // Verifica se esiste un utente con username e password uguali a quelli inseriti
            val match = GlobalData.user_list.any {
                it.username == etUsername.text.toString() &&
                        it.password == etPassword.text.toString()
            }

            if (match) {
                // Credenziali corrette: procedi alla Home
                login()
            } else {
                // Credenziali sbagliate:
                // 1) Mostra il messaggio di errore
                tvError.visibility = TextView.VISIBLE

                // 2) Evidenzia i campi in rosso
                etUsername.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.red)
                etPassword.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.red)

                // 3) Svuota i campi per nuovo tentativo
                etUsername.text.clear()
                etPassword.text.clear()
            }
        }
    }


    /** Apre la schermata di registrazione. */
    private fun register() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    /** Apre la HomeActivity (login riuscito). */
    private fun login() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
