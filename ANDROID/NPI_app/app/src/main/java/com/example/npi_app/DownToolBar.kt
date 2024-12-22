package com.example.npi_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AlertDialog
import java.util.Locale
import com.example.npi_app.LocaleManager


class DownToolBar(private val context: Context, private val toolbar: GridLayout) : BaseActivity() {

    companion object {
        var login: Boolean = false
    }

    init {
        // Inicializamos los elementos de la toolbar inferior
        val loginButton: ImageButton = toolbar.findViewById(R.id.btn_usuario)
        val btnAjustes: ImageButton = toolbar.findViewById(R.id.btn_ajustes)
        val ic_usuario: ImageView = toolbar.findViewById(R.id.btn_usuario)

        // Actualizamos la imagen del botón según el estado de login
        updateLoginIcon(ic_usuario)

        // Listener para el botón de login
        loginButton.setOnClickListener {
            if (!login) {
                // Navegar a la pantalla de login
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
        }

        // Establecemos el listener para el clic del botón de ajustes
        btnAjustes.setOnClickListener {
            // Llamar al método showLanguageDialog de manera segura
            if (context is Activity) {
                showLanguageDialog(context)
            }
        }
    }

    // Función para actualizar el ícono del botón según el estado de login
    private fun updateLoginIcon(ic_usuario: ImageView) {
        if (login) {
            ic_usuario.setImageResource(R.drawable.ic_login_correcto) // Si pongo la foto de marcelino se ve sólo blanco
        } else {
            ic_usuario.setImageResource(R.drawable.ic_usuario) // Ícono predeterminado
        }
    }

    // Cambiar la implementación de showLanguageDialog para usar el contexto correctamente
    private fun showLanguageDialog(context: Context) {
        val languages = arrayOf("Español", "English")
        val localeCodes = arrayOf("es", "en")
        val localeFlags = arrayOf(R.drawable.ic_flag_spanish, R.drawable.ic_flag_english)

        // Verificamos si el contexto es una instancia de Activity antes de crear el AlertDialog
        if (context is Activity) {
            AlertDialog.Builder(context, R.style.MyDialogTheme)
                .setTitle(R.string.language)
                .setItems(languages) { _, which ->
                    setLocale(localeCodes[which], context)
                }
                .show()
        }
    }

    private fun setLocale(language: String, context: Context) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        // Usamos el contexto proporcionado para obtener los recursos
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Actualizamos el idioma en LocaleManager
        LocaleManager.setLocale(context, locale.language)

        // Aseguramos que la actividad se reinicie en el hilo principal
        if (context is Activity) {
            // Reiniciar la actividad después de aplicar el idioma
            Handler(Looper.getMainLooper()).post {
                context.recreate()  // Reinicia la actividad
            }
        }
    }
}

