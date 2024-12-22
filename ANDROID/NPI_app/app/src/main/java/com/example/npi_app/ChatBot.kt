package com.example.npi_app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.Theme
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.npi_app.ui.theme.AzulChat
import com.example.npi_app.ui.theme.ChatBotTheme
import com.example.npi_app.ui.theme.MarronETSIIT
import com.example.npi_app.ui.theme.MarronETSIITBanner
import com.example.npi_app.ui.theme.RojoChat
import com.example.npi_app.ui.theme.ThemeColor
import com.example.npi_app.ui.theme.VerdeChat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import java.util.Locale


class ChatBot : BaseActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textToSpeech: TextToSpeech
    private val isListening = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        requestAudioPermission()

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Toast.makeText(this@ChatBot, "Listening...", Toast.LENGTH_SHORT).show()
                }

                @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
                override fun onResults(results: Bundle?) {
                    val spokenText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
                    spokenText?.let {
                        chatViewModel.sendMessage(it)
                    }
                }

                override fun onError(error: Int) {
                    when (error) {
                        SpeechRecognizer.ERROR_NETWORK -> {
                            Toast.makeText(this@ChatBot, "Error de red. Comprueba tu conexión a Internet.", Toast.LENGTH_SHORT).show()
                        }
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                            Toast.makeText(this@ChatBot, "Tiempo de espera agotado para la red.", Toast.LENGTH_SHORT).show()
                        }
                        SpeechRecognizer.ERROR_SERVER -> {
                            Toast.makeText(this@ChatBot, "El servidor de reconocimiento no está disponible.", Toast.LENGTH_SHORT).show()
                        }
                        SpeechRecognizer.ERROR_NO_MATCH -> {
                            Toast.makeText(this@ChatBot, "No se reconoció ningún texto. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                        }
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                            Toast.makeText(this@ChatBot, "No se concedieron los permisos necesarios.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@ChatBot, "Error desconocido: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }


                override fun onBeginningOfSpeech() {}
                override fun onEndOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        setContent {
            ChatBotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CharPage(modifier = Modifier.padding(innerPadding),chatViewModel,
                        onMicClick = {
                            toggleSpeechRecognition()
                        })
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestAudioPermission() {
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    private fun toggleSpeechRecognition() {
        if (isListening.value) {
            speechRecognizer.stopListening()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...")
            }
            speechRecognizer.startListening(intent)
        }
        isListening.value = !isListening.value
    }
}

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>(MessageModel("Hola, ¿en qué puedo ayudarte?", "model"))
    }

    @SuppressLint("SecretInSource")
    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Constants.apiKey
    )

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(question : String) {
        viewModelScope.launch {

            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }.toList()
                )

                if (chat == null) {
                    messageList.add(MessageModel("Error al iniciar el modelo generativo.", "model"))
                    return@launch
                }

                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Pensando...","model"))

                val response = chat.sendMessage("Estudio informática en la Escuela Técnica Superior de Ingenierías Informática y de Telecomunicación de la Universidad de Granada. Tengo una pregunta con la universidad: " + question)
                if (response == null) {
                    messageList.add(MessageModel("Error al obtener una respuesta del modelo generativo.", "model"))
                    Log.e("ChatViewModel", "Error al obtener una respuesta del modelo generativo.")
                    return@launch
                } else {
                    Log.d("ChatViewModel", "Respuesta del modelo generativo: ${response.text}")
                }
                if (messageList.isNotEmpty()) {
                    messageList.removeLast()
                }
                messageList.add(MessageModel(response.text.toString(), "model"))
            } catch (e : Exception) {
                if (messageList.isNotEmpty()) {
                    messageList.removeLast()
                }
                messageList.add(MessageModel("Lo siento, ha sucedido algún error. Comprueba tu conexión a internet.", "model"))
            }
        }
    }
}

data class MessageModel(
    val message : String,
    val role : String
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CharPage(modifier: Modifier = Modifier, viewModel: ChatViewModel, onMicClick: () -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.logougr),
            contentDescription = "Logo",
            tint = ThemeColor
        )
    }

    Column(
        modifier = modifier
    ) {
        AppHeader()
        MessageList(modifier = Modifier.weight(1f), messageList = viewModel.messageList)
        MessageInput(
            onMessageSend = {
                viewModel.sendMessage(it)
            },
            onMicClick = onMicClick
        )
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList : List<MessageModel>) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true
    ) {
        items(messageList.reversed()) {
            MessageRow(messageModel = it)
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role=="model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if (isModel) ThemeColor else RojoChat)
                    .padding(16.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = messageModel.message,
                        fontWeight = FontWeight.ExtraLight,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MessageInput(onMessageSend : (String)-> Unit, onMicClick: () -> Unit) {

    var message by remember {
        mutableStateOf("")
    }

    Row (
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMicClick) {
            Icon(
                painter = painterResource(id = R.drawable.microfono),
                contentDescription = "Microphone"
            )
        }
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = {
                message = it
            }
        )
        IconButton(onClick = {
            if (message.isNotEmpty()) {
                onMessageSend(  message)
                message = ""
            }
        }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send"
            )
        }
    }
}

@Composable
fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ThemeColor)
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "OyeETSIIT",
            color = Color.White,
            fontSize = 22.sp
        )
    }
}