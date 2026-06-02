package com.example.leitorrecibo.ui.screens

import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.leitorrecibo.data.local.database.AppDatabase
import com.example.leitorrecibo.domain.models.Produto
import com.example.leitorrecibo.utils.extrairDadosDoHtml
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(data: String, onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var listaProdutos by remember { mutableStateOf<List<Produto>>(emptyList()) }
    var dadosSalvos by remember { mutableStateOf(false) }

    val urlSegura = data.replace("http://", "https://").replace("|", "%7C")

    // 1. PACIÊNCIA EXTREMA: Espera até 60 segundos antes de desistir do site da SEFAZ
    LaunchedEffect(Unit) {
        delay(60000)
        if (isLoading) {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dados da Nota", color = Color.White) },
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
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = data,
                color = Color(0xFF1976D2),
                textDecoration = TextDecoration.Underline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable {
                    try { uriHandler.openUri(data) } catch (e: Exception) { }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Produtos Extraídos:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF1A237E))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("A ligar aos servidores da SEFAZ...", color = Color.Gray)
                        Text("Isto pode demorar um pouco.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }

                // O NAVEGADOR INVISÍVEL (Agora com 1 pixel transparente para o Android não o adormecer)
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                            webViewClient = object : WebViewClient() {
                                var iniciouVerificacao = false

                                override fun onPageFinished(view: WebView, url: String?) {
                                    if (iniciouVerificacao) return
                                    iniciouVerificacao = true

                                    val handler = Handler(Looper.getMainLooper())

                                    val runnable = object : Runnable {
                                        override fun run() {
                                            // Só para de procurar quando o ecrã sair do estado de Loading
                                            if (!isLoading) return

                                            view.evaluateJavascript(
                                                "(function() { return document.documentElement.outerHTML; })();"
                                            ) { htmlResult ->
                                                if (htmlResult != null && htmlResult != "null") {
                                                    val cleanHtml = htmlResult.drop(1).dropLast(1)
                                                        .replace("\\u003C", "<")
                                                        .replace("\\\"", "\"")

                                                    // ANALISA DIRETAMENTE EM SEGUNDO PLANO
                                                    coroutineScope.launch {
                                                        val produtosEncontrados = extrairDadosDoHtml(cleanHtml)

                                                        if (produtosEncontrados.isNotEmpty()) {
                                                            listaProdutos = produtosEncontrados
                                                            isLoading = false // Para a bolinha!

                                                            if (!dadosSalvos) {
                                                                dadosSalvos = true
                                                                val banco = AppDatabase.getDatabase(context)
                                                                banco.produtoDao().inserirVarios(produtosEncontrados)
                                                                println("💾 DADOS SALVOS COM SUCESSO NO ROOM!")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            // Tenta novamente a cada 2 segundos, SEM LIMITE de tentativas!
                                            handler.postDelayed(this, 2000)
                                        }
                                    }
                                    handler.postDelayed(runnable, 2000)
                                }
                            }
                            loadUrl(urlSegura)
                        }
                    },
                    modifier = Modifier.size(1.dp).alpha(0f) // Truque do 1 pixel transparente!
                )

            } else {
                if (listaProdutos.isEmpty()) {
                    Text("Nenhum produto encontrado. A nota expirou ou a Sefaz não respondeu a tempo.", color = Color.Gray)
                } else {
                    LazyColumn {
                        items(listaProdutos) { produto ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = produto.nome,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 12.dp)
                                )
                                Text(
                                    text = produto.preco,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF388E3C)
                                )
                            }
                            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}