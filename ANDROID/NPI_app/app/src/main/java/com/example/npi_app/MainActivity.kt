package com.example.npi_app

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale


class MainActivity : BaseActivity() {

    private lateinit var voiceSearchButton: ImageButton
    private lateinit var speechRecognizer: SpeechRecognizer
    private var speechIntent: Intent? = null
    private val REQUEST_RECORD_AUDIO_PERMISSION = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtén la referencia a la toolbar inferior desde el archivo XML
        val downToolBar: GridLayout = findViewById(R.id.down_toolbar)

        // Crea una instancia de ToolbarInferior para gestionar el comportamiento del toolbar
        DownToolBar(this, downToolBar)

        // Encontramos el botón por su ID
        val docenciaButton: ImageButton = findViewById(R.id.btn_docencia)
        val aulasButton: ImageButton = findViewById(R.id.btn_aulas)
        val tramitesButton: ImageButton = findViewById(R.id.btn_tramites)
        val servextButton: ImageButton = findViewById(R.id.btn_servicios_externos)
        val comedorButton: ImageButton = findViewById(R.id.btn_comedor)
        val profesoradoButton: ImageButton = findViewById(R.id.btn_profesorado)
        val pagosButton: ImageButton = findViewById(R.id.btn_pagos)
        val chatbotButton: ImageButton = findViewById(R.id.btn_chatbot)

        // Establecemos el listener para el clic del botón
        docenciaButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad DocenciaActivity
            val intent = Intent(this@MainActivity, DocenciaActivity::class.java)
            startActivity(intent)
        }

        aulasButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad AulasActivity
            val intent = Intent(this@MainActivity, MapActivity::class.java)
            startActivity(intent)
        }

        tramitesButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad TramitesActivity
            val intent = Intent(this@MainActivity, TramitesActivity::class.java)
            startActivity(intent)
        }

        servextButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad ServExtActivity
            val intent = Intent(this@MainActivity, ServExtActivity::class.java)
            startActivity(intent)
        }

        comedorButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad ComedorActivity
            val intent = Intent(this@MainActivity, ComedorActivity::class.java)
            startActivity(intent)
        }

        profesoradoButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad ProfesoradoActivity
            val intent = Intent(this@MainActivity, ProfesoradoActivity::class.java)
            startActivity(intent)
        }

        pagosButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad EspComActivity
            val intent = Intent(this@MainActivity, WalletEntryActivity::class.java)
            startActivity(intent)
        }

        chatbotButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad ChatBotActivity
            val intent = Intent(this@MainActivity, ChatBotActivity::class.java)
            startActivity(intent)
        }

        // Encontramos el botón por su ID
        val chatButton: ImageButton = findViewById(R.id.btn_chatbot)

        // Establecemos el listener para el clic del botón
        chatButton.setOnClickListener {
            // Crear un Intent para iniciar la actividad LocalizacionActivity
            val intent = Intent(this@MainActivity, ChatBot::class.java)
            startActivity(intent)
        }
        
        // MICRÓFONO

        voiceSearchButton = findViewById(R.id.btn_micro)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)

        voiceSearchButton.setOnClickListener {
            Log.d("Micro", "Microfono tocado")
            checkMicrophonePermission()
        }

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    }

    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        } else {
            startVoiceSearch()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceSearch()
            } else {
                Toast.makeText(this, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startVoiceSearch() {
        if (speechIntent == null) {
            speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            speechIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        }
        speechRecognizer.setRecognitionListener(speechRecognitionListener)
        speechRecognizer.startListening(speechIntent)
    }

    private val speechRecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {
            Log.d("Micro", "Ready for speech")
        }
        override fun onBeginningOfSpeech() {
            Log.d("Micro", "Speech started")
            Toast.makeText(this@MainActivity, "Escuchando...", Toast.LENGTH_SHORT).show()
        }
        override fun onRmsChanged(p0: Float) {
            Log.d("Micro", "RMS energy: $p0")
        }
        override fun onBufferReceived(p0: ByteArray?) {}
        override fun onEndOfSpeech() {
            Log.d("Micro", "Speech ended")
            Toast.makeText(this@MainActivity, "Escucha completada", Toast.LENGTH_SHORT).show()
        }
        override fun onError(p0: Int) {
            Log.d("Micro", "Error: $p0")
        }
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (matches != null && matches.isNotEmpty()) {
                val text = matches[0]
                Log.d("Micro", "Texto reconocido: $text")
                handleVoiceCommand(text)
            }else {
                Log.d("Micro", "No se reconoció ningún texto")
            }
        }

        override fun onPartialResults(p0: Bundle?) {
            TODO("Not yet implemented")
        }

        override fun onEvent(p0: Int, p1: Bundle?) {
            TODO("Not yet implemented")
        }
    }

    private val menuActivities = mapOf(
        "docencia" to DocenciaActivity::class.java,
        "localización" to MapActivity::class.java,
        "trámites" to TramitesActivity::class.java,
        "servicios externos" to ServExtActivity::class.java,
        "comedor" to ComedorActivity::class.java,
        "profesorado" to ProfesoradoActivity::class.java,
        "mis pagos" to WalletEntryActivity::class.java,
        "oye etsit" to ChatBot::class.java, // Opción para el chatbot
        "oye etsi" to ChatBot::class.java, // Opción para el chatbot
        "oye etsy" to ChatBot::class.java, // Opción para el chatbot
        "oye etsyt" to ChatBot::class.java, // Opción para el chatbot
        "oye epsy" to ChatBot::class.java, // Opción para el chatbot
        "oye epsyt" to ChatBot::class.java, // Opción para el chatbot
        "ajustes" to AjustesActivity::class.java
    )

    private fun handleVoiceCommand(command: String) {
        val formattedCommand = command.lowercase(Locale.ROOT)

        Log.d("Micro", "Comando reconocido: $formattedCommand")

        // Buscar la actividad asociada con el comando
        val activityClass = menuActivities[formattedCommand]

        if (activityClass != null) {
            // Si encontramos la actividad, la iniciamos
            Log.d("Micro", "Iniciando actividad para: $formattedCommand")
            val intent = Intent(this, activityClass)
            startActivity(intent)
        } else {
            // Si no encontramos la actividad, mostramos un mensaje de error
            showNotFoundDialog(formattedCommand)
        }
    }

    private fun showNotFoundDialog(option: String) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Opción no encontrada")
            .setMessage("No se encontró la opción \"$option\".")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
    }

}
