package com.example.superspan.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superspan.R
import android.widget.Toast


class PasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ivBack = findViewById<ImageView>(R.id.btnBackTop)

        // Icona indietro -> torna alla LoginActivity
        ivBack.setOnClickListener {
            back()
        }

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordConf = findViewById<EditText>(R.id.etPasswordConf)
        val btnAvanti = findViewById<Button>(R.id.btnAvanti)


        // Click su "Avanti" (Conferma cambio password)
        btnAvanti.setOnClickListener {
            var invalid = false
            val usernameInput = etUsername.text.toString()
            val newPass = etPassword.text.toString()
            val confPass = etPasswordConf.text.toString()

            // 1) Cerca l'utente nella lista (LOGICA INVERSA RISPETTO ALLA REGISTRAZIONE)
            // .find restituisce l'oggetto User se lo trova, oppure null se non esiste.
            val targetUser = GlobalData.user_list.find { it.username == usernameInput }

            if (targetUser == null) {
                // Se targetUser è null, l'username NON ESISTE -> Errore
                etUsername.error = "Nessun utente trovato con questo username"
                etUsername.backgroundTintList = ContextCompat.getColorStateList(this, R.color.soft_red)
                invalid = true
            } else {
                // Se l'utente esiste, togliamo eventuali segni rossi precedenti sull'username
                etUsername.backgroundTintList = null
            }

            // 2) Password uguali?
            if (newPass != confPass) {
                etPassword.text.clear()
                etPasswordConf.text.clear()
                etPassword.error = "Le password non coincidono"
                etPasswordConf.error = "Le password non coincidono"
                etPassword.backgroundTintList = ContextCompat.getColorStateList(this, R.color.soft_red)
                etPasswordConf.backgroundTintList = ContextCompat.getColorStateList(this, R.color.soft_red)
                invalid = true
            }

            // 3) Password lunghezza minima?
            if (newPass.length < 8) {
                // Nota: non pulire il testo qui, è fastidioso per l'utente dover riscrivere tutto se ne manca solo uno
                etPassword.error = "La password deve avere almeno 8 caratteri"
                etPassword.backgroundTintList = ContextCompat.getColorStateList(this, R.color.soft_red)
                invalid = true
            }

            // --- SE TUTTO È VALIDO ---
            if (!invalid && targetUser != null) {
                // 4) AGGIORNA LA PASSWORD REALE
                targetUser.password = newPass

                // Mostra messaggio di successo
                Toast.makeText(this, "Password aggiornata con successo!", Toast.LENGTH_SHORT).show()

                // 5) Torna alla schermata di Login
                val intent = Intent(this, MainActivity::class.java)
                // Questo flag pulisce la storia delle schermate precedenti (così se premi indietro non torni qui)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        // --- LOGICA DISABILITAZIONE BOTTONE "AVANTI" ---
        // 1. Stato iniziale: bottone disabilitato e trasparente
        btnAvanti.isEnabled = false
        btnAvanti.alpha = 0.6f

        // 2. Funzione che controlla se i 3 campi sono compilati
        fun checkValidation() {
            val areFieldsFilled = etUsername.text.isNotBlank() &&
                    etPassword.text.isNotBlank() &&
                    etPasswordConf.text.isNotBlank()

            // Se username, password e conferma sono pieni -> Attiva bottone
            if (areFieldsFilled) {
                btnAvanti.isEnabled = true
                btnAvanti.alpha = 1f
            } else {
                btnAvanti.isEnabled = false
                btnAvanti.alpha = 0.6f
            }
        }

        // 3. Funzione per "ascoltare" cosa scrive l'utente
        fun setupWatcher(editText: EditText) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    // A. Toglie il rosso (errore) appena l'utente scrive
                    editText.backgroundTintList = null

                    // B. Controlla se attivare il bottone
                    checkValidation()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        // 4. Applica il controllo ai 3 campi del recupero password
        setupWatcher(etUsername)
        setupWatcher(etPassword)
        setupWatcher(etPasswordConf)


    }

    fun back() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}