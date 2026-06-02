package com.example.leitorrecibo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.leitorrecibo.ui.screens.HomeScreen
import com.example.leitorrecibo.ui.screens.NotasSalvas.NotasSalvasScreen
import com.example.leitorrecibo.ui.screens.QrCodeScreen
import com.example.leitorrecibo.ui.screens.Result.ResultScreen
import com.example.leitorrecibo.ui.theme.LeitorReciboTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LeitorReciboTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("home") }
    var extractedData by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        "home" -> HomeScreen(
            onQrClick = { currentScreen = "qr" },
            onVerNotasClick = { currentScreen = "notas_salvas" } // LIGAÇÃO FEITA AQUI
        )
        "qr" -> QrCodeScreen(
            onBack = { currentScreen = "home" },
            onDataExtracted = { data ->
                extractedData = data
                currentScreen = "result"
            }
        )
        "result" -> ResultScreen(
            data = extractedData ?: "Nenhum dado extraído",
            onBack = { currentScreen = "home" }
        )
        // NOVA ROTA PARA O REPOSITÓRIO DE NOTAS
        "notas_salvas" -> NotasSalvasScreen(
            onBack = { currentScreen = "home" }
        )
    }
}