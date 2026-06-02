package com.example.leitorrecibo.ui.screens.Result

import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leitorrecibo.utils.extrairDadosDoHtml
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    data: String,
    onBack: () -> Unit,
    viewModel: ResultViewModel = viewModel() // Instancia o ViewModel
) {
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()

    // Observa o estado da UI gerido pelo ViewModel
    val uiState by viewModel.uiState.collectAsState()

    val urlSegura = data.replace("http://", "https://").replace("|", "%7C")

    val progressoAnimado by animateFloatAsState(
        targetValue = uiState.progresso,
        animationSpec = tween(durationMillis = 800),
        label = "progress_anim"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dados da Nota", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
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

            if (uiState.isLoading) {
                // UI de Carregamento
                Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(
                            progress = { progressoAnimado },
                            modifier = Modifier.fillMaxWidth(0.8f).height(8.dp),
                            color = Color(0xFF1A237E),
                            trackColor = Color(0xFFE0E0E0),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.mensagemStatus,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${(progressoAnimado * 100).toInt()}%",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // NAVEGADOR INVISÍVEL
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            settings.loadsImagesAutomatically = false
                            settings.blockNetworkImage = true

                            webViewClient = object : WebViewClient() {
                                var iniciouVerificacao = false

                                override fun onPageFinished(view: WebView, url: String?) {
                                    if (iniciouVerificacao) return
                                    iniciouVerificacao = true

                                    val handler = Handler(Looper.getMainLooper())

                                    val runnable = object : Runnable {
                                        override fun run() {
                                            // Se o ViewModel já disse que não está a carregar, para o loop
                                            if (!uiState.isLoading) return

                                            view.evaluateJavascript(
                                                "(function() { return document.documentElement.outerHTML; })();"
                                            ) { htmlResult ->
                                                if (htmlResult != null && htmlResult != "null") {
                                                    val cleanHtml = htmlResult.drop(1).dropLast(1)
                                                        .replace("\\u003C", "<")
                                                        .replace("\\\"", "\"")

                                                    coroutineScope.launch {
                                                        val produtosEncontrados = extrairDadosDoHtml(cleanHtml)
                                                        if (produtosEncontrados.isNotEmpty()) {
                                                            // DELEGA PARA O VIEWMODEL COM A URL
                                                            viewModel.onProdutosExtraidos(produtosEncontrados, urlSegura)
                                                        }
                                                    }
                                                }
                                            }
                                            handler.postDelayed(this, 800)
                                        }
                                    }
                                    handler.postDelayed(runnable, 800)
                                }
                            }
                            loadUrl(urlSegura)
                        }
                    },
                    modifier = Modifier.size(1.dp).alpha(0f)
                )

            } else {
                if (uiState.erroTimeout || uiState.listaProdutos.isEmpty()) {
                    Text("Nenhum produto encontrado. A nota expirou ou a Sefaz não respondeu a tempo.", color = Color.Gray)
                } else {
                    LazyColumn {
                        items(uiState.listaProdutos) { produto ->
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