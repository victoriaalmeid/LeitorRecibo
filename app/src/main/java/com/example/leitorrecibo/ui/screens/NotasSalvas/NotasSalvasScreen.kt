package com.example.leitorrecibo.ui.screens.NotasSalvas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leitorrecibo.ui.components.NotaFiscalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasSalvasScreen(
    onBack: () -> Unit,
    viewModel: NotasSalvasViewModel = viewModel() // Instancia o ViewModel automaticamente
) {
    // Observa o fluxo de dados do Room
    val notas by viewModel.historicoNotas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Compras", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        }
    ) { padding ->
        if (notas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma nota guardada ainda.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Itera sobre a lista de notas e cria um Card para cada uma
                items(notas) { notaComProdutos ->
                    NotaFiscalCard(notaComProdutos = notaComProdutos)
                }
            }
        }
    }
}