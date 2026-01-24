package com.example.superspan.ui.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superspan.R
import com.example.superspan.model.User
import java.util.Calendar

/**
 * Schermata di Registrazione.
 * - Valida i campi (password uguali, lunghezza minima, username unico, TOS accettati)
 * - Permette di selezionare la data di nascita con DatePicker
 * - Se la registrazione va a buon fine, crea un nuovo User e torna alla LoginActivity.
 */

/**
 * Oggetto globale usato per memorizzare informazioni condivise nell'app.
 * In questo caso contiene una lista di utenti (che simula un "database" in memoria).
 *//**
 * Oggetto globale usato per memorizzare informazioni condivise nell'app.
 * In questo caso contiene una lista di utenti (che simula un "database" in memoria).
 */
object GlobalData {
    // Lista di utenti memorizzata in RAM (si resetta se l'app viene completamente chiusa)
    var user_list = mutableListOf<User>()
    var currentUser: User? = null
}
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
        val etcitta = findViewById<EditText>(R.id.etcitta)


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
                etPassword.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.soft_red)
                etPasswordConf.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.soft_red)
                invalid = true
            }

            // 2) Password lunghezza minima?
            if (etPassword.text.length < 8) {
                etPassword.text.clear()
                etPassword.error = "La password deve avere almeno 8 caratteri"
                etPasswordConf.text.clear()
                etPassword.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.soft_red)
                invalid = true
            }

            // 3) Username unico? (controllo sulla lista globale)
            if (GlobalData.user_list.any { it.username == etUsername.text.toString() }) {
                etUsername.text.clear()
                etUsername.error = "Username già esistente"
                // 2) Evidenzia i campi in rosso
                etUsername.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.soft_red)
                invalid = true
            }


            // Se tutti i controlli sono ok -> crea User e termina registrazione
            if (!invalid) {
                val newUser = User(
                    etName.text.toString(),
                    etSurname.text.toString(),
                    etDate.text.toString(),
                    etcitta.text.toString(),// qui usi la data come terzo campo del tuo model
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

        btnRegister.isEnabled = false;
        btnRegister.alpha = 0.6f;

        //Creiamo funziona che controlla se tutto è valido
        fun checkValidation() {
            val areFieldsFilled = etUsername.text.isNotBlank() &&
                    etPassword.text.isNotBlank() &&
                    etName.text.isNotBlank() &&
                    etDate.text.isNotBlank() &&
                    etcitta.text.isNotBlank() &&
                    etSurname.text.isNotBlank() &&
                    etPasswordConf.text.isNotBlank()

            val isTosAccepted = checkBox.isChecked

            // Il bottone si attiva SOLO se i campi sono pieni E la checkbox è spuntata
            if (areFieldsFilled && isTosAccepted) {
                btnRegister.isEnabled = true
                btnRegister.alpha = 1f
            } else {
                btnRegister.isEnabled = false
                btnRegister.alpha = 0.6f // Mantiene il colore più scuro anche da disabilitato
            }
        }

        // Funzione semplice per aggiungere il controllo a un campo
        fun setupWatcher(editText: EditText) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // A. Appena scrivi, togli la tinta rossa (torna al colore originale)
                    editText.backgroundTintList = null

                    // B. Controlla se abilitare il bottone (la tua funzione esistente)
                    checkValidation()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

    // Ora applica questa funzione a tutti i tuoi campi
        setupWatcher(etUsername)
        setupWatcher(etPassword)
        setupWatcher(etDate)
        setupWatcher(etName)
        setupWatcher(etSurname)
        setupWatcher(etcitta)
        setupWatcher(etPasswordConf)
        checkBox.setOnCheckedChangeListener { _, _ ->
            checkValidation()
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


