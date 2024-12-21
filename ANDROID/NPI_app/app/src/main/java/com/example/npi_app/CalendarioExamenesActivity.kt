package com.example.npi_app

import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar


class CalendarioExamenesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendario_examenes) // Vincula el archivo XML

        val webView: WebView = findViewById(R.id.webview_pdf)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        // Configuración del WebView
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // Muestra la ProgressBar mientras el PDF se está cargando
                if (newProgress < 100) {
                    progressBar.visibility = ProgressBar.VISIBLE
                } else {
                    progressBar.visibility = ProgressBar.GONE
                }
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true) // Habilitar el zoom si es necesario
        webView.settings.builtInZoomControls = true // Habilitar los controles de zoom
        try {
            // Intentamos cargar el URL del PDF
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=https://etsiit.ugr.es/sites/centros/etsiit/public/inline-files/CalendarioExamenes24-25-GII.pdf")

            Log.d("Calendar", "Calendario listo")
        } catch (e: Exception) {
            // Captura cualquier error y lo loguea
            Log.d("Calendar", "Error al cargar el PDF: ${e.message}", e)
        }
    }
}