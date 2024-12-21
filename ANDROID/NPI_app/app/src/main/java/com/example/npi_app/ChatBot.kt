package com.example.npi_app

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import com.example.npi_app.ui.theme.ChatBotTheme
import com.example.npi_app.ui.theme.AzulChat
import com.example.npi_app.ui.theme.MarronETSIIT
import com.example.npi_app.ui.theme.MarronETSIITBanner
import com.example.npi_app.ui.theme.ThemeColor
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatBot : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        setContent {
            ChatBotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CharPage(modifier = Modifier.padding(innerPadding),chatViewModel)
                }
            }
        }
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

                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Pensando...","model"))

                val response = chat.sendMessage("Estudio informática en la Escuela Técnica Superior de Ingenierías Informática y de Telecomunicación de la Universidad de Granada. Tengo una pregunta con la universidad: " + question)
                messageList.removeLast()
                messageList.add(MessageModel(response.text.toString(), "model"))
            } catch (e : Exception) {
                messageList.removeLast()
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
fun CharPage(modifier: Modifier = Modifier, viewModel: ChatViewModel) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.logougr),
            contentDescription = "Logo",
            tint = MarronETSIIT
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
            }
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
                modifier = Modifier.align(if(isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if(isModel) 8.dp else 70.dp,
                        end = if(isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if(isModel) MarronETSIIT else ThemeColor)
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
fun MessageInput(onMessageSend : (String)-> Unit) {

    var message by remember {
        mutableStateOf("")
    }

    Row (
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            .background(MarronETSIITBanner)
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "OyeETSIIT",
            color = Color.White,
            fontSize = 22.sp
        )
    }
}
