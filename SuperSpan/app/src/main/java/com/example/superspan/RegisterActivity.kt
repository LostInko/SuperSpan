package com.example.superspan

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        val etName = findViewById<EditText>(R.id.etUsername);
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
            if(!invalid){
                var newUser = User(etName.text.toString(), etSurname.text.toString(), etDate.text.toString(), etUsername.text.toString(), etPassword.text.toString());
                GlobalData.user_list.add(newUser);
                endRegister();
            }
        }

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