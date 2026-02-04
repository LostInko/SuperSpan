package com.example.superspan.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superspan.R
import com.example.superspan.model.Address
import com.example.superspan.model.User


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abilita il layout "edge to edge", cioè permette al contenuto
        // di estendersi anche dietro le barre di sistema (status bar e navigation bar)
        enableEdgeToEdge()

        // Imposta il layout grafico da mostrare in questa schermata
        setContentView(R.layout.activity_login)

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
            User("Admin", "Admin", "1234", "Cagliari", "admin", "admin", addresses = mutableListOf(
                Address("Cagliari", "Via del Nastro Azzurro 17", "09131", "Casa Mia", isSelected = true),
                Address("Cagliari", "Via Buxelles 13", "09129", "Casa di Alice", isSelected = false)))
        )
        GlobalData.user_list.add(
            1,
            User("Matteo", "Manai", "22/11/2004", "Quartu Sant'Elena", "m", "m", addresses = mutableListOf(
                Address("Quartu Sant'Elena", "Via Roma 12", "09045", "Casa", isSelected = true)
            ))
        )

        val fromRegister = intent.getBooleanExtra("fromRegisterActivity", false)
        if (fromRegister) {
            Toast.makeText(this, "Registrazione completata con successo", Toast.LENGTH_SHORT).show()
        }

        // --- Bind delle View ---
        val tvRegister = findViewById<TextView>(R.id.tvRegister)  // link "Registrati"
        val etUsername = findViewById<EditText>(R.id.etUsername)  // input username
        val etPassword = findViewById<EditText>(R.id.etPassword)  // input password
        val btnLogin = findViewById<Button>(R.id.btnLogin)      // bottone login
        val tvError = findViewById<TextView>(R.id.tvError)     // messaggio di errore (di solito inizialmente GONE)

        val tvPassDim = findViewById<TextView>(R.id.tvPswDim)

        // Tap su "Registrati" -> vai alla schermata di registrazione
        tvRegister.setOnClickListener {
            register()
        }

        tvPassDim.setOnClickListener {
            passDim()
        }

        // Tap su "Login" -> valida credenziali
        btnLogin.setOnClickListener {
            // Verifica se esiste un utente con username e password uguali a quelli inseriti
            val match = GlobalData.user_list.any {
                it.username == etUsername.text.toString() &&
                        it.password == etPassword.text.toString()
            }

            if (match) {
                GlobalData.currentUser =
                    GlobalData.user_list.find { it.username == etUsername.text.toString() && it.password == etPassword.text.toString() }
                // Credenziali corrette: procedi alla Home
                login()
            } else {
                // Credenziali sbagliate:
                // 1) Mostra il messaggio di errore
                tvError.visibility = TextView.VISIBLE

                // 2) Evidenzia i campi in rosso
                etUsername.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.soft_red)
                etPassword.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.soft_red)

                // 3) Svuota i campi per nuovo tentativo
                //etUsername.text.clear()
                //etPassword.text.clear()
            }
        }

        // Rende il bottone login invisibile fin quando non vengono inseriti campi
        btnLogin.isEnabled = false;
        btnLogin.alpha = 0.6f;

        val textWatcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (etUsername.text.isNotBlank() && etPassword.text.isNotBlank()) {
                    btnLogin.isEnabled = true;
                    btnLogin.alpha = 1f;
                } else {
                    btnLogin.isEnabled = false;
                    btnLogin.alpha = 0.3f;
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // Appena l'utente scrive qualcosa, nascondiamo l'errore!

                // 1. Nascondi la scritta rossa
                tvError.visibility = TextView.INVISIBLE

                // 2. Togli il colore rosso dallo sfondo (resettando a null o bianco)
                etUsername.backgroundTintList = null
                etPassword.backgroundTintList = null

            }

        }

        etUsername.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)


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

    /** Apre la schermata di recupero password. */
    private fun passDim() {
        val intent = Intent(this, PasswordActivity::class.java)
        startActivity(intent)
    }
}
