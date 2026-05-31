package com.example.leitorrecibo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.text.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(data: String, onBack: () -> Unit) {
    // Ferramenta do Compose para abrir URLs no navegador padrão
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("URL Encontrada", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Text("Link do QR Code:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Texto transformado num link clicável
            Text(
                text = data,
                color = Color(0xFF1976D2), // Cor azul clássica de links
                textDecoration = TextDecoration.Underline, // Sublinhado
                modifier = Modifier.clickable {
                    try {
                        // Tenta abrir o link no navegador
                        uriHandler.openUri(data)
                    } catch (e: Exception) {
                        println("Erro ao abrir o link: ${e.message}")
                    }
                }
            )
        }
    }
}