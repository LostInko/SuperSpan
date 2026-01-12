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

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val etName = findViewById<EditText>(R.id.etName);
        val etSurname = findViewById<EditText>(R.id.etSurname)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordConf = findViewById<EditText>(R.id.etPasswordConf)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)

        val btnRegister = findViewById<Button>(R.id.btnRegister)

        ivBack.setOnClickListener {
            back()
        }

        btnRegister.setOnClickListener {
            var invalid = false;
            if(etPassword.text.toString() != etPasswordConf.text.toString()){
                etPassword.text.clear();
                etPassword.setError("Le password non coincidono");
                etPasswordConf.text.clear();
                etPasswordConf.setError("Le password non coincidono");
                invalid = true;
            }
            if(etPassword.text.length < 8){
                etPassword.text.clear();
                etPassword.setError("La password deve avere almeno 8 caratteri");
                etPasswordConf.text.clear();
                invalid = true;
            }
            if(GlobalData.user_list.any{it.username == etUsername.text.toString()}){
                etUsername.text.clear();
                etUsername.setError("Username già esistente");
                invalid = true;
            }
            if(!checkBox.isChecked){
                checkBox.setError("Devi accettare i termini di servizio");
                invalid = true;
            }
            if(!invalid){
                var newUser = User(etName.text.toString(), etSurname.text.toString(), etDate.text.toString(), etUsername.text.toString(), etPassword.text.toString());
                GlobalData.user_list.add(newUser);
                endRegister();
            }
        }

        etDate.setOnClickListener(){
            datePicker(etDate)
        }

        var tvHelp = findViewById<TextView>(R.id.tvHelp);
        TooltipCompat.setTooltipText(tvHelp, "La password deve avere almeno 8 caratteri");


    }

    fun datePicker(etDate  : EditText){
        var calendar = Calendar.getInstance();
        val day = calendar.get(Calendar.DAY_OF_MONTH);
        val month = calendar.get(Calendar.MONTH);
        val year = calendar.get(Calendar.YEAR);
        var dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedDate = "$day/${month + 1}/$year"
                etDate.setText(selectedDate)
            }, year, month, day
        );

        dialog.datePicker.maxDate = System.currentTimeMillis();
        dialog.show();
    }

    fun back() {
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent);
    }

    fun endRegister(){
        var intent = Intent(this, LoginActivity::class.java);
        intent.putExtra("fromRegisterActivity", true);
        startActivity(intent);
    }
}