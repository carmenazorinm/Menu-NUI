package com.example.npi_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
            if (validateCredentials()) {
                DownToolBar.login = true // Cambiamos el valor de la variable estática
                val resultIntent = Intent(this, MainActivity::class.java)
                startActivity(resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Error: usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateCredentials(): Boolean {
        // Lógica para comprobar usuario y contraseña
        val username = findViewById<EditText>(R.id.et_username).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()
        return username == "user" && password == "1234"
    }
}

