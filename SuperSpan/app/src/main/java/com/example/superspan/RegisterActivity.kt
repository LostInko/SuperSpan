
package com.example.superspan

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

/**
 * Schermata di Registrazione.
 * - Valida i campi (password uguali, lunghezza minima, username unico, TOS accettati)
 * - Permette di selezionare la data di nascita con DatePicker
 * - Se la registrazione va a buon fine, crea un nuovo User e torna alla LoginActivity.
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()                           // Layout a schermo pieno (edge-to-edge)
        setContentView(R.layout.activity_register)   // Imposta il layout della schermata

        // Applica padding in base a status/navigation bar per non coprire i contenuti
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Bind delle view dal layout ---
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val etName = findViewById<EditText>(R.id.etName)
        val etSurname = findViewById<EditText>(R.id.etSurname)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordConf = findViewById<EditText>(R.id.etPasswordConf)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)     // TOS / privacy
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // Icona indietro -> torna alla LoginActivity
        ivBack.setOnClickListener {
            back()
        }

        // Click su "Registrati"
        btnRegister.setOnClickListener {
            var invalid = false

            // 1) Password uguali?
            if (etPassword.text.toString() != etPasswordConf.text.toString()) {
                etPassword.text.clear()
                etPassword.error = "Le password non coincidono"
                etPasswordConf.text.clear()
                etPasswordConf.error = "Le password non coincidono"
                invalid = true
            }

            // 2) Password lunghezza minima?
            if (etPassword.text.length < 8) {
                etPassword.text.clear()
                etPassword.error = "La password deve avere almeno 8 caratteri"
                etPasswordConf.text.clear()
                invalid = true
            }

            // 3) Username unico? (controllo sulla lista globale)
            if (GlobalData.user_list.any { it.username == etUsername.text.toString() }) {
                etUsername.text.clear()
                etUsername.error = "Username già esistente"
                invalid = true
            }

            // 4) Termini/Privacy accettati?
            if (!checkBox.isChecked) {
                checkBox.error = "Devi accettare i termini di servizio"
                invalid = true
            }

            // Se tutti i controlli sono ok -> crea User e termina registrazione
            if (!invalid) {
                val newUser = User(
                    etName.text.toString(),
                    etSurname.text.toString(),
                    etDate.text.toString(),        // qui usi la data come terzo campo del tuo model
                    etUsername.text.toString(),
                    etPassword.text.toString()
                )
                GlobalData.user_list.add(newUser)
                endRegister() // Torna alla LoginActivity con un extra
            }
        }

        // Tap nel campo data -> apre DatePicker
        etDate.setOnClickListener {
            datePicker(etDate)
        }

        // Tooltip di aiuto (press-and-hold o long-press sul TextView)
        val tvHelp = findViewById<TextView>(R.id.tvHelp)
        TooltipCompat.setTooltipText(tvHelp, "La password deve avere almeno 8 caratteri")
    }

    /**
     * Mostra un DatePicker e scrive la data selezionata nell'EditText passato.
     * - Imposta maxDate = oggi (non puoi selezionare date future)
     */
    fun datePicker(etDate: EditText) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val dialog = DatePickerDialog(
            this,
            { _, y, m, d ->
                // Nota: month è 0-based -> aggiungi 1 per mostrare 1..12
                val selectedDate = "$d/${m + 1}/$y"
                etDate.setText(selectedDate)
            },
            year, month, day
        )

        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    /**
     * Torna alla LoginActivity senza extra (usato per il tasto indietro).
     */
    fun back() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Conclude la registrazione:
     * - Torna alla LoginActivity
     * - Passa un extra per poter mostrare un messaggio "Registrazione completata"
     *   o riempire automaticamente dei campi, se vuoi gestirlo in LoginActivity.
     */
    fun endRegister() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fromRegisterActivity", true)
        startActivity(intent)
    }
}
