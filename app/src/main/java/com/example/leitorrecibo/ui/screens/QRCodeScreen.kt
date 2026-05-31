package com.example.leitorrecibo.ui.screens

import androidx.compose.ui.graphics.Color

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.leitorrecibo.domain.extractors.QRCodeExtractor
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(
    onBack: () -> Unit,
    onDataExtracted: (String) -> Unit
) {
    val context = LocalContext.current
    var isProcessing by remember { mutableStateOf(false) }
    var currentImageUri by remember { mutableStateOf<Uri?>(null) } // Guardamos a URI da imagem aqui

    // Lançador para abrir a galeria e selecionar a foto
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        currentImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ler QR Code", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Área da Imagem
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (currentImageUri != null) {
                    AsyncImage(
                        model = currentImageUri,
                        contentDescription = "Imagem selecionada",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma imagem selecionada.\nSelecione o print ou foto do QR Code.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isProcessing) {
                CircularProgressIndicator(color = Color(0xFF1A237E))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Analisando QR Code com ML Kit...", color = Color.Gray)
            } else {
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3949AB))
                ) {
                    Text("Selecionar Imagem da Galeria")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        currentImageUri?.let { uri ->
                            isProcessing = true

                            QRCodeExtractor(context, uri) { urlEncontrada ->
                                isProcessing = false
                                if (urlEncontrada != null) {
                                    onDataExtracted(urlEncontrada)
                                } else {
                                    // ADICIONAMOS O TOAST AQUI PARA O USUÁRIO VER O ERRO
                                    android.widget.Toast.makeText(
                                        context,
                                        "Nenhum QR Code válido encontrado nesta imagem.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    enabled = currentImageUri != null,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Processar Imagem")
                }
            }
        }
    }
}