package com.example.leitorrecibo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leitorrecibo.ui.components.ExtractionCard
import com.example.leitorrecibo.ui.components.StatItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Leitor de NF-e",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Notas Fiscais Eletrônicas",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título da seção
            Text(
                text = "Como deseja extrair os dados?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Card 1 - OCR
            ExtractionCard(
                icon = "📸",
                title = "Leitura por OCR",
                description = "Tire uma foto da nota fiscal e extraia os dados automaticamente",
                color = Color(0xFFE3F2FD),
                iconColor = Color(0xFF1976D2),
                onClick = { selectedOption = "OCR" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2 - QR Code
            ExtractionCard(
                icon = "📱",
                title = "Leitura por QR Code",
                description = "Leia o QR Code presente na nota fiscal",
                color = Color(0xFFE8F5E9),
                iconColor = Color(0xFF388E3C),
                onClick = { selectedOption = "QR Code" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 3 - Código de Acesso
            ExtractionCard(
                icon = "🔢",
                title = "Código de Acesso",
                description = "Digite manualmente o código de 44 dígitos da NF-e",
                color = Color(0xFFFFF3E0),
                iconColor = Color(0xFFF57C00),
                onClick = { selectedOption = "Código de Acesso" }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Card de estatísticas (mock)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📊 Resumo das Notas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(number = "0", label = "Notas salvas")
                        StatItem(number = "0", label = "Produtos")
                        StatItem(number = "R$ 0,00", label = "Total gasto")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Futuramente: ver notas salvas */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ver todas as notas", color = Color.White)
                    }
                }
            }

            // Feedback de seleção (só para demonstrar)
            selectedOption?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
                ) {
                    Text(
                        text = "✅ $it selecionado - função em desenvolvimento",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF1A237E)
                    )
                }
            }
        }
    }
}