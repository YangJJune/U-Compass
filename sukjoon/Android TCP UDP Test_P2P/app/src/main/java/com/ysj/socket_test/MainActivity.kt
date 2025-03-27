package com.ysj.socket_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ysj.socket_test.ui.theme.Socket_testTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        socketConnection()
//        socketConnection_UDP()
        socketConnection_TCP_Client()
        //socketConnection_TCP_Server()
        //socketConnection_UDPServer()
//        socketConnection_UDPClient()
        setContent {
            Socket_testTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Socket_testTheme {
        Greeting("Android")
    }
}

fun socketConnection() {
    CoroutineScope(Dispatchers.IO).launch {
        launch{
            delay(1000)
            Client().openSocket()
        }
        launch{
            Server().openServer()
        }
    }
}

fun socketConnection_UDP() {
    CoroutineScope(Dispatchers.IO).launch {
        launch{
            delay(1000)
            UDPClient().openSocket()
        }
        launch{
            UDPServer().openServer()
        }
    }
}

fun socketConnection_TCP_Server(){
    CoroutineScope(Dispatchers.IO).launch {
        launch{
            Server().openServer()
        }
    }
}

fun socketConnection_TCP_Client(){
    CoroutineScope(Dispatchers.IO).launch {
        launch{
            Client().openSocket()
        }
    }
}

fun socketConnection_UDPServer() {
    CoroutineScope(Dispatchers.IO).launch {
        launch{
            UDPServer().openServer()
        }
    }
}
fun socketConnection_UDPClient() {
    CoroutineScope(Dispatchers.IO).launch {
        launch{
            UDPClient().openSocket()
        }
    }
}
