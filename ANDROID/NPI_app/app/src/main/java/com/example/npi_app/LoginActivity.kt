package com.example.npi_app

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

        val usernameField = findViewById<EditText>(R.id.et_username)
        val passwordField = findViewById<EditText>(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_login)

        loginButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            if (validateCredentials(username, password)) {
                // Navegar a la siguiente actividad (MainActivity, por ejemplo)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Opcional: cerrar la actividad de login
            } else {
                // Mostrar un mensaje de error
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateCredentials(username: String, password: String): Boolean {
        // Credenciales predeterminadas
        val correctUsername = "user"
        val correctPassword = "1234"

        return username == correctUsername && password == correctPassword
    }
}

