
package com.example.superspan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Schermata di Login.
 * - Permette di inserire username e password
 * - Verifica le credenziali confrontandole con la lista globale (GlobalData.user_list)
 * - Se corrette: apre la HomeActivity; se sbagliate: mostra errore e colora i campi in rosso
 * - Ha un link per andare alla RegisterActivity
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abilita il layout edge-to-edge (contenuti disegnati anche sotto status/nav bar)
        enableEdgeToEdge()

        // Imposta il layout della schermata
        setContentView(R.layout.activity_login)

        // Applica padding al root in base alle system bars, così i contenuti non vengono coperti
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

