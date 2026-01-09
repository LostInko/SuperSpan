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

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val etUsername = findViewById<EditText>(R.id.etUsername);
        val etPassword = findViewById<EditText>(R.id.etPassword);
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvError = findViewById<TextView>(R.id.tvError)

        tvRegister.setOnClickListener {
            register()
        }

        btnLogin.setOnClickListener {
            if(com.example.superspan.GlobalData.user_list.any{it.username == etUsername.text.toString() && it.password == etPassword.text.toString()})
                login();
            else{
                tvError.visibility = TextView.VISIBLE;
                etUsername.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red);
                etUsername.text.clear();
                etPassword.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red);
                etPassword.text.clear();
            }
        }

    }

    fun register(){
        val intent = Intent(this, RegisterActivity::class.java);
        startActivity(intent);
    }

    fun login(){
        val intent = Intent(this, HomeActivity::class.java);
        startActivity(intent);
    }
}